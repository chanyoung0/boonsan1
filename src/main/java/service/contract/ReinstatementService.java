package service.contract;

import db.ReinstatementMapper;
import db.MyBatisSessionFactory;
import model.contract.Reinstatement;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 부활 관리 서비스 — 부활 가능 여부 판단, DB 저장 담당
public class ReinstatementService {

    // 계약 상태 기준 부활 가능 여부 판단
    public static boolean canReinstate(String contractStatus) {
        return "실효".equals(contractStatus);
    }

    // 미납 보험료 = 미납 회차 × 회당 보험료
    public static BigDecimal calculateUnpaidPremium(int unpaidInstallments, BigDecimal perInstallmentPremium) {
        if (unpaidInstallments <= 0 || perInstallmentPremium == null) return BigDecimal.ZERO;
        return perInstallmentPremium.multiply(BigDecimal.valueOf(unpaidInstallments));
    }

    // 부활 처리 결과를 DB에 저장하고 생성된 부활번호를 반환한다
    public static String saveReinstatement(String policyNo, String uwResult) {
        if (policyNo == null) {
            return null;
        }
        String reinstatementId = "REIN-" + System.currentTimeMillis();
        Reinstatement reinstatement = new Reinstatement();
        reinstatement.setAppliedAt(LocalDateTime.now());
        reinstatement.setProcessedAt(LocalDateTime.now());
        String reinstatementStatus = uwResult != null && uwResult.contains("승인") ? "APPROVED" : "REJECTED";
        String uwResultName = resolveUwResultName(resolveUwResultCode(uwResult));
        String statusName = resolveStatusName(reinstatementStatus);
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            int rows = s.getMapper(ReinstatementMapper.class)
                    .insert(reinstatement, reinstatementId, policyNo, uwResultName, statusName);
            return rows > 0 ? reinstatementId : null;
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 저장 실패: " + e.getMessage());
            return null;
        }
    }

    public static List<Reinstatement> getReinstatementList() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(ReinstatementMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static Reinstatement findReinstatementById(String reinstatementId) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(ReinstatementMapper.class).findById(reinstatementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public static String getReinstatementStatus(String reinstatementId) {
        String status;
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            status = s.getMapper(ReinstatementMapper.class).findStatusById(reinstatementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 부가정보 조회 실패: " + e.getMessage());
            return "";
        }
        return status == null ? "" : status;
    }

    private static String resolveUwResultCode(String uwResult) {
        if (uwResult == null) return "APPROVED";
        if (uwResult.startsWith("할증")) return "SURCHARGE";
        if (uwResult.startsWith("거절")) return "REJECTED";
        return "APPROVED";
    }

    private static String resolveUwResultName(String uwResult) {
        if ("APPROVED".equals(uwResult) || "SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult)) {
            return uwResult;
        }
        return "APPROVED";
    }

    private static String resolveStatusName(String status) {
        if ("APPROVED".equals(status) || "REJECTED".equals(status) || "PENDING".equals(status)) {
            return status;
        }
        return "PENDING";
    }
}
