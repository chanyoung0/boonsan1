package service.accident;

import db.SubrogationDBO;
import enums.SubrogationStatus;
import model.accident.InsurancePayment;
import model.accident.Subrogation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public static boolean saveSubrogation(Subrogation subrogation) {
        return subrogationDBO.save(subrogation);
    }

    public static boolean updateSubrogation(Subrogation subrogation) {
        return subrogationDBO.update(subrogation);
    }
}
