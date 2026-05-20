package service.accident;

import db.DamageInvestigationMapper;
import db.MyBatisSessionFactory;
import model.accident.DamageInvestigation;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// 손해조사 서비스 — 이의제기 처리 판정, DB 저장 담당
public class DamageInvestigationService {

    // 이의제기 처리 결과 메시지 반환
    public static String processObjection(String choice) {
        switch (choice) {
            case "1": return "기각 사유서가 DB에 저장되었습니다.";
            case "2": return "이의 제기 수용 — 사건 상태: '재조사 필요'";
            case "3": return "사건 상태: '법률과 이관' — 법률과 처리 완료 시 결과 알림 발송.";
            default:  return "기각 처리합니다.";
        }
    }

    // 구상 처리 필요 여부 판단
    public static boolean needsSubrogation(String answer) {
        return "Y".equalsIgnoreCase(answer);
    }

    public static boolean isYes(String answer) {
        return "Y".equalsIgnoreCase(answer) || "1".equals(answer);
    }

    // 손해조사 결과를 DB에 저장하고 생성된 조사번호를 반환한다
    public static String saveDamageInvestigation(DamageInvestigation investigation, String reportNo) {
        if (investigation == null || investigation.getInvestigationId() == null || reportNo == null) {
            return null;
        }
        String statusName = resolveStatusName("PENDING");
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            int rows = s.getMapper(DamageInvestigationMapper.class)
                    .insert(investigation, reportNo, statusName);
            return rows > 0 ? investigation.getInvestigationId() : null;
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 저장 실패: " + e.getMessage());
            return null;
        }
    }

    public static List<DamageInvestigation> getDamageInvestigationList() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(DamageInvestigationMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static DamageInvestigation findDamageInvestigationById(String investigationId) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(DamageInvestigationMapper.class).findById(investigationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public static String getInvestigationStatus(String investigationId) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            String status = s.getMapper(DamageInvestigationMapper.class).findStatusById(investigationId);
            return status == null ? "" : status;
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 부가정보 조회 실패: " + e.getMessage());
            return "";
        }
    }

    public static BigDecimal parseAmount(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.replace(",", "").replace("원", "").trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal sumAmounts(BigDecimal... amounts) {
        BigDecimal total = BigDecimal.ZERO;
        if (amounts == null) {
            return total;
        }
        for (BigDecimal amount : amounts) {
            if (amount != null) {
                total = total.add(amount);
            }
        }
        return total;
    }

    private static String resolveStatusName(String status) {
        if ("PENDING".equals(status) || "APPROVED".equals(status) || "REJECTED".equals(status)) {
            return status;
        }
        return "PENDING";
    }
}
