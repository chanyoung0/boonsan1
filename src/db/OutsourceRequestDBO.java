package db;

import enums.RequestStatus;
import model.accident.OutsourceRequest;
import model.partner.Partner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// OutsourceRequest 엔티티 DB 매핑 — outsource_request 테이블 CRUD 담당
// transferred_data 컬럼은 콤마 구분 문자열로 직렬화하여 저장한다.
public class OutsourceRequestDBO extends DBA {

    private static final PartnerDBO partnerDBO = new PartnerDBO();

    public OutsourceRequest findById(String requestId) {
        String sql = "SELECT request_id, request_status, request_datetime, result, "
                + "transferred_data, partner_id "
                + "FROM outsource_request WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRequest(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 위탁요청 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<OutsourceRequest> findAll() {
        String sql = "SELECT request_id, request_status, request_datetime, result, "
                + "transferred_data, partner_id "
                + "FROM outsource_request ORDER BY request_datetime DESC NULLS LAST, request_id";
        List<OutsourceRequest> requestList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                requestList.add(mapRequest(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 위탁요청 목록 조회 실패: " + e.getMessage());
        }
        return requestList;
    }

    public boolean save(OutsourceRequest request) {
        if (request == null) {
            return false;
        }
        String sql = "INSERT INTO outsource_request "
                + "(request_id, request_status, request_datetime, result, transferred_data, partner_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getRequestId());
            statement.setString(2, resolveStatusName(request.getRequestStatus()));
            statement.setTimestamp(3, toTimestamp(request.getRequestDateTime()));
            statement.setString(4, request.getResult());
            statement.setString(5, joinTransferredData(request.getTransferredDataList()));
            statement.setString(6, resolvePartnerId(request.getPartner()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 위탁요청 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(OutsourceRequest request) {
        if (request == null) {
            return false;
        }
        String sql = "UPDATE outsource_request SET "
                + "request_status = ?, request_datetime = ?, result = ?, transferred_data = ?, partner_id = ? "
                + "WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveStatusName(request.getRequestStatus()));
            statement.setTimestamp(2, toTimestamp(request.getRequestDateTime()));
            statement.setString(3, request.getResult());
            statement.setString(4, joinTransferredData(request.getTransferredDataList()));
            statement.setString(5, resolvePartnerId(request.getPartner()));
            statement.setString(6, request.getRequestId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 위탁요청 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String requestId) {
        String sql = "DELETE FROM outsource_request WHERE request_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requestId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 위탁요청 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private OutsourceRequest mapRequest(ResultSet resultSet) throws SQLException {
        Timestamp requestTs = resultSet.getTimestamp("request_datetime");
        String partnerId = resultSet.getString("partner_id");
        Partner partner = partnerId == null ? null : partnerDBO.findById(partnerId);

        OutsourceRequest request = new OutsourceRequest(
                resultSet.getString("request_id"),
                resolveStatus(resultSet.getString("request_status")),
                requestTs == null ? null : requestTs.toLocalDateTime(),
                partner
        );
        request.setResult(resultSet.getString("result"));
        request.setTransferredDataList(splitTransferredData(resultSet.getString("transferred_data")));
        return request;
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
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

    private String resolvePartnerId(Partner partner) {
        return partner == null ? null : partner.getId();
    }

    private String joinTransferredData(List<String> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }
        return String.join(",", dataList);
    }

    private List<String> splitTransferredData(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) {
            return result;
        }
        result.addAll(Arrays.asList(raw.split(",")));
        return result;
    }
}
