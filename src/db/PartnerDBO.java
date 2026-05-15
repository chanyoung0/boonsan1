package db;

import model.partner.Partner;
import java.util.ArrayList;
import java.util.List;

// Partner 엔티티 DB 매핑 — partner 테이블 CRUD 담당
public class PartnerDBO extends DBA {

    public Partner findById(String partnerId) {
        executeSelect("SELECT * FROM partner WHERE id = ?", partnerId);
        return null;
    }

    public List<Partner> findAll() {
        executeSelect("SELECT * FROM partner");
        return new ArrayList<>();
    }

    public void save(Partner partner) {
        executeInsert(
            "INSERT INTO partner (id, partner_name, partner_type, contact, responsibility, evaluation_grade) " +
            "VALUES (?, ?, ?, ?, ?, ?)",
            partner.getId(), partner.getPartnerName(), partner.getPartnerType(),
            partner.getContact(), partner.getResponsibility(), partner.getEvaluationGrade()
        );
    }

    public void update(Partner partner) {
        executeUpdate(
            "UPDATE partner SET partner_name = ?, partner_type = ?, contact = ?, responsibility = ?, evaluation_grade = ? WHERE id = ?",
            partner.getPartnerName(), partner.getPartnerType(), partner.getContact(),
            partner.getResponsibility(), partner.getEvaluationGrade(), partner.getId()
        );
    }

    public void delete(String partnerId) {
        executeDelete("DELETE FROM partner WHERE id = ?", partnerId);
    }
}
