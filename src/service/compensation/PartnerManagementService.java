package service.compensation;

import db.PartnerDBO;
import enums.EvaluationGrade;
import model.partner.Partner;

// 협력업체 관리 서비스 — 협력업체 등록/수정 저장 유스케이스 흐름 담당
public class PartnerManagementService {

    // 입력값을 EvaluationGrade 매핑
    public EvaluationGrade resolveGrade(String choice) {
        switch (choice) {
            case "1": return EvaluationGrade.EXCELLENT;
            case "2": return EvaluationGrade.GOOD;
            case "3": return EvaluationGrade.AVERAGE;
            case "4": return EvaluationGrade.POOR;
            default:  return EvaluationGrade.AVERAGE;
        }
    }

    // Partner 객체 생성 및 저장
    public void savePartner(String partnerName, String partnerType, String contact,
                             String responsibility, String gradeChoice) {
        String id = "PARTNER-" + System.currentTimeMillis();
        EvaluationGrade grade = resolveGrade(gradeChoice);
        Partner partner = new Partner(id, partnerName, partnerType, contact, responsibility, grade);
        partner.register();
        new PartnerDBO().save(partner);
    }
}
