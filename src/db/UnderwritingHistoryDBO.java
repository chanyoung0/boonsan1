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
            "history_id, underwriting_id, name, age, gender, occupation, annual_income, "
                    + "bmi, is_smoker, is_medicated, alcohol_consumption, past_medical_history, "
                    + "surgery_history, family_history, resident_registration_number, "
                    + "vehicle_number, vehicle_model, inquired_at";

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
            System.out.println("[DB 오류] 언더라이팅 이력 조회 실패: " + e.getMessage());
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
            System.out.println("[DB 오류] 언더라이팅 이력 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT history_id FROM underwriting_history ORDER BY history_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("history_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 이력 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public List<UnderwritingHistory> findByUnderwritingId(String underwritingId) {
        List<UnderwritingHistory> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM underwriting_history WHERE underwriting_id = ? ORDER BY history_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwritingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapHistory(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 이력 조회 실패 (심사ID): " + e.getMessage());
        }
        return list;
    }

    public boolean save(UnderwritingHistory history) {
        throw new UnsupportedOperationException("historyId, underwritingId 파라미터가 필요합니다.");
    }

    public boolean save(UnderwritingHistory history, String historyId, String underwritingId) {
        if (history == null || historyId == null || underwritingId == null) {
            return false;
        }
        String sql = "INSERT INTO underwriting_history "
                + "(history_id, underwriting_id, name, age, gender, occupation, annual_income, "
                + "bmi, is_smoker, is_medicated, alcohol_consumption, past_medical_history, "
                + "surgery_history, family_history, resident_registration_number, "
                + "vehicle_number, vehicle_model, inquired_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, history, historyId, underwritingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 이력 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(UnderwritingHistory history) {
        throw new UnsupportedOperationException("historyId 파라미터가 필요합니다.");
    }

    public boolean update(UnderwritingHistory history, String historyId) {
        if (history == null || historyId == null) {
            return false;
        }
        String sql = "UPDATE underwriting_history SET "
                + "name = ?, age = ?, gender = ?, occupation = ?, annual_income = ?, "
                + "bmi = ?, is_smoker = ?, is_medicated = ?, alcohol_consumption = ?, "
                + "past_medical_history = ?, surgery_history = ?, family_history = ?, "
                + "resident_registration_number = ?, vehicle_number = ?, vehicle_model = ?, "
                + "inquired_at = ? "
                + "WHERE history_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, history.getName());
            statement.setInt(2, history.getAge());
            statement.setString(3, resolveGenderName(history.getGender()));
            statement.setString(4, history.getOccupation());
            statement.setBigDecimal(5, history.getAnnualIncome());
            statement.setString(6, history.getBMI());
            statement.setBoolean(7, history.isSmoker());
            statement.setBoolean(8, history.isMedicated());
            statement.setString(9, history.getAlcoholConsumption());
            statement.setString(10, history.getPastMedicalHistory());
            statement.setString(11, history.getSurgeryHistory());
            statement.setString(12, history.getFamilyHistory());
            statement.setString(13, history.getResidentRegistrationNumber());
            statement.setString(14, history.getVehicleNumber());
            statement.setString(15, history.getVehicleModel());
            statement.setTimestamp(16, toTimestamp(history.getInquiredAt()));
            statement.setString(17, historyId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 이력 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String historyId) {
        String sql = "DELETE FROM underwriting_history WHERE history_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, historyId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 이력 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private UnderwritingHistory mapHistory(ResultSet resultSet) throws SQLException {
        UnderwritingHistory history = new UnderwritingHistory();
        history.setHistoryId(resultSet.getString("history_id"));
        history.setUnderwritingId(resultSet.getString("underwriting_id"));
        history.setName(resultSet.getString("name"));
        history.setAge(resultSet.getInt("age"));
        history.setGender(resolveGender(resultSet.getString("gender")));
        history.setOccupation(resultSet.getString("occupation"));
        history.setAnnualIncome(resultSet.getBigDecimal("annual_income"));
        history.setBMI(resultSet.getString("bmi"));
        history.setSmoker(resultSet.getBoolean("is_smoker"));
        history.setMedicated(resultSet.getBoolean("is_medicated"));
        history.setAlcoholConsumption(resultSet.getString("alcohol_consumption"));
        history.setPastMedicalHistory(resultSet.getString("past_medical_history"));
        history.setSurgeryHistory(resultSet.getString("surgery_history"));
        history.setFamilyHistory(resultSet.getString("family_history"));
        history.setResidentRegistrationNumber(resultSet.getString("resident_registration_number"));
        history.setVehicleNumber(resultSet.getString("vehicle_number"));
        history.setVehicleModel(resultSet.getString("vehicle_model"));
        history.setInquiredAt(toLocalDateTime(resultSet.getTimestamp("inquired_at")));
        return history;
    }

    private void setSaveParams(PreparedStatement statement, UnderwritingHistory history,
                               String historyId, String underwritingId) throws SQLException {
        statement.setString(1, historyId);
        statement.setString(2, underwritingId);
        statement.setString(3, history.getName());
        statement.setInt(4, history.getAge());
        statement.setString(5, resolveGenderName(history.getGender()));
        statement.setString(6, history.getOccupation());
        statement.setBigDecimal(7, history.getAnnualIncome());
        statement.setString(8, history.getBMI());
        statement.setBoolean(9, history.isSmoker());
        statement.setBoolean(10, history.isMedicated());
        statement.setString(11, history.getAlcoholConsumption());
        statement.setString(12, history.getPastMedicalHistory());
        statement.setString(13, history.getSurgeryHistory());
        statement.setString(14, history.getFamilyHistory());
        statement.setString(15, history.getResidentRegistrationNumber());
        statement.setString(16, history.getVehicleNumber());
        statement.setString(17, history.getVehicleModel());
        statement.setTimestamp(18, toTimestamp(history.getInquiredAt()));
    }

    private String resolveGenderName(Gender value) {
        if (value == null) return Gender.OTHER.name();
        return value.name();
    }

    private Gender resolveGender(String value) {
        if (value == null || value.trim().isEmpty()) return Gender.OTHER;
        try {
            return Gender.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return Gender.OTHER;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
