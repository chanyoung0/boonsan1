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

// UnderwritingRequest entity database operator for underwriting_request CRUD.
public class UnderwritingRequestDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "request_id, request_reason, request_status, underwriting_type, "
                    + "underwriting_result, rejection_reason, surcharge_condition, "
                    + "applied_at, applied_id";

    public UnderwritingRequest findById(String requestId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting_request WHERE request_id = ?";
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
        List<UnderwritingRequest> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting_request ORDER BY request_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapRequest(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT request_id FROM underwriting_request ORDER BY request_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("request_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public boolean save(UnderwritingRequest request) {
        throw new UnsupportedOperationException("requestId 파라미터가 필요합니다.");
    }

    public boolean save(UnderwritingRequest request, String requestId) {
        if (request == null || requestId == null) {
            return false;
        }
        String sql = "INSERT INTO underwriting_request "
                + "(request_id, request_reason, request_status, underwriting_type, "
                + "underwriting_result, rejection_reason, surcharge_condition, "
                + "applied_at, applied_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            statement.setString(2, resolveRequestReasonName(request.getRequestReason()));
            statement.setString(3, resolveRequestStatusName(request.getRequestStatus()));
            statement.setString(4, resolveUnderwritingTypeName(request.getUnderwritingType()));
            statement.setString(5, resolveResultTypeName(request.getUnderwritingResult()));
            statement.setString(6, resolveRejectionReasonName(request.getRejectionReason()));
            statement.setString(7, resolveSurchargeConditionName(request.getSurchargeCondition()));
            statement.setTimestamp(8, toTimestamp(request.getAppliedAt()));
            statement.setTimestamp(9, toTimestamp(request.getAppliedId()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(UnderwritingRequest request) {
        throw new UnsupportedOperationException("requestId 파라미터가 필요합니다.");
    }

    public boolean update(UnderwritingRequest request, String requestId) {
        if (request == null || requestId == null) {
            return false;
        }
        String sql = "UPDATE underwriting_request SET "
                + "request_reason = ?, request_status = ?, underwriting_type = ?, "
                + "underwriting_result = ?, rejection_reason = ?, surcharge_condition = ?, "
                + "applied_at = ?, applied_id = ? "
                + "WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveRequestReasonName(request.getRequestReason()));
            statement.setString(2, resolveRequestStatusName(request.getRequestStatus()));
            statement.setString(3, resolveUnderwritingTypeName(request.getUnderwritingType()));
            statement.setString(4, resolveResultTypeName(request.getUnderwritingResult()));
            statement.setString(5, resolveRejectionReasonName(request.getRejectionReason()));
            statement.setString(6, resolveSurchargeConditionName(request.getSurchargeCondition()));
            statement.setTimestamp(7, toTimestamp(request.getAppliedAt()));
            statement.setTimestamp(8, toTimestamp(request.getAppliedId()));
            statement.setString(9, requestId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String requestId) {
        String sql = "DELETE FROM underwriting_request WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 요청 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private UnderwritingRequest mapRequest(ResultSet resultSet) throws SQLException {
        UnderwritingRequest request = new UnderwritingRequest();
        request.setRequestId(resultSet.getString("request_id"));
        request.setRequestReason(resolveRequestReason(resultSet.getString("request_reason")));
        request.setRequestStatus(resolveRequestStatus(resultSet.getString("request_status")));
        request.setUnderwritingType(resolveUnderwritingType(resultSet.getString("underwriting_type")));
        request.setUnderwritingResult(resolveResultType(resultSet.getString("underwriting_result")));
        request.setRejectionReason(resolveRejectionReason(resultSet.getString("rejection_reason")));
        request.setSurchargeCondition(resolveSurchargeCondition(resultSet.getString("surcharge_condition")));
        request.setAppliedAt(toLocalDateTime(resultSet.getTimestamp("applied_at")));
        request.setAppliedId(toLocalDateTime(resultSet.getTimestamp("applied_id")));
        return request;
    }

    private String resolveRequestReasonName(RequestReason value) {
        if (value == null) return RequestReason.NEW_APPLICATION.name();
        return value.name();
    }

    private RequestReason resolveRequestReason(String value) {
        if (value == null || value.trim().isEmpty()) return RequestReason.NEW_APPLICATION;
        try {
            return RequestReason.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return RequestReason.NEW_APPLICATION;
        }
    }

    private String resolveRequestStatusName(RequestStatus value) {
        if (value == null) return RequestStatus.PENDING.name();
        return value.name();
    }

    private RequestStatus resolveRequestStatus(String value) {
        if (value == null || value.trim().isEmpty()) return RequestStatus.PENDING;
        try {
            return RequestStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return RequestStatus.PENDING;
        }
    }

    private String resolveUnderwritingTypeName(UnderwritingType value) {
        if (value == null) return UnderwritingType.GENERAL.name();
        return value.name();
    }

    private UnderwritingType resolveUnderwritingType(String value) {
        if (value == null || value.trim().isEmpty()) return UnderwritingType.GENERAL;
        try {
            return UnderwritingType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return UnderwritingType.GENERAL;
        }
    }

    private String resolveResultTypeName(UnderwritingResultType value) {
        if (value == null) return UnderwritingResultType.PENDING.name();
        return value.name();
    }

    private UnderwritingResultType resolveResultType(String value) {
        if (value == null || value.trim().isEmpty()) return UnderwritingResultType.PENDING;
        try {
            return UnderwritingResultType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return UnderwritingResultType.PENDING;
        }
    }

    private String resolveRejectionReasonName(RejectionReason value) {
        if (value == null) return null;
        return value.name();
    }

    private RejectionReason resolveRejectionReason(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return RejectionReason.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveSurchargeConditionName(SurchargeCondition value) {
        if (value == null) return SurchargeCondition.NONE.name();
        return value.name();
    }

    private SurchargeCondition resolveSurchargeCondition(String value) {
        if (value == null || value.trim().isEmpty()) return SurchargeCondition.NONE;
        try {
            return SurchargeCondition.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return SurchargeCondition.NONE;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
