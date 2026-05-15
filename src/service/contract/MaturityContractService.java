package service.contract;

// 만기계약 관리 서비스 — 재계약 의사 처리 순수 비즈니스 로직 담당
public class MaturityContractService {

    // 재계약 의사 결과 처리
    public static String processRenewalIntention(String intention) {
        switch (intention) {
            case "1": return "재계약 의사 있음 — 재계약 절차를 안내합니다.";
            case "2": return "만기 처리 완료 — 계약상태: '만기종료'";
            case "3": return "회신 기한 초과 — 계약상태: '만기종료'";
            default:  return "[오류] 올바른 선택이 아닙니다.";
        }
    }
}
