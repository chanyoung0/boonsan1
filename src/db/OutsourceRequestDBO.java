package db;

import enums.RequestStatus;
import model.accident.OutsourceRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// OutsourceRequest entity database operator for outsource_request CRUD.
public class OutsourceRequestDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "request_id, investigation_id, partner_id, request_status, result, request_datetime";

    public OutsourceRequest findById(String requestId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM outsource_request WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRequest(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 외부 조사 의뢰 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<OutsourceRequest> findAll() {
        List<OutsourceRequest> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM outsource_request ORDER BY request_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapRequest(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 외부 조사 의뢰 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT request_id FROM outsource_request ORDER BY request_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("request_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 외부 조사 의뢰 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public List<OutsourceRequest> findByInvestigationId(String investigationId) {
        List<OutsourceRequest> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM outsource_request WHERE investigation_id = ? ORDER BY request_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, investigationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapRequest(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 외부 조사 의뢰 조회 실패 (조사ID): " + e.getMessage());
        }
        return list;
    }

    public boolean save(OutsourceRequest request) {
        throw new UnsupportedOperationException("investigationId, partnerId, requestStatus 파라미터가 필요합니다.");
    }

    public boolean save(OutsourceRequest request, String investigationId,
                        String partnerId, String requestStatus) {
        if (request == null || request.getRequestId() == null) {
            return false;
        }
        String sql = "INSERT INTO outsource_request "
                + "(request_id, investigation_id, partner_id, request_status, result, request_datetime) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getRequestId());
            statement.setString(2, investigationId);
            statement.setString(3, partnerId);
            statement.setString(4, resolveRequestStatusName(requestStatus));
            statement.setString(5, request.getResult());
            statement.setTimestamp(6, toTimestamp(request.getRequestDateTime()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 외부 조사 의뢰 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(OutsourceRequest request) {
        throw new UnsupportedOperationException("requestStatus 파라미터가 필요합니다.");
    }

    public boolean update(OutsourceRequest request, String requestStatus) {
        if (request == null || request.getRequestId() == null) {
            return false;
        }
        String sql = "UPDATE outsource_request SET "
                + "request_status = ?, result = ?, request_datetime = ? "
                + "WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveRequestStatusName(requestStatus));
            statement.setString(2, request.getResult());
            statement.setTimestamp(3, toTimestamp(request.getRequestDateTime()));
            statement.setString(4, request.getRequestId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 외부 조사 의뢰 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String requestId) {
        String sql = "DELETE FROM outsource_request WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 외부 조사 의뢰 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private OutsourceRequest mapRequest(ResultSet resultSet) throws SQLException {
        OutsourceRequest request = new OutsourceRequest();
        request.setRequestId(resultSet.getString("request_id"));
        request.setInvestigationId(resultSet.getString("investigation_id"));
        request.setPartnerId(resultSet.getString("partner_id"));
        request.setRequestStatus(resolveRequestStatus(resultSet.getString("request_status")));
        request.setResult(resultSet.getString("result"));
        request.setRequestDateTime(toLocalDateTime(resultSet.getTimestamp("request_datetime")));
        return request;
    }

    private String resolveRequestStatusName(String value) {
        if ("PENDING".equals(value) || "IN_PROGRESS".equals(value)
                || "COMPLETED".equals(value) || "CANCELLED".equals(value)) {
            return value;
        }
        return RequestStatus.PENDING.name();
    }

    private RequestStatus resolveRequestStatus(String value) {
        if (value == null || value.trim().isEmpty()) return RequestStatus.PENDING;
        try {
            return RequestStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return RequestStatus.PENDING;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
