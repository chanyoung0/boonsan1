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

// AccidentHistory entity database operator for accident_history CRUD.
public class AccidentHistoryDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "receipt_number, history_id, accident_type, location, occurred_at, received_at, "
                    + "claimed_amount, recognized_amount, diagnosis_code, diagnosis_name, "
                    + "treatment_details, hospitalization_period, has_surgery, paid_at";

    public AccidentHistory findByReceiptNumber(String receiptNumber) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM accident_history WHERE receipt_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, receiptNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapHistory(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 이력 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<AccidentHistory> findAll() {
        List<AccidentHistory> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM accident_history ORDER BY receipt_number";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapHistory(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 이력 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT receipt_number FROM accident_history ORDER BY receipt_number";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("receipt_number"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 이력 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public List<AccidentHistory> findByHistoryId(String historyId) {
        List<AccidentHistory> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM accident_history WHERE history_id = ? ORDER BY occurred_at DESC";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, historyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapHistory(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 이력 조회 실패 (이력ID): " + e.getMessage());
        }
        return list;
    }

    public boolean save(AccidentHistory history) {
        throw new UnsupportedOperationException("historyId 파라미터가 필요합니다.");
    }

    public boolean save(AccidentHistory history, String historyId) {
        if (history == null || history.getReceiptNumber() == null) {
            return false;
        }
        String sql = "INSERT INTO accident_history "
                + "(receipt_number, history_id, accident_type, location, occurred_at, received_at, "
                + "claimed_amount, recognized_amount, diagnosis_code, diagnosis_name, "
                + "treatment_details, hospitalization_period, has_surgery, paid_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, history, historyId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 이력 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(AccidentHistory history) {
        throw new UnsupportedOperationException("없는 파라미터입니다. receiptNumber는 모델에 포함되어 있습니다.");
    }

    public boolean update(AccidentHistory history, String historyId) {
        if (history == null || history.getReceiptNumber() == null) {
            return false;
        }
        String sql = "UPDATE accident_history SET "
                + "history_id = ?, accident_type = ?, location = ?, occurred_at = ?, "
                + "received_at = ?, claimed_amount = ?, recognized_amount = ?, "
                + "diagnosis_code = ?, diagnosis_name = ?, treatment_details = ?, "
                + "hospitalization_period = ?, has_surgery = ?, paid_at = ? "
                + "WHERE receipt_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, historyId);
            statement.setString(2, resolveAccidentTypeName(history.getAccidentType()));
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
            statement.setString(14, history.getReceiptNumber());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 이력 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String receiptNumber) {
        String sql = "DELETE FROM accident_history WHERE receipt_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, receiptNumber);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 이력 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private AccidentHistory mapHistory(ResultSet resultSet) throws SQLException {
        AccidentHistory history = new AccidentHistory();
        history.setReceiptNumber(resultSet.getString("receipt_number"));
        history.setHistoryId(resultSet.getString("history_id"));
        history.setAccidentType(resolveAccidentType(resultSet.getString("accident_type")));
        history.setLocation(resultSet.getString("location"));
        history.setOccurredAt(toLocalDateTime(resultSet.getTimestamp("occurred_at")));
        history.setReceivedAt(toLocalDateTime(resultSet.getTimestamp("received_at")));
        history.setClaimedAmount(resultSet.getBigDecimal("claimed_amount"));
        history.setRecognizedAmount(resultSet.getBigDecimal("recognized_amount"));
        history.setDiagnosisCode(resultSet.getString("diagnosis_code"));
        history.setDiagnosisName(resultSet.getString("diagnosis_name"));
        history.setTreatmentDetails(resultSet.getString("treatment_details"));
        history.setHospitalizationPeriod(toLocalDateTime(resultSet.getTimestamp("hospitalization_period")));
        history.setHasSurgery(resultSet.getBoolean("has_surgery"));
        history.setPaidAt(toLocalDateTime(resultSet.getTimestamp("paid_at")));
        return history;
    }

    private void setSaveParams(PreparedStatement statement, AccidentHistory history,
                               String historyId) throws SQLException {
        statement.setString(1, history.getReceiptNumber());
        statement.setString(2, historyId);
        statement.setString(3, resolveAccidentTypeName(history.getAccidentType()));
        statement.setString(4, history.getLocation());
        statement.setTimestamp(5, toTimestamp(history.getOccurredAt()));
        statement.setTimestamp(6, toTimestamp(history.getReceivedAt()));
        statement.setBigDecimal(7, history.getClaimedAmount());
        statement.setBigDecimal(8, history.getRecognizedAmount());
        statement.setString(9, history.getDiagnosisCode());
        statement.setString(10, history.getDiagnosisName());
        statement.setString(11, history.getTreatmentDetails());
        statement.setTimestamp(12, toTimestamp(history.getHospitalizationPeriod()));
        statement.setBoolean(13, history.isHasSurgery());
        statement.setTimestamp(14, toTimestamp(history.getPaidAt()));
    }

    private String resolveAccidentTypeName(AccidentType value) {
        if (value == null) return AccidentType.INJURY.name();
        return value.name();
    }

    private AccidentType resolveAccidentType(String value) {
        if (value == null || value.trim().isEmpty()) return AccidentType.INJURY;
        try {
            return AccidentType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return AccidentType.INJURY;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
