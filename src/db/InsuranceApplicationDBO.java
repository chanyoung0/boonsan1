package db;

import enums.ApplicationStatus;
import enums.SpecialContractType;
import model.underwriting.InsuranceApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// InsuranceApplication 엔티티 DB 매핑 — insurance_application 테이블 CRUD 담당
public class InsuranceApplicationDBO extends DBA {

    public InsuranceApplication findById(String applicationId) {
        String sql = "SELECT application_id, product_code, insured_person_info, insured_amount, premium, "
                + "payment_cycle, special_contract_type, terms_version, applied_condition, "
                + "application_status, applied_at "
                + "FROM insurance_application WHERE application_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, applicationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapApplication(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 청약 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<InsuranceApplication> findAll() {
        String sql = "SELECT application_id, product_code, insured_person_info, insured_amount, premium, "
                + "payment_cycle, special_contract_type, terms_version, applied_condition, "
                + "application_status, applied_at "
                + "FROM insurance_application ORDER BY applied_at DESC NULLS LAST, application_id";
        List<InsuranceApplication> applicationList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                applicationList.add(mapApplication(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 청약 목록 조회 실패: " + e.getMessage());
        }
        return applicationList;
    }

    public boolean save(InsuranceApplication application) {
        if (application == null) {
            return false;
        }
        String sql = "INSERT INTO insurance_application "
                + "(application_id, product_code, insured_person_info, insured_amount, premium, "
                + "payment_cycle, special_contract_type, terms_version, applied_condition, "
                + "application_status, applied_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setApplicationParams(statement, application);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 청약 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(InsuranceApplication application) {
        if (application == null) {
            return false;
        }
        String sql = "UPDATE insurance_application SET "
                + "product_code = ?, insured_person_info = ?, insured_amount = ?, premium = ?, "
                + "payment_cycle = ?, special_contract_type = ?, terms_version = ?, applied_condition = ?, "
                + "application_status = ?, applied_at = ? "
                + "WHERE application_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, application.getProductCode());
            statement.setString(2, application.getInsuredPersonInfo());
            statement.setBigDecimal(3, application.getInsuredAmount());
            statement.setBigDecimal(4, application.getPremium());
            statement.setString(5, application.getPaymentCycle());
            statement.setString(6, resolveSpecialContractTypeName(application.getSpecialContractList()));
            statement.setString(7, application.getTermsVersion());
            statement.setString(8, application.getAppliedCondition());
            statement.setString(9, resolveStatusName(application.getApplicationStatus()));
            statement.setTimestamp(10, toTimestamp(application.getAppliedAt()));
            statement.setString(11, application.getApplicationId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 청약 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String applicationId) {
        String sql = "DELETE FROM insurance_application WHERE application_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, applicationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 청약 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private InsuranceApplication mapApplication(ResultSet resultSet) throws SQLException {
        InsuranceApplication application = new InsuranceApplication(
                resultSet.getString("product_code"),
                resultSet.getString("insured_person_info"),
                resultSet.getBigDecimal("insured_amount"),
                resultSet.getBigDecimal("premium"),
                resultSet.getString("payment_cycle"),
                resolveSpecialContractType(resultSet.getString("special_contract_type")),
                resultSet.getString("terms_version"),
                resultSet.getString("applied_condition")
        );
        application.setApplicationId(resultSet.getString("application_id"));
        application.setApplicationStatus(resolveStatus(resultSet.getString("application_status")));
        application.setAppliedAt(toLocalDateTime(resultSet.getTimestamp("applied_at")));
        return application;
    }

    private void setApplicationParams(PreparedStatement statement, InsuranceApplication application) throws SQLException {
        statement.setString(1, application.getApplicationId());
        statement.setString(2, application.getProductCode());
        statement.setString(3, application.getInsuredPersonInfo());
        statement.setBigDecimal(4, application.getInsuredAmount());
        statement.setBigDecimal(5, application.getPremium());
        statement.setString(6, application.getPaymentCycle());
        statement.setString(7, resolveSpecialContractTypeName(application.getSpecialContractList()));
        statement.setString(8, application.getTermsVersion());
        statement.setString(9, application.getAppliedCondition());
        statement.setString(10, resolveStatusName(application.getApplicationStatus()));
        statement.setTimestamp(11, toTimestamp(application.getAppliedAt()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveStatusName(ApplicationStatus status) {
        return status == null ? null : status.name();
    }

    private ApplicationStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ApplicationStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveSpecialContractTypeName(SpecialContractType type) {
        return type == null ? null : type.name();
    }

    private SpecialContractType resolveSpecialContractType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return SpecialContractType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
