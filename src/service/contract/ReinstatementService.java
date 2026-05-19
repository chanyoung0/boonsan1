package service.contract;

import db.ReinstatementDBO;
import model.contract.Reinstatement;

// 부활 관리 서비스 — 부활 가능 여부 판단 + 영속화 담당
public class ReinstatementService {

    private static final ReinstatementDBO reinstatementDBO = new ReinstatementDBO();

    // 계약 상태 기준 부활 가능 여부 판단
    public static boolean canReinstate(String contractStatus) {
        return "실효".equals(contractStatus);
    }

    public static boolean saveReinstatement(Reinstatement reinstatement, String policyNumber) {
        if (reinstatement == null) {
            return false;
        }
        if (reinstatement.getReinstatementId() == null || reinstatement.getReinstatementId().isEmpty()) {
            reinstatement.setReinstatementId("RST-" + System.currentTimeMillis());
        }
        reinstatement.setPolicyNumber(policyNumber);
        return reinstatementDBO.save(reinstatement);
    }

    public static boolean updateReinstatement(Reinstatement reinstatement) {
        return reinstatementDBO.update(reinstatement);
    }
}
