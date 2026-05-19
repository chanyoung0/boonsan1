package service.contract;

import db.PayoutDBO;
import enums.CalculationBasis;
import enums.PaymentType;
import model.contract.Payout;

import java.math.BigDecimal;
import java.util.List;

public class PayoutService {

    private static final PayoutDBO payoutDBO = new PayoutDBO();

    public static Payout createPayout(String policyNumber, String processor, PaymentType paymentType,
                                      CalculationBasis calculationBasis, BigDecimal baseAmount,
                                      BigDecimal deductionAmount, String deductionItem) {
        BigDecimal finalPaymentAmount = calculateFinalPaymentAmount(baseAmount, deductionAmount);
        Payout payout = new Payout(processor, paymentType, calculationBasis,
                baseAmount, finalPaymentAmount, deductionItem, null);
        payout.calculatePayment();
        payout.setPayoutId(generatePayoutId());
        payout.setPolicyNumber(policyNumber);
        payout.setStatus("REGISTERED");
        return payoutDBO.save(payout) ? payout : null;
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
        if (payout == null || isCancelled(payout)) {
            return null;
        }
        payout.approvePayment();
        payout.setStatus("APPROVED");
        return payoutDBO.update(payout) ? payout : null;
    }

    public static Payout processPayout(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null || isCancelled(payout) || payout.getApprovedAt() == null) {
            return null;
        }
        payout.processPayment();
        payout.setStatus("PAID");
        return payoutDBO.update(payout) ? payout : null;
    }

    public static Payout cancelPayout(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return null;
        }
        payout.cancelPayment();
        payout.setStatus("CANCELLED");
        return payoutDBO.update(payout) ? payout : null;
    }

    public static List<Payout> getPayoutList() {
        return payoutDBO.findAll();
    }

    public static Payout findPayoutById(String payoutId) {
        return payoutDBO.findById(payoutId);
    }

    public static String getPayoutId(Payout payout) {
        return payout == null ? null : payout.getPayoutId();
    }

    public static String getPolicyNumber(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        return payout == null ? null : payout.getPolicyNumber();
    }

    public static String getPayoutStatus(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return "NOT_FOUND";
        }
        return payout.getStatus();
    }

    public static String createPayoutCalculationSummary(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return "[시스템] 지급금 산출 정보 없음";
        }
        return "[시스템] 지급금 산출 결과"
                + "\n  증권번호: " + payout.getPolicyNumber()
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
                + "\n  반려사유: " + emptyToDefault(rejectionReason);
    }

    public static String createPaymentNotice(String payoutId) {
        Payout payout = findPayoutById(payoutId);
        if (payout == null) {
            return "[시스템] 지급안내장 생성 불가";
        }
        return "[시스템] 지급안내장 자동 발송 완료"
                + "\n  증권번호: " + payout.getPolicyNumber()
                + "\n  지급유형: " + payout.getPaymentType()
                + "\n  지급금액: " + formatAmount(payout.getFinalPaymentAmount())
                + "\n  지급일시: " + payout.getPaidAt()
                + "\n  입금계좌: 현재 계약자 계좌정보 미연동";
    }

    public static String createCancellationMessage(String payoutId, String reason) {
        return "[시스템] 제지급금 취소/반려 사유 기록"
                + "\n  지급번호: " + payoutId
                + "\n  증권번호: " + getPolicyNumber(payoutId)
                + "\n  사유: " + emptyToDefault(reason);
    }

    public static boolean isCancelled(String payoutId) {
        return "CANCELLED".equals(getPayoutStatus(payoutId));
    }

    private static boolean isCancelled(Payout payout) {
        return "CANCELLED".equals(payout.getStatus());
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
