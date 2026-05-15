package db;

import model.contract.Payout;
import java.util.ArrayList;
import java.util.List;

// Payout 엔티티 DB 매핑 — payout 테이블 CRUD 담당
public class PayoutDBO extends DBA {

    public Payout findById(String payoutId) {
        executeSelect("SELECT * FROM payout WHERE payout_id = '" + payoutId + "'");
        return null;
    }

    public List<Payout> findAll() {
        executeSelect("SELECT * FROM payout");
        return new ArrayList<>();
    }

    public void save(Payout payout) {
        executeInsert("INSERT INTO payout (...) VALUES (...)");
    }

    public void update(Payout payout) {
        executeUpdate("UPDATE payout SET ... WHERE payout_id = ...");
    }

    public void delete(String payoutId) {
        executeDelete("DELETE FROM payout WHERE payout_id = '" + payoutId + "'");
    }
}
