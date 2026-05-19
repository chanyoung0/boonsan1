package db;

import enums.PaymentMethod;
import model.contract.UnpaidNotice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// UnpaidNotice 엔티티 DB 매핑 — unpaid_notice 테이블 CRUD 담당
public class UnpaidNoticeDBO extends DBA {

    public UnpaidNotice findById(String unpaidNoticeId) {
        String sql = "SELECT unpaid_notice_id, payment_collection_id, unpaid_amount, due_date, "
                + "payment_method, sent_at "
                + "FROM unpaid_notice WHERE unpaid_notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, unpaidNoticeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapNotice(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 미납 안내 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<UnpaidNotice> findAll() {
        String sql = "SELECT unpaid_notice_id, payment_collection_id, unpaid_amount, due_date, "
                + "payment_method, sent_at "
                + "FROM unpaid_notice ORDER BY sent_at DESC NULLS LAST, unpaid_notice_id";
        List<UnpaidNotice> noticeList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                noticeList.add(mapNotice(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 미납 안내 목록 조회 실패: " + e.getMessage());
        }
        return noticeList;
    }

    public boolean save(UnpaidNotice notice) {
        if (notice == null) {
            return false;
        }
        String sql = "INSERT INTO unpaid_notice "
                + "(unpaid_notice_id, payment_collection_id, unpaid_amount, due_date, payment_method, sent_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notice.getUnpaidNoticeId());
            statement.setString(2, notice.getPaymentCollectionId());
            statement.setBigDecimal(3, notice.getUnpaidAmount());
            statement.setTimestamp(4, toTimestamp(notice.getDueDate()));
            statement.setString(5, resolveMethodName(notice.getPaymentMethod()));
            statement.setTimestamp(6, toTimestamp(notice.getSentAt()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 미납 안내 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(UnpaidNotice notice) {
        if (notice == null) {
            return false;
        }
        String sql = "UPDATE unpaid_notice SET "
                + "payment_collection_id = ?, unpaid_amount = ?, due_date = ?, payment_method = ?, sent_at = ? "
                + "WHERE unpaid_notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notice.getPaymentCollectionId());
            statement.setBigDecimal(2, notice.getUnpaidAmount());
            statement.setTimestamp(3, toTimestamp(notice.getDueDate()));
            statement.setString(4, resolveMethodName(notice.getPaymentMethod()));
            statement.setTimestamp(5, toTimestamp(notice.getSentAt()));
            statement.setString(6, notice.getUnpaidNoticeId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 미납 안내 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String unpaidNoticeId) {
        String sql = "DELETE FROM unpaid_notice WHERE unpaid_notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, unpaidNoticeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 미납 안내 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private UnpaidNotice mapNotice(ResultSet resultSet) throws SQLException {
        UnpaidNotice notice = new UnpaidNotice(
                resultSet.getBigDecimal("unpaid_amount"),
                toLocalDateTime(resultSet.getTimestamp("due_date")),
                resolveMethod(resultSet.getString("payment_method")),
                toLocalDateTime(resultSet.getTimestamp("sent_at"))
        );
        notice.setUnpaidNoticeId(resultSet.getString("unpaid_notice_id"));
        notice.setPaymentCollectionId(resultSet.getString("payment_collection_id"));
        return notice;
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveMethodName(PaymentMethod method) {
        return method == null ? null : method.name();
    }

    private PaymentMethod resolveMethod(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return PaymentMethod.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
