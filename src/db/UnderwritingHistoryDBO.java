package db;

import enums.Gender;
import model.underwriting.UnderwritingHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// UnderwritingHistory entity database operator for underwriting_history CRUD.
public class UnderwritingHistoryDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "history_id, rrn, name, age, gender, occupation, bmi, "
                    + "alcohol_consumption, family_history, is_medicated, is_smoker, "
                    + "past_medical_history, surgery_history, vehicle_model, vehicle_number, inquired_at";

    public UnderwritingHistory findById(String historyId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting_history WHERE history_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, historyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapHistory(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 이력 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public UnderwritingHistory findLatestByRrn(String rrn) {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM underwriting_history WHERE rrn = ? ORDER BY inquired_at DESC LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rrn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapHistory(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 최근 심사 이력 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<UnderwritingHistory> findAll() {
        List<UnderwritingHistory> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting_history ORDER BY history_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapHistory(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 이력 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public void save(UnderwritingHistory history) {
        if (history == null || history.getHistoryId() == null) return;
        String sql = "INSERT INTO underwriting_history "
                + "(history_id, rrn, name, age, gender, occupation, bmi, "
                + "alcohol_consumption, family_history, is_medicated, is_smoker, "
                + "past_medical_history, surgery_history, vehicle_model, vehicle_number, inquired_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, history);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 이력 저장 실패: " + e.getMessage());
        }
    }

    public void update(UnderwritingHistory history) {
        if (history == null || history.getHistoryId() == null) return;
        String sql = "UPDATE underwriting_history SET "
                + "rrn = ?, name = ?, age = ?, gender = ?, occupation = ?, bmi = ?, "
                + "alcohol_consumption = ?, family_history = ?, is_medicated = ?, is_smoker = ?, "
                + "past_medical_history = ?, surgery_history = ?, vehicle_model = ?, vehicle_number = ?, "
                + "inquired_at = ? WHERE history_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setUpdateParams(statement, history);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 이력 수정 실패: " + e.getMessage());
        }
    }

    public void delete(String historyId) {
        String sql = "DELETE FROM underwriting_history WHERE history_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, historyId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 이력 삭제 실패: " + e.getMessage());
        }
    }

    private UnderwritingHistory mapHistory(ResultSet resultSet) throws SQLException {
        UnderwritingHistory history = new UnderwritingHistory();
        history.setHistoryId(resultSet.getString("history_id"));
        history.setResidentRegistrationNumber(resultSet.getString("rrn"));
        history.setName(resultSet.getString("name"));
        history.setAge(resultSet.getInt("age"));
        history.setGender(resolveGender(resultSet.getString("gender")));
        history.setOccupation(resultSet.getString("occupation"));
        history.setBMI(resultSet.getString("bmi"));
        history.setAlcoholConsumption(resultSet.getString("alcohol_consumption"));
        history.setFamilyHistory(resultSet.getString("family_history"));
        history.setMedicated(resultSet.getBoolean("is_medicated"));
        history.setSmoker(resultSet.getBoolean("is_smoker"));
        history.setPastMedicalHistory(resultSet.getString("past_medical_history"));
        history.setSurgeryHistory(resultSet.getString("surgery_history"));
        history.setVehicleModel(resultSet.getString("vehicle_model"));
        history.setVehicleNumber(resultSet.getString("vehicle_number"));
        Timestamp ts = resultSet.getTimestamp("inquired_at");
        if (ts != null) history.setInquiredAt(ts.toLocalDateTime());
        return history;
    }

    private void setSaveParams(PreparedStatement stmt, UnderwritingHistory h) throws SQLException {
        stmt.setString(1, h.getHistoryId());
        stmt.setString(2, h.getResidentRegistrationNumber());
        stmt.setString(3, h.getName());
        stmt.setInt(4, h.getAge());
        stmt.setString(5, h.getGender() != null ? h.getGender().name() : null);
        stmt.setString(6, h.getOccupation());
        stmt.setString(7, h.getBMI());
        stmt.setString(8, h.getAlcoholConsumption());
        stmt.setString(9, h.getFamilyHistory());
        stmt.setBoolean(10, h.isMedicated());
        stmt.setBoolean(11, h.isSmoker());
        stmt.setString(12, h.getPastMedicalHistory());
        stmt.setString(13, h.getSurgeryHistory());
        stmt.setString(14, h.getVehicleModel());
        stmt.setString(15, h.getVehicleNumber());
        stmt.setTimestamp(16, h.getInquiredAt() != null ? Timestamp.valueOf(h.getInquiredAt()) : null);
    }

    private void setUpdateParams(PreparedStatement stmt, UnderwritingHistory h) throws SQLException {
        stmt.setString(1, h.getResidentRegistrationNumber());
        stmt.setString(2, h.getName());
        stmt.setInt(3, h.getAge());
        stmt.setString(4, h.getGender() != null ? h.getGender().name() : null);
        stmt.setString(5, h.getOccupation());
        stmt.setString(6, h.getBMI());
        stmt.setString(7, h.getAlcoholConsumption());
        stmt.setString(8, h.getFamilyHistory());
        stmt.setBoolean(9, h.isMedicated());
        stmt.setBoolean(10, h.isSmoker());
        stmt.setString(11, h.getPastMedicalHistory());
        stmt.setString(12, h.getSurgeryHistory());
        stmt.setString(13, h.getVehicleModel());
        stmt.setString(14, h.getVehicleNumber());
        stmt.setTimestamp(15, h.getInquiredAt() != null ? Timestamp.valueOf(h.getInquiredAt()) : null);
        stmt.setString(16, h.getHistoryId());
    }

    private Gender resolveGender(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Gender.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
