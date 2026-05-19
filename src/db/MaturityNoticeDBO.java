package db;

import enums.DeliveryMethod;
import model.contract.MaturityNotice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// MaturityNotice 엔티티 DB 매핑 — maturity_notice 테이블 CRUD 담당
public class MaturityNoticeDBO extends DBA {

    public MaturityNotice findById(String maturityNoticeId) {
        String sql = "SELECT maturity_notice_id, policy_number, delivery_method, sent_at, "
                + "renewal_intention, checked_at "
                + "FROM maturity_notice WHERE maturity_notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maturityNoticeId);
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
        String sql = "SELECT maturity_notice_id, policy_number, delivery_method, sent_at, "
                + "renewal_intention, checked_at "
                + "FROM maturity_notice ORDER BY sent_at DESC NULLS LAST, maturity_notice_id";
        List<MaturityNotice> noticeList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                noticeList.add(mapNotice(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 목록 조회 실패: " + e.getMessage());
        }
        return noticeList;
    }

    public boolean save(MaturityNotice notice) {
        if (notice == null) {
            return false;
        }
        String sql = "INSERT INTO maturity_notice "
                + "(maturity_notice_id, policy_number, delivery_method, sent_at, "
                + "renewal_intention, checked_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notice.getMaturityNoticeId());
            statement.setString(2, notice.getPolicyNumber());
            statement.setString(3, resolveMethodName(notice.getDeliveryMethod()));
            statement.setTimestamp(4, toTimestamp(notice.getSentAt()));
            setNullableBoolean(statement, 5, notice.getRenewalIntention());
            statement.setTimestamp(6, toTimestamp(notice.getCheckedAt()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(MaturityNotice notice) {
        if (notice == null) {
            return false;
        }
        String sql = "UPDATE maturity_notice SET "
                + "policy_number = ?, delivery_method = ?, sent_at = ?, "
                + "renewal_intention = ?, checked_at = ? "
                + "WHERE maturity_notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notice.getPolicyNumber());
            statement.setString(2, resolveMethodName(notice.getDeliveryMethod()));
            statement.setTimestamp(3, toTimestamp(notice.getSentAt()));
            setNullableBoolean(statement, 4, notice.getRenewalIntention());
            statement.setTimestamp(5, toTimestamp(notice.getCheckedAt()));
            statement.setString(6, notice.getMaturityNoticeId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String maturityNoticeId) {
        String sql = "DELETE FROM maturity_notice WHERE maturity_notice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maturityNoticeId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 만기 안내 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private MaturityNotice mapNotice(ResultSet resultSet) throws SQLException {
        MaturityNotice notice = new MaturityNotice(
                resolveMethod(resultSet.getString("delivery_method")),
                toLocalDateTime(resultSet.getTimestamp("sent_at"))
        );
        notice.setMaturityNoticeId(resultSet.getString("maturity_notice_id"));
        notice.setPolicyNumber(resultSet.getString("policy_number"));

        boolean intention = resultSet.getBoolean("renewal_intention");
        notice.setRenewalIntention(resultSet.wasNull() ? null : intention);
        notice.setCheckedAt(toLocalDateTime(resultSet.getTimestamp("checked_at")));
        return notice;
    }

    private void setNullableBoolean(PreparedStatement statement, int index, Boolean value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BOOLEAN);
        } else {
            statement.setBoolean(index, value);
        }
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveMethodName(DeliveryMethod method) {
        return method == null ? null : method.name();
    }

    private DeliveryMethod resolveMethod(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return DeliveryMethod.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
