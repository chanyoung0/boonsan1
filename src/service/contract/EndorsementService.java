package service.contract;

// 배서 관리 서비스 — 배서 심사 필요 여부 판단 순수 비즈니스 로직 담당
public class EndorsementService {

    // 배서유형별 심사 필요 여부 판단 (가입금액 변경/특약 추가 → 위험 변동 → 심사 필요)
    public static boolean needsUnderwriting(String endorsementType) {
        return requiresFullUnderwriting(endorsementType);
    }

    // 위험 변동 배서 여부 판단
    public static boolean requiresFullUnderwriting(String endorsementType) {
        return "1".equals(endorsementType) || "3".equals(endorsementType);
    }
}
