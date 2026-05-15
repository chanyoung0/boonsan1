package service.contract;

import db.EndorsementDBO;
import enums.ChangeReason;
import enums.EndorsementType;
import model.contract.Endorsement;

import java.time.LocalDateTime;

// 배서 관리 서비스 — 배서 심사 필요 여부 판단, 배서 객체 저장 유스케이스 흐름 담당
public class EndorsementService {

    // 배서유형별 심사 필요 여부 판단 (가입금액 변경/특약 추가 → 위험 변동 → 심사 필요)
    public static boolean needsUnderwriting(String endorsementType) {
        return "1".equals(endorsementType) || "3".equals(endorsementType);
    }

    // Endorsement 객체 생성 및 저장
    public static void saveEndorsement(String typeChoice, String previousContent, String newContent) {
        Endorsement endorsement = new Endorsement(
            resolveEndorsementType(typeChoice),
            resolveChangeReason(typeChoice),
            previousContent,
            newContent,
            LocalDateTime.now()
        );
        new EndorsementDBO().save(endorsement);
    }

    private static EndorsementType resolveEndorsementType(String choice) {
        switch (choice) {
            case "1": return EndorsementType.COVERAGE_CHANGE;
            case "2": return EndorsementType.PREMIUM_CHANGE;
            case "3":
            case "4": return EndorsementType.SPECIAL_CONTRACT_CHANGE;
            default:  return EndorsementType.COVERAGE_CHANGE;
        }
    }

    private static ChangeReason resolveChangeReason(String choice) {
        switch (choice) {
            case "1": return ChangeReason.INSURED_AMOUNT_CHANGE;
            case "2": return ChangeReason.PAYMENT_CYCLE_CHANGE;
            case "3": return ChangeReason.SPECIAL_CONTRACT_ADD;
            case "4": return ChangeReason.SPECIAL_CONTRACT_REMOVE;
            default:  return ChangeReason.INSURED_AMOUNT_CHANGE;
        }
    }
}
