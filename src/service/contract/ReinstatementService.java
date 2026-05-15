package service.contract;

// 부활 관리 서비스 — 부활 가능 여부 판단 순수 비즈니스 로직 담당
public class ReinstatementService {

    // 계약 상태 기준 부활 가능 여부 판단
    public static boolean canReinstate(String contractStatus) {
        return "실효".equals(contractStatus);
    }
}
