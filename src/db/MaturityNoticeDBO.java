package db;

import enums.DeliveryMethod;
import model.contract.MaturityNotice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// MaturityNotice entity database operator for maturity_notice CRUD.
public class MaturityNoticeDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "notice_id, delivery_method, sent_at, checked_at, renewal_intention";

    public MaturityNotice findById(String noticeId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM maturity_notice WHERE notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noticeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapNotice(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<MaturityNotice> findAll() {
        List<MaturityNotice> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM maturity_notice ORDER BY notice_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapNotice(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT notice_id FROM maturity_notice ORDER BY notice_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("notice_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 ID 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public boolean save(MaturityNotice notice) {
        throw new UnsupportedOperationException("noticeId 파라미터가 필요합니다.");
    }

    public boolean save(MaturityNotice notice, String noticeId) {
        if (notice == null || noticeId == null) {
            return false;
        }
        String sql = "INSERT INTO maturity_notice "
                + "(notice_id, delivery_method, sent_at, checked_at, renewal_intention) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, notice, noticeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(MaturityNotice notice) {
        throw new UnsupportedOperationException("noticeId 파라미터가 필요합니다.");
    }

    public boolean update(MaturityNotice notice, String noticeId) {
        if (notice == null || noticeId == null) {
            return false;
        }
        String sql = "UPDATE maturity_notice SET "
                + "delivery_method = ?, sent_at = ?, checked_at = ?, renewal_intention = ? "
                + "WHERE notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveDeliveryMethodName(notice));
            statement.setTimestamp(2, toTimestamp(notice.getSentAt()));
            statement.setTimestamp(3, toTimestamp(notice.getCheckedAt()));
            statement.setString(4, resolveRenewalIntention(notice.getRenewalIntention()));
            statement.setString(5, noticeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String noticeId) {
        String sql = "DELETE FROM maturity_notice WHERE notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noticeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    public String findRenewalIntentionById(String noticeId) {
        return findStringColumnById(noticeId, "renewal_intention");
    }

    private String findStringColumnById(String noticeId, String columnName) {
        String sql = "SELECT " + columnName + " FROM maturity_notice WHERE notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noticeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 부가정보 조회 실패: " + e.getMessage());
        }
        return null;
    }

    private MaturityNotice mapNotice(ResultSet resultSet) throws SQLException {
        MaturityNotice notice = new MaturityNotice();
        notice.setNoticeId(resultSet.getString("notice_id"));
        notice.setDeliveryMethod(resolveDeliveryMethod(resultSet.getString("delivery_method")));
        notice.setSentAt(toLocalDateTime(resultSet.getTimestamp("sent_at")));
        notice.setCheckedAt(toLocalDateTime(resultSet.getTimestamp("checked_at")));
        notice.setRenewalIntention(resolveRenewalIntentionValue(resultSet.getString("renewal_intention")));
        return notice;
    }

    private void setSaveParams(PreparedStatement statement, MaturityNotice notice,
                               String noticeId) throws SQLException {
        statement.setString(1, noticeId);
        statement.setString(2, resolveDeliveryMethodName(notice));
        statement.setTimestamp(3, toTimestamp(notice.getSentAt()));
        statement.setTimestamp(4, toTimestamp(notice.getCheckedAt()));
        statement.setString(5, resolveRenewalIntention(notice.getRenewalIntention()));
    }

    private String resolveDeliveryMethodName(MaturityNotice notice) {
        if (notice == null || notice.getDeliveryMethod() == null) {
            return DeliveryMethod.SMS.name();
        }
        return notice.getDeliveryMethod().name();
    }

    private DeliveryMethod resolveDeliveryMethod(String value) {
        if (value == null || value.trim().isEmpty()) {
            return DeliveryMethod.SMS;
        }
        try {
            return DeliveryMethod.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return DeliveryMethod.SMS;
        }
    }

    private String resolveRenewalIntention(Boolean value) {
        if (value == null) return null;
        return value ? "YES" : "NO";
    }

    private Boolean resolveRenewalIntentionValue(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return "YES".equalsIgnoreCase(value.trim());
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
