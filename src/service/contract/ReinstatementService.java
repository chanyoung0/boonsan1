package service.contract;

import db.ReinstatementDBO;
import enums.ReinstatementReason;
import model.contract.Reinstatement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 부활 관리 서비스 — 부활 가능 여부 판단, 부활 객체 저장 유스케이스 흐름 담당
public class ReinstatementService {

    // 계약 상태 기준 부활 가능 여부 판단
    public boolean canReinstate(String contractStatus) {
        return "실효".equals(contractStatus);
    }

    // Reinstatement 객체 생성 및 저장
    public void saveReinstatement(String healthChanged, BigDecimal unpaidPremium) {
        Reinstatement reinstatement = new Reinstatement(
            ReinstatementReason.OTHER,
            unpaidPremium,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(7),
            LocalDate.now(),
            "있음".equals(healthChanged)
        );
        new ReinstatementDBO().save(reinstatement);
    }
}
