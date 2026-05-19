package db;

import enums.AccidentType;
import model.accident.AccidentHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// AccidentHistory 엔티티 DB 매핑 — accident_history 테이블 CRUD 담당
public class AccidentHistoryDBO extends DBA {

    public AccidentHistory findByReceiptNumber(String receiptNumber) {
        String sql = "SELECT receipt_number, accident_type, location, occurred_at, received_at, "
                + "claimed_amount, recognized_amount, diagnosis_code, diagnosis_name, "
                + "treatment_details, hospitalization_period, has_surgery, paid_at "
                + "FROM accident_history WHERE receipt_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, receiptNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapHistory(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고이력 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<AccidentHistory> findAll() {
        String sql = "SELECT receipt_number, accident_type, location, occurred_at, received_at, "
                + "claimed_amount, recognized_amount, diagnosis_code, diagnosis_name, "
                + "treatment_details, hospitalization_period, has_surgery, paid_at "
                + "FROM accident_history ORDER BY occurred_at DESC NULLS LAST, receipt_number";
        List<AccidentHistory> historyList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                historyList.add(mapHistory(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고이력 목록 조회 실패: " + e.getMessage());
        }
        return historyList;
    }

    public boolean save(AccidentHistory history) {
        if (history == null) {
            return false;
        }
        String sql = "INSERT INTO accident_history "
                + "(receipt_number, accident_type, location, occurred_at, received_at, "
                + "claimed_amount, recognized_amount, diagnosis_code, diagnosis_name, "
                + "treatment_details, hospitalization_period, has_surgery, paid_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setHistoryParams(statement, history);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고이력 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(AccidentHistory history) {
        if (history == null) {
            return false;
        }
        String sql = "UPDATE accident_history SET "
                + "accident_type = ?, location = ?, occurred_at = ?, received_at = ?, "
                + "claimed_amount = ?, recognized_amount = ?, diagnosis_code = ?, diagnosis_name = ?, "
                + "treatment_details = ?, hospitalization_period = ?, has_surgery = ?, paid_at = ? "
                + "WHERE receipt_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveTypeName(history.getAccidentType()));
            statement.setString(2, history.getLocation());
            statement.setTimestamp(3, toTimestamp(history.getOccurredAt()));
            statement.setTimestamp(4, toTimestamp(history.getReceivedAt()));
            statement.setBigDecimal(5, history.getClaimedAmount());
            statement.setBigDecimal(6, history.getRecognizedAmount());
            statement.setString(7, history.getDiagnosisCode());
            statement.setString(8, history.getDiagnosisName());
            statement.setString(9, history.getTreatmentDetails());
            statement.setTimestamp(10, toTimestamp(history.getHospitalizationPeriod()));
            statement.setBoolean(11, history.isHasSurgery());
            statement.setTimestamp(12, toTimestamp(history.getPaidAt()));
            statement.setString(13, history.getReceiptNumber());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고이력 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String receiptNumber) {
        String sql = "DELETE FROM accident_history WHERE receipt_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, receiptNumber);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고이력 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private AccidentHistory mapHistory(ResultSet resultSet) throws SQLException {
        return new AccidentHistory(
                resultSet.getString("receipt_number"),
                resolveType(resultSet.getString("accident_type")),
                resultSet.getString("location"),
                toLocalDateTime(resultSet.getTimestamp("occurred_at")),
                toLocalDateTime(resultSet.getTimestamp("received_at")),
                resultSet.getBigDecimal("claimed_amount"),
                resultSet.getBigDecimal("recognized_amount"),
                resultSet.getString("diagnosis_code"),
                resultSet.getString("diagnosis_name"),
                resultSet.getString("treatment_details"),
                toLocalDateTime(resultSet.getTimestamp("hospitalization_period")),
                resultSet.getBoolean("has_surgery"),
                toLocalDateTime(resultSet.getTimestamp("paid_at"))
        );
    }

    private void setHistoryParams(PreparedStatement statement, AccidentHistory history) throws SQLException {
        statement.setString(1, history.getReceiptNumber());
        statement.setString(2, resolveTypeName(history.getAccidentType()));
        statement.setString(3, history.getLocation());
        statement.setTimestamp(4, toTimestamp(history.getOccurredAt()));
        statement.setTimestamp(5, toTimestamp(history.getReceivedAt()));
        statement.setBigDecimal(6, history.getClaimedAmount());
        statement.setBigDecimal(7, history.getRecognizedAmount());
        statement.setString(8, history.getDiagnosisCode());
        statement.setString(9, history.getDiagnosisName());
        statement.setString(10, history.getTreatmentDetails());
        statement.setTimestamp(11, toTimestamp(history.getHospitalizationPeriod()));
        statement.setBoolean(12, history.isHasSurgery());
        statement.setTimestamp(13, toTimestamp(history.getPaidAt()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveTypeName(AccidentType type) {
        return type == null ? null : type.name();
    }

    private AccidentType resolveType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return AccidentType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
