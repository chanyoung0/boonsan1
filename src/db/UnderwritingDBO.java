package db;

import enums.UnderwritingStatus;
import enums.UnderwritingTerm;
import enums.UnderwritingType;
import model.underwriting.Underwriting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Underwriting 엔티티 DB 매핑 — underwriting 테이블 CRUD 담당
public class UnderwritingDBO extends DBA {

    public Underwriting findById(String underwritingId) {
        String sql = "SELECT underwriting_id, application_id, underwriter, underwriting_type, "
                + "underwriting_status, underwriting_item, underwritten_at, total_score, "
                + "itemized_scores, deduction_reason, underwriting_opinion, is_coinsurance_recommended "
                + "FROM underwriting WHERE underwriting_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwritingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUnderwriting(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Underwriting> findAll() {
        String sql = "SELECT underwriting_id, application_id, underwriter, underwriting_type, "
                + "underwriting_status, underwriting_item, underwritten_at, total_score, "
                + "itemized_scores, deduction_reason, underwriting_opinion, is_coinsurance_recommended "
                + "FROM underwriting ORDER BY underwritten_at DESC NULLS LAST, underwriting_id";
        List<Underwriting> underwritingList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                underwritingList.add(mapUnderwriting(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 목록 조회 실패: " + e.getMessage());
        }
        return underwritingList;
    }

    public boolean save(Underwriting underwriting) {
        if (underwriting == null) {
            return false;
        }
        String sql = "INSERT INTO underwriting "
                + "(underwriting_id, application_id, underwriter, underwriting_type, "
                + "underwriting_status, underwriting_item, underwritten_at, total_score, "
                + "itemized_scores, deduction_reason, underwriting_opinion, is_coinsurance_recommended) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setUnderwritingParams(statement, underwriting);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Underwriting underwriting) {
        if (underwriting == null) {
            return false;
        }
        String sql = "UPDATE underwriting SET "
                + "application_id = ?, underwriter = ?, underwriting_type = ?, underwriting_status = ?, "
                + "underwriting_item = ?, underwritten_at = ?, total_score = ?, itemized_scores = ?, "
                + "deduction_reason = ?, underwriting_opinion = ?, is_coinsurance_recommended = ? "
                + "WHERE underwriting_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwriting.getApplicationId());
            statement.setString(2, underwriting.getUnderwriter());
            statement.setString(3, resolveTypeName(underwriting.getUnderwritingType()));
            statement.setString(4, resolveStatusName(underwriting.getUnderwritingStatus()));
            statement.setString(5, resolveItemName(underwriting.getUnderwritingItem()));
            statement.setTimestamp(6, toTimestamp(underwriting.getUnderwrittenAt()));
            statement.setFloat(7, underwriting.getTotalScore());
            statement.setString(8, underwriting.getItemizedScores());
            statement.setString(9, underwriting.getDeductionReason());
            statement.setString(10, underwriting.getUnderwritingOpinion());
            statement.setBoolean(11, underwriting.isCoinsuranceRecommended());
            statement.setString(12, underwriting.getUnderwritingId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String underwritingId) {
        String sql = "DELETE FROM underwriting WHERE underwriting_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwritingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Underwriting mapUnderwriting(ResultSet resultSet) throws SQLException {
        Underwriting underwriting = new Underwriting(
                resultSet.getString("underwriter"),
                resolveType(resultSet.getString("underwriting_type")),
                resolveStatus(resultSet.getString("underwriting_status")),
                resolveItem(resultSet.getString("underwriting_item")),
                toLocalDateTime(resultSet.getTimestamp("underwritten_at"))
        );
        underwriting.setUnderwritingId(resultSet.getString("underwriting_id"));
        underwriting.setApplicationId(resultSet.getString("application_id"));
        underwriting.setTotalScore(resultSet.getFloat("total_score"));
        underwriting.setItemizedScores(resultSet.getString("itemized_scores"));
        underwriting.setDeductionReason(resultSet.getString("deduction_reason"));
        underwriting.setUnderwritingOpinion(resultSet.getString("underwriting_opinion"));
        underwriting.setCoinsuranceRecommended(resultSet.getBoolean("is_coinsurance_recommended"));
        return underwriting;
    }

    private void setUnderwritingParams(PreparedStatement statement, Underwriting underwriting) throws SQLException {
        statement.setString(1, underwriting.getUnderwritingId());
        statement.setString(2, underwriting.getApplicationId());
        statement.setString(3, underwriting.getUnderwriter());
        statement.setString(4, resolveTypeName(underwriting.getUnderwritingType()));
        statement.setString(5, resolveStatusName(underwriting.getUnderwritingStatus()));
        statement.setString(6, resolveItemName(underwriting.getUnderwritingItem()));
        statement.setTimestamp(7, toTimestamp(underwriting.getUnderwrittenAt()));
        statement.setFloat(8, underwriting.getTotalScore());
        statement.setString(9, underwriting.getItemizedScores());
        statement.setString(10, underwriting.getDeductionReason());
        statement.setString(11, underwriting.getUnderwritingOpinion());
        statement.setBoolean(12, underwriting.isCoinsuranceRecommended());
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
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

    private String resolveStatusName(UnderwritingStatus status) {
        return status == null ? null : status.name();
    }

    private UnderwritingStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return UnderwritingStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveItemName(UnderwritingTerm item) {
        return item == null ? null : item.name();
    }

    private UnderwritingTerm resolveItem(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return UnderwritingTerm.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
