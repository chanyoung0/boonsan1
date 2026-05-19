package db;

import enums.ProcessingResult;
import model.contract.PaymentCollection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// PaymentCollection 엔티티 DB 매핑 — payment_collection 테이블 CRUD 담당
public class PaymentCollectionDBO extends DBA {

    public PaymentCollection findById(String paymentCollectionId) {
        String sql = "SELECT payment_collection_id, policy_number, due_date, collected_amount, "
                + "unpaid_amount, unpaid_installment_count, processing_result, collected_at "
                + "FROM payment_collection WHERE payment_collection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentCollectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCollection(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 분납수금 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<PaymentCollection> findAll() {
        String sql = "SELECT payment_collection_id, policy_number, due_date, collected_amount, "
                + "unpaid_amount, unpaid_installment_count, processing_result, collected_at "
                + "FROM payment_collection ORDER BY due_date DESC NULLS LAST, payment_collection_id";
        List<PaymentCollection> collectionList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                collectionList.add(mapCollection(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 분납수금 목록 조회 실패: " + e.getMessage());
        }
        return collectionList;
    }

    public boolean save(PaymentCollection collection) {
        if (collection == null) {
            return false;
        }
        String sql = "INSERT INTO payment_collection "
                + "(payment_collection_id, policy_number, due_date, collected_amount, "
                + "unpaid_amount, unpaid_installment_count, processing_result, collected_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setCollectionParams(statement, collection);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 분납수금 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(PaymentCollection collection) {
        if (collection == null) {
            return false;
        }
        String sql = "UPDATE payment_collection SET "
                + "policy_number = ?, due_date = ?, collected_amount = ?, unpaid_amount = ?, "
                + "unpaid_installment_count = ?, processing_result = ?, collected_at = ? "
                + "WHERE payment_collection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, collection.getPolicyNumber());
            statement.setDate(2, toDate(collection.getDueDate()));
            statement.setBigDecimal(3, collection.getCollectedAmount());
            statement.setBigDecimal(4, collection.getUnpaidAmount());
            statement.setInt(5, collection.getUnpaidInstallmentCount());
            statement.setString(6, resolveResultName(collection.getProcessingResult()));
            statement.setTimestamp(7, toTimestamp(collection.getCollectedAt()));
            statement.setString(8, collection.getPaymentCollectionId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 분납수금 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String paymentCollectionId) {
        String sql = "DELETE FROM payment_collection WHERE payment_collection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentCollectionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 분납수금 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private PaymentCollection mapCollection(ResultSet resultSet) throws SQLException {
        PaymentCollection collection = new PaymentCollection(
                toLocalDate(resultSet.getDate("due_date")),
                resultSet.getBigDecimal("collected_amount"),
                resultSet.getBigDecimal("unpaid_amount"),
                resultSet.getInt("unpaid_installment_count"),
                resolveResult(resultSet.getString("processing_result")),
                toLocalDateTime(resultSet.getTimestamp("collected_at"))
        );
        collection.setPaymentCollectionId(resultSet.getString("payment_collection_id"));
        collection.setPolicyNumber(resultSet.getString("policy_number"));
        return collection;
    }

    private void setCollectionParams(PreparedStatement statement, PaymentCollection collection) throws SQLException {
        statement.setString(1, collection.getPaymentCollectionId());
        statement.setString(2, collection.getPolicyNumber());
        statement.setDate(3, toDate(collection.getDueDate()));
        statement.setBigDecimal(4, collection.getCollectedAmount());
        statement.setBigDecimal(5, collection.getUnpaidAmount());
        statement.setInt(6, collection.getUnpaidInstallmentCount());
        statement.setString(7, resolveResultName(collection.getProcessingResult()));
        statement.setTimestamp(8, toTimestamp(collection.getCollectedAt()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private Date toDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private String resolveResultName(ProcessingResult result) {
        return result == null ? null : result.name();
    }

    private ProcessingResult resolveResult(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ProcessingResult.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
