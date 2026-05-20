package db;

import enums.ApplicationStatus;
import model.underwriting.InsuranceApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// InsuranceApplication entity database operator for insurance_application CRUD.
public class InsuranceApplicationDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "application_id, policy_number, application_status, applied_condition, applied_at";

    public InsuranceApplication findById(String applicationId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insurance_application WHERE application_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, applicationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapApplication(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 청약 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<InsuranceApplication> findAll() {
        List<InsuranceApplication> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insurance_application ORDER BY application_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapApplication(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 청약 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public boolean save(InsuranceApplication application) {
        throw new UnsupportedOperationException("policyNumber, applicationStatus, appliedCondition 파라미터가 필요합니다.");
    }

    public boolean save(InsuranceApplication application, String policyNumber,
                        String applicationStatus, String appliedCondition) {
        if (application == null || application.getApplicationId() == null) {
            return false;
        }
        String sql = "INSERT INTO insurance_application "
                + "(application_id, policy_number, application_status, applied_condition, applied_at) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, application, policyNumber, applicationStatus, appliedCondition);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 청약 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(InsuranceApplication application) {
        throw new UnsupportedOperationException("policyNumber, applicationStatus 파라미터가 필요합니다.");
    }

    public boolean update(InsuranceApplication application, String policyNumber, String applicationStatus) {
        if (application == null || application.getApplicationId() == null) {
            return false;
        }
        String sql = "UPDATE insurance_application SET "
                + "policy_number = ?, application_status = ?, applied_condition = ?, applied_at = ? "
                + "WHERE application_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            statement.setString(2, resolveStatusName(applicationStatus));
            statement.setString(3, application.getAppliedCondition());
            statement.setTimestamp(4, toTimestamp(application.getAppliedAt()));
            statement.setString(5, application.getApplicationId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 청약 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String applicationId) {
        String sql = "DELETE FROM insurance_application WHERE application_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, applicationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 청약 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    public String findStatusById(String applicationId) {
        return findStringColumnById(applicationId, "application_status");
    }

    public String findPolicyNumberById(String applicationId) {
        return findStringColumnById(applicationId, "policy_number");
    }

    private String findStringColumnById(String applicationId, String columnName) {
        String sql = "SELECT " + columnName + " FROM insurance_application WHERE application_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, applicationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 청약 부가정보 조회 실패: " + e.getMessage());
        }
        return null;
    }

    private InsuranceApplication mapApplication(ResultSet resultSet) throws SQLException {
        InsuranceApplication application = new InsuranceApplication();
        application.setApplicationId(resultSet.getString("application_id"));
        application.setPolicyNumber(resultSet.getString("policy_number"));
        application.setApplicationStatus(resolveStatus(resultSet.getString("application_status")));
        application.setAppliedCondition(resultSet.getString("applied_condition"));
        application.setAppliedAt(toLocalDateTime(resultSet.getTimestamp("applied_at")));
        return application;
    }

    private void setSaveParams(PreparedStatement statement, InsuranceApplication application,
                               String policyNumber, String applicationStatus,
                               String appliedCondition) throws SQLException {
        statement.setString(1, application.getApplicationId());
        statement.setString(2, policyNumber);
        statement.setString(3, resolveStatusName(applicationStatus));
        statement.setString(4, appliedCondition);
        statement.setTimestamp(5, toTimestamp(application.getAppliedAt()));
    }

    private String resolveStatusName(String status) {
        if ("PENDING".equals(status) || "APPROVED".equals(status)
                || "REJECTED".equals(status) || "CANCELLED".equals(status)) {
            return status;
        }
        return ApplicationStatus.PENDING.name();
    }

    private ApplicationStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ApplicationStatus.PENDING;
        }
        try {
            return ApplicationStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return ApplicationStatus.PENDING;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
