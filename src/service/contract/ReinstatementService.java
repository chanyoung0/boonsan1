package service.contract;

import db.ReinstatementDBO;
import model.contract.Reinstatement;

import java.time.LocalDateTime;
import java.util.List;

// 부활 관리 서비스 — 부활 가능 여부 판단, DB 저장 담당
public class ReinstatementService {

    private static final ReinstatementDBO reinstatementDBO = new ReinstatementDBO();

    // 계약 상태 기준 부활 가능 여부 판단
    public static boolean canReinstate(String contractStatus) {
        return "실효".equals(contractStatus);
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
        boolean saved = reinstatementDBO.save(reinstatement, reinstatementId, policyNo,
                resolveUwResultCode(uwResult), reinstatementStatus);
        return saved ? reinstatementId : null;
    }

    public static List<Reinstatement> getReinstatementList() {
        return reinstatementDBO.findAll();
    }

    public static Reinstatement findReinstatementById(String reinstatementId) {
        return reinstatementDBO.findById(reinstatementId);
    }

    public static String getReinstatementStatus(String reinstatementId) {
        String status = reinstatementDBO.findStatusById(reinstatementId);
        return status == null ? "" : status;
    }

    private static String resolveUwResultCode(String uwResult) {
        if (uwResult == null) return "APPROVED";
        if (uwResult.startsWith("할증")) return "SURCHARGE";
        if (uwResult.startsWith("거절")) return "REJECTED";
        return "APPROVED";
    }
}
