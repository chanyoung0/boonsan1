package service.accident;

import db.SubrogationDBO;
import enums.SubrogationStatus;
import model.accident.InsurancePayment;
import model.accident.Subrogation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SubrogationService {

    private static final SubrogationDBO subrogationDBO = new SubrogationDBO();

    public static Subrogation createSubrogation(InsurancePayment payment, String offenderName,
                                                String offenderContact, float faultRatio,
                                                BigDecimal paidAmount, LocalDateTime paymentDeadline,
                                                String depositAccount) {
        BigDecimal subrogationAmount = calculateSubrogationAmount(paidAmount, faultRatio);
        Subrogation subrogation = new Subrogation(
                "SUB-" + System.currentTimeMillis(),
                offenderName,
                offenderContact,
                faultRatio,
                subrogationAmount,
                paymentDeadline,
                depositAccount,
                SubrogationStatus.PENDING
        );

        if (payment != null) {
            payment.setSubrogation(subrogation);
            payment.registerSubrogation();
        }

        return subrogation;
    }

    public static SubrogationStatus sendClaim(Subrogation subrogation) {
        subrogation.generateSubrogationDocument();
        subrogation.sendClaim();
        return subrogation.getSubrogationStatus();
    }

    public static SubrogationStatus confirmDeposit(Subrogation subrogation, String answer) {
        if ("Y".equalsIgnoreCase(answer)) {
            subrogation.confirmDeposit();
        }
        return subrogation.getSubrogationStatus();
    }

    public static BigDecimal calculateSubrogationAmount(BigDecimal paidAmount, float faultRatio) {
        if (paidAmount == null) {
            return BigDecimal.ZERO;
        }
        return paidAmount
                .multiply(BigDecimal.valueOf(faultRatio))
                .divide(BigDecimal.valueOf(100));
    }

    // 구상권 정보를 DB에 저장하고 성공 여부를 반환한다
    public static boolean saveSubrogation(Subrogation subrogation, String paymentId) {
        if (subrogation == null || subrogation.getSubrogationId() == null || paymentId == null) {
            return false;
        }
        subrogation.setPaymentId(paymentId);
        return subrogationDBO.save(subrogation, paymentId,
                subrogation.getSubrogationStatus() == null ? "PENDING" : subrogation.getSubrogationStatus().name());
    }

    public static List<Subrogation> getSubrogationList() {
        return subrogationDBO.findAll();
    }

    public static List<Subrogation> findSubrogationsByPaymentId(String paymentId) {
        return subrogationDBO.findByPaymentId(paymentId);
    }

    public static float parseFaultRatio(String value) {
        if (value == null) {
            return 0F;
        }

        try {
            float ratio = Float.parseFloat(value.replace("%", "").trim());
            if (ratio < 0F) {
                return 0F;
            }
            if (ratio > 100F) {
                return 100F;
            }
            return ratio;
        } catch (NumberFormatException e) {
            return 0F;
        }
    }

    public static LocalDateTime resolvePaymentDeadline(String value) {
        if (value == null || value.trim().isEmpty()) {
            return LocalDateTime.now().plusDays(14);
        }

        try {
            return LocalDate.parse(value.trim()).atStartOfDay();
        } catch (RuntimeException e) {
            return LocalDateTime.now().plusDays(14);
        }
    }
}
