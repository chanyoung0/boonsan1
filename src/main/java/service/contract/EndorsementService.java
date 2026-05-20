package service.contract;

import db.mapper.EndorsementMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.EndorsementType;
import model.contract.Endorsement;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 배서 관리 서비스 — 배서 심사 필요 여부 판단, DB 저장 담당
public class EndorsementService {

    // 배서유형별 심사 필요 여부 판단 (가입금액 변경/특약 추가 → 위험 변동 → 심사 필요)
    public static boolean needsUnderwriting(String endorsementType) {
        return requiresFullUnderwriting(endorsementType);
    }

    // 위험 변동 배서 여부 판단
    public static boolean requiresFullUnderwriting(String endorsementType) {
        return "1".equals(endorsementType) || "3".equals(endorsementType);
    }

    public static Endorsement createEndorsement(String previousContent, String newContent) {
        Endorsement endorsement = new Endorsement();
        endorsement.setPreviousContent(previousContent);
        endorsement.setNewContent(newContent);
        endorsement.setAppliedAt(LocalDateTime.now());
        return endorsement;
    }

    // 배서를 DB에 저장하고 생성된 배서번호를 반환한다
    public static String saveEndorsement(String policyNo, String endorsementTypeChoice,
                                         Endorsement endorsement, String changeReason, String uwResult) {
        if (endorsement == null || policyNo == null) {
            return null;
        }
        if (endorsement.getProcessedAt() == null) {
            endorsement.setProcessedAt(LocalDateTime.now());
        }
        String endorsementId = "END-" + System.currentTimeMillis();
        String endorsementType = resolveEndorsementTypeChoice(endorsementTypeChoice);
        String uwResultName = resolveUwResultName(uwResult);
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            int rows = s.getMapper(EndorsementMapper.class)
                    .insert(endorsement, endorsementId, policyNo,
                            endorsementType, changeReason, uwResultName);
            return rows > 0 ? endorsementId : null;
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 저장 실패: " + e.getMessage());
            return null;
        }
    }

    public static List<Endorsement> getEndorsementList() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(EndorsementMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static Endorsement findEndorsementById(String endorsementId) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(EndorsementMapper.class).findById(endorsementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public static String createEndorsementRequestSummary(String policyNo, String endorsementType,
                                                         Endorsement endorsement,
                                                         String changeReason) {
        return "[시스템] 배서 신청 내용"
                + "\n  증권번호: " + policyNo
                + "\n  배서유형: " + resolveEndorsementTypeLabel(endorsementType)
                + "\n  변경 전 내용: " + emptyToDefault(endorsement.getPreviousContent())
                + "\n  변경 후 내용: " + emptyToDefault(endorsement.getNewContent())
                + "\n  변경사유: " + emptyToDefault(changeReason)
                + "\n  신청일시: " + endorsement.getAppliedAt()
                + "\n  안내: EndorsementType enum이 현재 메뉴와 완전히 1:1 대응되지 않아 기존 심사 정책을 유지합니다.";
    }

    public static String createEndorsementSaveSummary(String policyNo, Endorsement endorsement,
                                                      String changeReason, String underwritingResult) {
        endorsement.setProcessedAt(LocalDateTime.now());
        return "[시스템] 배서 저장 요약"
                + "\n  증권번호: " + policyNo
                + "\n  변경 전 내용: " + emptyToDefault(endorsement.getPreviousContent())
                + "\n  변경 후 내용: " + emptyToDefault(endorsement.getNewContent())
                + "\n  변경사유: " + emptyToDefault(changeReason)
                + "\n  심사결과: " + emptyToDefault(underwritingResult)
                + "\n  처리일시: " + endorsement.getProcessedAt();
    }

    private static String resolveEndorsementTypeLabel(String endorsementType) {
        switch (endorsementType) {
            case "1": return "보험가입금액 변경";
            case "2": return "납입주기 변경";
            case "3": return "특약 추가";
            case "4": return "특약 삭제";
            default:  return "미확인";
        }
    }

    private static String resolveEndorsementTypeChoice(String choice) {
        switch (choice == null ? "" : choice) {
            case "1": return EndorsementType.COVERAGE_CHANGE.name();
            case "2": return EndorsementType.PREMIUM_CHANGE.name();
            case "3": return EndorsementType.SPECIAL_CONTRACT_CHANGE.name();
            case "4": return EndorsementType.SPECIAL_CONTRACT_CHANGE.name();
            default:  return choice;
        }
    }

    private static String resolveUwResultName(String uwResult) {
        if (uwResult == null) return null;
        if (uwResult.startsWith("할증")) return "SURCHARGE";
        if (uwResult.startsWith("거절")) return "REJECTED";
        if (uwResult.startsWith("승인")) return "APPROVED";
        if ("SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult) || "APPROVED".equals(uwResult)) {
            return uwResult;
        }
        return null;
    }

    private static String emptyToDefault(String value) {
        return value == null || value.trim().isEmpty() ? "미입력" : value;
    }
}
