package service.contract;

import db.EndorsementDBO;
import model.contract.Endorsement;

import java.time.LocalDateTime;

// 배서 관리 서비스 — 배서 심사 필요 여부 판단 + 영속화 담당
public class EndorsementService {

    private static final EndorsementDBO endorsementDBO = new EndorsementDBO();

    public static boolean saveEndorsement(Endorsement endorsement, String policyNumber) {
        if (endorsement == null) {
            return false;
        }
        if (endorsement.getEndorsementId() == null || endorsement.getEndorsementId().isEmpty()) {
            endorsement.setEndorsementId("END-" + System.currentTimeMillis());
        }
        endorsement.setPolicyNumber(policyNumber);
        return endorsementDBO.save(endorsement);
    }

    public static boolean updateEndorsement(Endorsement endorsement) {
        return endorsementDBO.update(endorsement);
    }

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

    private static String emptyToDefault(String value) {
        return value == null || value.trim().isEmpty() ? "미입력" : value;
    }
}
