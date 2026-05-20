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

// DamageInvestigation entity database operator for damage_investigation CRUD.
public class DamageInvestigationDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "investigation_id, report_no, adjuster_id, medical_expense, lost_income, "
                    + "repair_cost, settlement_amount, fault_ratio, investigation_status, investigation_at";

    public DamageInvestigation findById(String investigationId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM damage_investigation WHERE investigation_id = ?";
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
        List<DamageInvestigation> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM damage_investigation ORDER BY investigation_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapInvestigation(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT investigation_id FROM damage_investigation ORDER BY investigation_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("investigation_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 ID 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public boolean save(DamageInvestigation investigation) {
        throw new UnsupportedOperationException("reportNo, investigationStatus 파라미터가 필요합니다.");
    }

    public boolean save(DamageInvestigation investigation, String reportNo, String investigationStatus) {
        if (investigation == null || investigation.getInvestigationId() == null || reportNo == null) {
            return false;
        }
        String sql = "INSERT INTO damage_investigation "
                + "(investigation_id, report_no, adjuster_id, medical_expense, lost_income, "
                + "repair_cost, settlement_amount, fault_ratio, investigation_status, investigation_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, investigation, reportNo, investigationStatus);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(DamageInvestigation investigation) {
        throw new UnsupportedOperationException("reportNo, investigationStatus 파라미터가 필요합니다.");
    }

    public boolean update(DamageInvestigation investigation, String reportNo, String investigationStatus) {
        if (investigation == null || investigation.getInvestigationId() == null || reportNo == null) {
            return false;
        }
        String sql = "UPDATE damage_investigation SET "
                + "report_no = ?, adjuster_id = ?, medical_expense = ?, lost_income = ?, "
                + "repair_cost = ?, settlement_amount = ?, fault_ratio = ?, "
                + "investigation_status = ?, investigation_at = ? "
                + "WHERE investigation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportNo);
            statement.setString(2, investigation.getAdjusterId());
            statement.setBigDecimal(3, investigation.getMedicalExpense());
            statement.setBigDecimal(4, investigation.getLostIncome());
            statement.setBigDecimal(5, investigation.getRepairCost());
            statement.setBigDecimal(6, investigation.getSettlementAmount());
            statement.setFloat(7, investigation.getFaultRatio());
            statement.setString(8, resolveStatusName(investigationStatus));
            statement.setTimestamp(9, toTimestamp(investigation.getInvestigationAt()));
            statement.setString(10, investigation.getInvestigationId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String investigationId) {
        String sql = "DELETE FROM damage_investigation WHERE investigation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, investigationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    public String findStatusById(String investigationId) {
        return findStringColumnById(investigationId, "investigation_status");
    }

    public String findReportNoById(String investigationId) {
        return findStringColumnById(investigationId, "report_no");
    }

    private String findStringColumnById(String investigationId, String columnName) {
        String sql = "SELECT " + columnName + " FROM damage_investigation WHERE investigation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, investigationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 손해조사 부가정보 조회 실패: " + e.getMessage());
        }
        return null;
    }

    private DamageInvestigation mapInvestigation(ResultSet resultSet) throws SQLException {
        DamageInvestigation investigation = new DamageInvestigation();
        investigation.setInvestigationId(resultSet.getString("investigation_id"));
        investigation.setAdjusterId(resultSet.getString("adjuster_id"));
        investigation.setMedicalExpense(resultSet.getBigDecimal("medical_expense"));
        investigation.setLostIncome(resultSet.getBigDecimal("lost_income"));
        investigation.setRepairCost(resultSet.getBigDecimal("repair_cost"));
        investigation.setSettlementAmount(resultSet.getBigDecimal("settlement_amount"));
        investigation.setFaultRatio(resultSet.getFloat("fault_ratio"));
        investigation.setInvestigationAt(toLocalDateTime(resultSet.getTimestamp("investigation_at")));
        return investigation;
    }

    private void setSaveParams(PreparedStatement statement, DamageInvestigation investigation,
                               String reportNo, String investigationStatus) throws SQLException {
        statement.setString(1, investigation.getInvestigationId());
        statement.setString(2, reportNo);
        statement.setString(3, investigation.getAdjusterId());
        statement.setBigDecimal(4, investigation.getMedicalExpense());
        statement.setBigDecimal(5, investigation.getLostIncome());
        statement.setBigDecimal(6, investigation.getRepairCost());
        statement.setBigDecimal(7, investigation.getSettlementAmount());
        statement.setFloat(8, investigation.getFaultRatio());
        statement.setString(9, resolveStatusName(investigationStatus));
        statement.setTimestamp(10, toTimestamp(investigation.getInvestigationAt()));
    }

    private String resolveStatusName(String status) {
        if ("PENDING".equals(status) || "APPROVED".equals(status) || "REJECTED".equals(status)) {
            return status;
        }
        return "PENDING";
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
