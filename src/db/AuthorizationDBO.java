package db;

import model.insurance.Authorization;
import model.insurance.FinancialSupervisoryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// Authorization entity database operator for authorization_request CRUD.
public class AuthorizationDBO extends DBA {

    private static final String TABLE_NAME = "authorization_request";
    private static final String SELECT_COLUMNS =
            "request_id, product_code, request_reason, submission_agency_name, "
                    + "requested_at, approved_at, is_approved";

    public Authorization findById(String requestId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM " + TABLE_NAME + " WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAuthorization(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 상품 인가요청 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Authorization> findAll() {
        List<Authorization> authorizationList = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM " + TABLE_NAME + " ORDER BY request_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                authorizationList.add(mapAuthorization(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 상품 인가요청 목록 조회 실패: " + e.getMessage());
        }
        return authorizationList;
    }

    public boolean save(Authorization authorization) {
        return save(authorization, null);
    }

    public boolean save(Authorization authorization, String productCode) {
        String sql = "INSERT INTO " + TABLE_NAME + " "
                + "(request_id, product_code, request_reason, submission_agency_name, "
                + "requested_at, approved_at, is_approved) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setAuthorizationParams(statement, authorization, productCode);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 상품 인가요청 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Authorization authorization) {
        String sql = "UPDATE " + TABLE_NAME + " SET "
                + "request_reason = ?, submission_agency_name = ?, requested_at = ?, "
                + "approved_at = ?, is_approved = ? WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, authorization.getRequestReason());
            statement.setString(2, authorization.getSubmissionAgencyName());
            statement.setTimestamp(3, toTimestamp(authorization.getRequestedAt()));
            statement.setTimestamp(4, toTimestamp(authorization.getApprovedAt()));
            statement.setBoolean(5, authorization.isApproved());
            statement.setString(6, authorization.getRequestId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 상품 인가요청 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Authorization authorization, String productCode) {
        String sql = "UPDATE " + TABLE_NAME + " SET "
                + "product_code = ?, request_reason = ?, submission_agency_name = ?, requested_at = ?, "
                + "approved_at = ?, is_approved = ? WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productCode);
            statement.setString(2, authorization.getRequestReason());
            statement.setString(3, authorization.getSubmissionAgencyName());
            statement.setTimestamp(4, toTimestamp(authorization.getRequestedAt()));
            statement.setTimestamp(5, toTimestamp(authorization.getApprovedAt()));
            statement.setBoolean(6, authorization.isApproved());
            statement.setString(7, authorization.getRequestId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 상품 인가요청 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String requestId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 상품 인가요청 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private Authorization mapAuthorization(ResultSet resultSet) throws SQLException {
        String submissionAgencyName = resultSet.getString("submission_agency_name");
        FinancialSupervisoryService supervisoryService =
                new FinancialSupervisoryService("FSS", submissionAgencyName);
        Authorization authorization = new Authorization(
                resultSet.getString("request_id"),
                resultSet.getString("request_reason"),
                submissionAgencyName,
                toLocalDateTime(resultSet.getTimestamp("requested_at")),
                supervisoryService
        );
        authorization.setApprovedAt(toLocalDateTime(resultSet.getTimestamp("approved_at")));
        authorization.setApproved(resultSet.getBoolean("is_approved"));
        return authorization;
    }

    private void setAuthorizationParams(PreparedStatement statement, Authorization authorization,
                                        String productCode) throws SQLException {
        statement.setString(1, authorization.getRequestId());
        statement.setString(2, productCode);
        statement.setString(3, authorization.getRequestReason());
        statement.setString(4, authorization.getSubmissionAgencyName());
        statement.setTimestamp(5, toTimestamp(authorization.getRequestedAt()));
        statement.setTimestamp(6, toTimestamp(authorization.getApprovedAt()));
        statement.setBoolean(7, authorization.isApproved());
    }

    private Timestamp toTimestamp(java.time.LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
