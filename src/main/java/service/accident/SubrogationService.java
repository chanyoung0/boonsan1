package service.accident;

import db.MyBatisSessionFactory;
import db.SubrogationMapper;
import enums.SubrogationStatus;
import model.accident.InsurancePayment;
import model.accident.Subrogation;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubrogationService {

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

    // 구상을 DB에 저장하고 subrogationId를 반환한다
    public static String saveSubrogation(Subrogation subrogation) {
        if (subrogation == null || subrogation.getSubrogationId() == null) return null;
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            int rows = s.getMapper(SubrogationMapper.class).insert(subrogation);
            return rows > 0 ? subrogation.getSubrogationId() : null;
        } catch (Exception e) {
            System.out.println("[DB 오류] 구상 저장 실패: " + e.getMessage());
            return null;
        }
    }

    public static List<Subrogation> findAll() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(SubrogationMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 구상 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static Subrogation findById(String subrogationId) {
        if (subrogationId == null) return null;
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(SubrogationMapper.class).findById(subrogationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 구상 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public static String findStatusById(String subrogationId) {
        if (subrogationId == null) return null;
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(SubrogationMapper.class).findStatusById(subrogationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 구상 상태 조회 실패: " + e.getMessage());
            return null;
        }
    }
}
