package db;

import enums.RejectionReason;
import enums.RequestReason;
import enums.RequestStatus;
import enums.SurchargeCondition;
import enums.UnderwritingResultType;
import enums.UnderwritingType;
import model.underwriting.UnderwritingRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// UnderwritingRequest 엔티티 DB 매핑 — underwriting_request 테이블 CRUD 담당
public class UnderwritingRequestDBO extends DBA {

    public UnderwritingRequest findById(String requestId) {
        String sql = "SELECT request_id, policy_number, request_reason, request_status, underwriting_type, "
                + "underwriting_result, surcharge_condition, rejection_reason, applied_at "
                + "FROM underwriting_request WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRequest(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<UnderwritingRequest> findAll() {
        String sql = "SELECT request_id, policy_number, request_reason, request_status, underwriting_type, "
                + "underwriting_result, surcharge_condition, rejection_reason, applied_at "
                + "FROM underwriting_request ORDER BY applied_at DESC NULLS LAST, request_id";
        List<UnderwritingRequest> requestList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                requestList.add(mapRequest(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 목록 조회 실패: " + e.getMessage());
        }
        return requestList;
    }

    public boolean save(UnderwritingRequest request) {
        if (request == null) {
            return false;
        }
        String sql = "INSERT INTO underwriting_request "
                + "(request_id, policy_number, request_reason, request_status, underwriting_type, "
                + "underwriting_result, surcharge_condition, rejection_reason, applied_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setRequestParams(statement, request);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(UnderwritingRequest request) {
        if (request == null) {
            return false;
        }
        String sql = "UPDATE underwriting_request SET "
                + "policy_number = ?, request_reason = ?, request_status = ?, underwriting_type = ?, "
                + "underwriting_result = ?, surcharge_condition = ?, rejection_reason = ?, applied_at = ? "
                + "WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getPolicyNumber());
            statement.setString(2, resolveReasonName(request.getRequestReason()));
            statement.setString(3, resolveStatusName(request.getRequestStatus()));
            statement.setString(4, resolveTypeName(request.getUnderwritingType()));
            statement.setString(5, resolveResultName(request.getUnderwritingResult()));
            statement.setString(6, resolveSurchargeName(request.getSurchargeCondition()));
            statement.setString(7, resolveRejectionName(request.getRejectionReason()));
            statement.setTimestamp(8, toTimestamp(request.getAppliedAt()));
            statement.setString(9, request.getRequestId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String requestId) {
        String sql = "DELETE FROM underwriting_request WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private UnderwritingRequest mapRequest(ResultSet resultSet) throws SQLException {
        UnderwritingRequest request = new UnderwritingRequest(
                toLocalDateTime(resultSet.getTimestamp("applied_at")),
                resolveReason(resultSet.getString("request_reason")),
                resolveType(resultSet.getString("underwriting_type")),
                resolveStatus(resultSet.getString("request_status"))
        );
        request.setRequestId(resultSet.getString("request_id"));
        request.setPolicyNumber(resultSet.getString("policy_number"));
        request.setUnderwritingResult(resolveResult(resultSet.getString("underwriting_result")));
        request.setSurchargeCondition(resolveSurcharge(resultSet.getString("surcharge_condition")));
        request.setRejectionReason(resolveRejection(resultSet.getString("rejection_reason")));
        return request;
    }

    private void setRequestParams(PreparedStatement statement, UnderwritingRequest request) throws SQLException {
        statement.setString(1, request.getRequestId());
        statement.setString(2, request.getPolicyNumber());
        statement.setString(3, resolveReasonName(request.getRequestReason()));
        statement.setString(4, resolveStatusName(request.getRequestStatus()));
        statement.setString(5, resolveTypeName(request.getUnderwritingType()));
        statement.setString(6, resolveResultName(request.getUnderwritingResult()));
        statement.setString(7, resolveSurchargeName(request.getSurchargeCondition()));
        statement.setString(8, resolveRejectionName(request.getRejectionReason()));
        statement.setTimestamp(9, toTimestamp(request.getAppliedAt()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveReasonName(RequestReason reason) {
        return reason == null ? null : reason.name();
    }

    private RequestReason resolveReason(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return RequestReason.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveStatusName(RequestStatus status) {
        return status == null ? RequestStatus.PENDING.name() : status.name();
    }

    private RequestStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return RequestStatus.PENDING;
        }
        try {
            return RequestStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return RequestStatus.PENDING;
        }
    }

    private String resolveTypeName(UnderwritingType type) {
        return type == null ? null : type.name();
    }

    private UnderwritingType resolveType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return UnderwritingType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveResultName(UnderwritingResultType result) {
        return result == null ? null : result.name();
    }

    private UnderwritingResultType resolveResult(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return UnderwritingResultType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveSurchargeName(SurchargeCondition condition) {
        return condition == null ? null : condition.name();
    }

    private SurchargeCondition resolveSurcharge(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return SurchargeCondition.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveRejectionName(RejectionReason rejection) {
        return rejection == null ? null : rejection.name();
    }

    private RejectionReason resolveRejection(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return RejectionReason.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
