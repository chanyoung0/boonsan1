package service.accident;

// 손해조사 서비스 — 이의제기 처리 판정 순수 비즈니스 로직 담당
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
}
