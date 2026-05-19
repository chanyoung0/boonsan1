package db;

import model.insurance.Authorization;
import model.insurance.FinancialSupervisoryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Authorization 엔티티 DB 매핑 — product_authorization 테이블 CRUD 담당
public class AuthorizationDBO extends DBA {

    private static final String SELECT_COLS =
            "SELECT request_id, product_code, request_reason, submission_agency_name, "
            + "requested_at, approved_at, is_approved FROM product_authorization";

    public Authorization findById(String requestId) {
        String sql = SELECT_COLS + " WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAuthorization(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 인가 요청 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Authorization> findAll() {
        List<Authorization> list = new ArrayList<>();
        String sql = SELECT_COLS + " ORDER BY requested_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapAuthorization(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 인가 요청 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public boolean save(Authorization authorization) {
        String sql = "INSERT INTO product_authorization "
                + "(request_id, product_code, request_reason, submission_agency_name, requested_at, approved_at, is_approved) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authorization.getRequestId());
            stmt.setString(2, authorization.getProductCode());
            stmt.setString(3, authorization.getRequestReason());
            stmt.setString(4, authorization.getSubmissionAgencyName());
            stmt.setTimestamp(5, toTimestamp(authorization.getRequestedAt()));
            stmt.setTimestamp(6, toTimestamp(authorization.getApprovedAt()));
            stmt.setBoolean(7, authorization.isApproved());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 인가 요청 등록 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Authorization authorization) {
        String sql = "UPDATE product_authorization SET is_approved=?, approved_at=? WHERE request_id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, authorization.isApproved());
            stmt.setTimestamp(2, toTimestamp(authorization.getApprovedAt()));
            stmt.setString(3, authorization.getRequestId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 인가 요청 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String requestId) {
        String sql = "DELETE FROM product_authorization WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 인가 요청 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Authorization mapAuthorization(ResultSet rs) throws SQLException {
        Authorization auth = new Authorization();
        auth.setRequestId(rs.getString("request_id"));
        auth.setProductCode(rs.getString("product_code"));
        auth.setRequestReason(rs.getString("request_reason"));
        auth.setSubmissionAgencyName(rs.getString("submission_agency_name"));
        Timestamp requestedAt = rs.getTimestamp("requested_at");
        if (requestedAt != null) {
            auth.setRequestedAt(requestedAt.toLocalDateTime());
        }
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) {
            auth.setApprovedAt(approvedAt.toLocalDateTime());
        }
        auth.setApproved(rs.getBoolean("is_approved"));
        auth.setFinancialSupervisoryService(new FinancialSupervisoryService("FSS", "금융감독원"));
        return auth;
    }

    private Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }
}
