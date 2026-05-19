package db;

import model.accident.DamageInvestigation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// DamageInvestigation 엔티티 DB 매핑 — damage_investigation 테이블 CRUD 담당
public class DamageInvestigationDBO extends DBA {

    public DamageInvestigation findById(String investigationId) {
        String sql = "SELECT investigation_id, adjuster_id, fault_ratio, repair_cost, medical_expense, "
                + "lost_income, settlement_amount, investigation_at "
                + "FROM damage_investigation WHERE investigation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, investigationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapInvestigation(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<DamageInvestigation> findAll() {
        String sql = "SELECT investigation_id, adjuster_id, fault_ratio, repair_cost, medical_expense, "
                + "lost_income, settlement_amount, investigation_at "
                + "FROM damage_investigation ORDER BY investigation_at DESC NULLS LAST, investigation_id";
        List<DamageInvestigation> investigationList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                investigationList.add(mapInvestigation(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 목록 조회 실패: " + e.getMessage());
        }
        return investigationList;
    }

    public boolean save(DamageInvestigation investigation) {
        if (investigation == null) {
            return false;
        }
        String sql = "INSERT INTO damage_investigation "
                + "(investigation_id, adjuster_id, fault_ratio, repair_cost, medical_expense, "
                + "lost_income, settlement_amount, investigation_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setInvestigationParams(statement, investigation);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(DamageInvestigation investigation) {
        if (investigation == null) {
            return false;
        }
        String sql = "UPDATE damage_investigation SET "
                + "adjuster_id = ?, fault_ratio = ?, repair_cost = ?, medical_expense = ?, "
                + "lost_income = ?, settlement_amount = ?, investigation_at = ? "
                + "WHERE investigation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, investigation.getAdjusterId());
            statement.setFloat(2, investigation.getFaultRatio());
            statement.setBigDecimal(3, investigation.getRepairCost());
            statement.setBigDecimal(4, investigation.getMedicalExpense());
            statement.setBigDecimal(5, investigation.getLostIncome());
            statement.setBigDecimal(6, investigation.getSettlementAmount());
            statement.setTimestamp(7, toTimestamp(investigation.getInvestigationAt()));
            statement.setString(8, investigation.getInvestigationId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String investigationId) {
        String sql = "DELETE FROM damage_investigation WHERE investigation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, investigationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private DamageInvestigation mapInvestigation(ResultSet resultSet) throws SQLException {
        Timestamp investigationTs = resultSet.getTimestamp("investigation_at");
        return new DamageInvestigation(
                resultSet.getString("investigation_id"),
                resultSet.getString("adjuster_id"),
                resultSet.getFloat("fault_ratio"),
                resultSet.getBigDecimal("repair_cost"),
                resultSet.getBigDecimal("medical_expense"),
                resultSet.getBigDecimal("lost_income"),
                resultSet.getBigDecimal("settlement_amount"),
                investigationTs == null ? null : investigationTs.toLocalDateTime()
        );
    }

    private void setInvestigationParams(PreparedStatement statement,
                                        DamageInvestigation investigation) throws SQLException {
        statement.setString(1, investigation.getInvestigationId());
        statement.setString(2, investigation.getAdjusterId());
        statement.setFloat(3, investigation.getFaultRatio());
        statement.setBigDecimal(4, investigation.getRepairCost());
        statement.setBigDecimal(5, investigation.getMedicalExpense());
        statement.setBigDecimal(6, investigation.getLostIncome());
        statement.setBigDecimal(7, investigation.getSettlementAmount());
        statement.setTimestamp(8, toTimestamp(investigation.getInvestigationAt()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }
}
