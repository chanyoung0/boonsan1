package service.contract;

import enums.CalculationBasis;
import enums.ContractStatus;
import enums.PaymentCycle;
import enums.PaymentType;
import model.contract.Contract;
import model.contract.Payout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PayoutService {

    private static final Map<String, Payout> payoutMap = new LinkedHashMap<>();
    private static final Map<String, Contract> contractMap = new LinkedHashMap<>();
    private static final Map<String, String> payoutPolicyNumberMap = new LinkedHashMap<>();
    private static final Set<String> cancelledPayoutIdSet = new LinkedHashSet<>();

    public static Payout createPayout(String policyNumber, String processor, PaymentType paymentType,
                                      CalculationBasis calculationBasis, BigDecimal baseAmount,
                                      BigDecimal deductionAmount, String deductionItem) {
        BigDecimal finalPaymentAmount = calculateFinalPaymentAmount(baseAmount, deductionAmount);
        Payout payout = new Payout(processor, paymentType, calculationBasis,
                baseAmount, finalPaymentAmount, deductionItem, null);
        payout.calculatePayment();

        String payoutId = generatePayoutId();
        payoutMap.put(payoutId, payout);
        payoutPolicyNumberMap.put(payoutId, policyNumber);
        addPayoutToContract(resolveContract(policyNumber), payout);
        return payout;
    }

    public static BigDecimal calculateFinalPaymentAmount(BigDecimal baseAmount, BigDecimal deductionAmount) {
        BigDecimal base = baseAmount == null ? BigDecimal.ZERO : baseAmount;
        BigDecimal deduction = deductionAmount == null ? BigDecimal.ZERO : deductionAmount;
        return base.subtract(deduction);
    }

    public static boolean isPayableContractStatus(String contractStatus) {
        return "유효".equals(contractStatus) || "만기".equals(contractStatus);
    }

    public static Payout approvePayout(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null || isCancelled(payoutId)) {
            return null;
        }
        payout.approvePayment();
        return payout;
    }

    public static Payout processPayout(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null || isCancelled(payoutId) || payout.getApprovedAt() == null) {
            return null;
        }
        payout.processPayment();
        return payout;
    }

    public static Payout cancelPayout(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return null;
        }
        payout.cancelPayment();
        cancelledPayoutIdSet.add(payoutId);
        return payout;
    }

    public static List<Payout> getPayoutList() {
        return new ArrayList<>(payoutMap.values());
    }

    public static Payout findPayoutById(String payoutId) {
        return payoutMap.get(payoutId);
    }

    public static void addPayoutToContract(Contract contract, Payout payout) {
        if (contract != null && payout != null) {
            contract.getPayoutList().add(payout);
        }
    }

    public static String getPayoutId(Payout payout) {
        for (Map.Entry<String, Payout> entry : payoutMap.entrySet()) {
            if (entry.getValue() == payout) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getPolicyNumber(String payoutId) {
        return payoutPolicyNumberMap.get(payoutId);
    }

    public static String getPayoutStatus(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return "NOT_FOUND";
        }
        if (isCancelled(payoutId)) {
            return "CANCELLED";
        }
        if (payout.getPaidAt() != null) {
            return "PAID";
        }
        if (payout.getApprovedAt() != null) {
            return "APPROVED";
        }
        return "REGISTERED";
    }

    public static String createPayoutCalculationSummary(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return "[시스템] 지급금 산출 정보 없음";
        }
        return "[시스템] 지급금 산출 결과"
                + "\n  증권번호: " + getPolicyNumber(payoutId)
                + "\n  지급유형: " + payout.getPaymentType()
                + "\n  산출금액: " + formatAmount(payout.getCalculatedAmount())
                + "\n  산출기준: " + payout.getCalculationBasis()
                + "\n  공제항목: " + emptyToDefault(payout.getDeductionItem())
                + "\n  최종지급금액: " + formatAmount(payout.getFinalPaymentAmount());
    }

    public static String createPayoutRejectionMessage(String payoutId, String rejectionReason) {
        return "[시스템] 지급금 산출 결과 반려"
                + "\n  지급번호: " + payoutId
                + "\n  증권번호: " + getPolicyNumber(payoutId)
                + "\n  반려사유: " + emptyToDefault(rejectionReason)
                + "\n  안내: 반려 상태 필드가 없어 실제 상태 저장은 보류됩니다.";
    }

    public static String createPaymentNotice(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return "[시스템] 지급안내장 생성 불가";
        }
        return "[시스템] 지급안내장 자동 발송 완료"
                + "\n  증권번호: " + getPolicyNumber(payoutId)
                + "\n  지급유형: " + payout.getPaymentType()
                + "\n  지급금액: " + formatAmount(payout.getFinalPaymentAmount())
                + "\n  지급일시: " + payout.getPaidAt()
                + "\n  입금계좌: 현재 계약자 계좌정보 미연동";
    }

    public static String createCancellationMessage(String payoutId, String reason) {
        return "[시스템] 제지급금 취소/반려 사유 기록"
                + "\n  지급번호: " + payoutId
                + "\n  증권번호: " + getPolicyNumber(payoutId)
                + "\n  사유: " + emptyToDefault(reason)
                + "\n  안내: 취소 사유 저장 필드가 없어 출력 메시지로만 반영됩니다.";
    }

    public static boolean isCancelled(String payoutId) {
        return cancelledPayoutIdSet.contains(payoutId);
    }

    private static Contract resolveContract(String policyNumber) {
        Contract contract = contractMap.get(policyNumber);
        if (contract != null) {
            return contract;
        }

        Contract newContract = new Contract();
        newContract.setPolicyNumber(policyNumber);
        newContract.setContractStatus(ContractStatus.ACTIVE);
        newContract.setPaymentCycle(PaymentCycle.MONTHLY);
        contractMap.put(policyNumber, newContract);
        return newContract;
    }

    private static String generatePayoutId() {
        return "PO-" + System.currentTimeMillis();
    }

    private static String formatAmount(BigDecimal amount) {
        return amount == null ? "0원" : String.format("%,d원", amount.longValue());
    }

    private static String emptyToDefault(String value) {
        return value == null || value.trim().isEmpty() ? "미입력" : value;
    }
}
