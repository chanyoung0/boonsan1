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
}
