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

// PaymentCollection entity database operator for payment_collection CRUD.
public class PaymentCollectionDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "collection_id, policy_number, due_date, collected_amount, unpaid_amount, "
                    + "unpaid_installment_count, processing_result, collected_at, "
                    + "transfer_type, collection_status";

    public PaymentCollection findById(String collectionId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM payment_collection WHERE collection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, collectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPaymentCollection(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB Error] Payment collection lookup failed: " + e.getMessage());
        }
        return null;
    }

    public List<PaymentCollection> findAll() {
        List<PaymentCollection> collectionList = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM payment_collection ORDER BY collection_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                collectionList.add(mapPaymentCollection(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB Error] Payment collection list lookup failed: " + e.getMessage());
        }
        return collectionList;
    }

    public List<String> findAllIds() {
        List<String> collectionIdList = new ArrayList<>();
        String sql = "SELECT collection_id FROM payment_collection ORDER BY collection_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                collectionIdList.add(resultSet.getString("collection_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB Error] Payment collection id list lookup failed: " + e.getMessage());
        }
        return collectionIdList;
    }

    public String findPolicyNumberById(String collectionId) {
        return findStringColumnById(collectionId, "policy_number");
    }

    public String findStatusById(String collectionId) {
        String status = findStringColumnById(collectionId, "collection_status");
        return resolveCollectionStatus(status);
    }

    public String findTransferTypeById(String collectionId) {
        return findStringColumnById(collectionId, "transfer_type");
    }

    public boolean save(PaymentCollection collection) {
        return false;
    }

    public boolean save(PaymentCollection collection, String collectionId,
                        String policyNumber, String collectionStatus, String transferType) {
        if (collection == null || collectionId == null || policyNumber == null) {
            return false;
        }
        String sql = "INSERT INTO payment_collection "
                + "(collection_id, policy_number, due_date, collected_amount, unpaid_amount, "
                + "unpaid_installment_count, processing_result, collected_at, transfer_type, collection_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, collection, collectionId, policyNumber, collectionStatus, transferType);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB Error] Payment collection save failed: " + e.getMessage());
        }
        return false;
    }

    public boolean update(PaymentCollection collection) {
        return false;
    }

    public boolean update(PaymentCollection collection, String collectionId,
                          String policyNumber, String collectionStatus, String transferType) {
        if (collection == null || collectionId == null || policyNumber == null) {
            return false;
        }
        String sql = "UPDATE payment_collection SET "
                + "policy_number = ?, due_date = ?, collected_amount = ?, unpaid_amount = ?, "
                + "unpaid_installment_count = ?, processing_result = ?, collected_at = ?, "
                + "transfer_type = ?, collection_status = ? "
                + "WHERE collection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            statement.setDate(2, toSqlDate(collection.getDueDate()));
            statement.setBigDecimal(3, collection.getCollectedAmount());
            statement.setBigDecimal(4, collection.getUnpaidAmount());
            statement.setInt(5, collection.getUnpaidInstallmentCount());
            statement.setString(6, resolveProcessingResultName(collection));
            statement.setTimestamp(7, toTimestamp(collection.getCollectedAt()));
            statement.setString(8, resolveTransferType(transferType));
            statement.setString(9, resolveCollectionStatus(collectionStatus));
            statement.setString(10, collectionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB Error] Payment collection update failed: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String collectionId) {
        String sql = "DELETE FROM payment_collection WHERE collection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, collectionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB Error] Payment collection delete failed: " + e.getMessage());
        }
        return false;
    }

    private String findStringColumnById(String collectionId, String columnName) {
        String sql = "SELECT " + columnName + " FROM payment_collection WHERE collection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, collectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB Error] Payment collection column lookup failed: " + e.getMessage());
        }
        return null;
    }

    private PaymentCollection mapPaymentCollection(ResultSet resultSet) throws SQLException {
        return new PaymentCollection(
                toLocalDate(resultSet.getDate("due_date")),
                resultSet.getBigDecimal("collected_amount"),
                resultSet.getBigDecimal("unpaid_amount"),
                resultSet.getInt("unpaid_installment_count"),
                resolveProcessingResult(resultSet.getString("processing_result")),
                toLocalDateTime(resultSet.getTimestamp("collected_at"))
        );
    }

    private void setSaveParams(PreparedStatement statement, PaymentCollection collection,
                               String collectionId, String policyNumber,
                               String collectionStatus, String transferType) throws SQLException {
        statement.setString(1, collectionId);
        statement.setString(2, policyNumber);
        statement.setDate(3, toSqlDate(collection.getDueDate()));
        statement.setBigDecimal(4, collection.getCollectedAmount());
        statement.setBigDecimal(5, collection.getUnpaidAmount());
        statement.setInt(6, collection.getUnpaidInstallmentCount());
        statement.setString(7, resolveProcessingResultName(collection));
        statement.setTimestamp(8, toTimestamp(collection.getCollectedAt()));
        statement.setString(9, resolveTransferType(transferType));
        statement.setString(10, resolveCollectionStatus(collectionStatus));
    }

    private String resolveProcessingResultName(PaymentCollection collection) {
        if (collection == null || collection.getProcessingResult() == null) {
            return ProcessingResult.PENDING.name();
        }
        return collection.getProcessingResult().name();
    }

    private ProcessingResult resolveProcessingResult(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ProcessingResult.PENDING;
        }
        try {
            return ProcessingResult.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return ProcessingResult.PENDING;
        }
    }

    private String resolveCollectionStatus(String value) {
        if ("COLLECTED".equals(value) || "UNPAID".equals(value)
                || "TRANSFERRED".equals(value) || "CREATED".equals(value)) {
            return value;
        }
        return "CREATED";
    }

    private String resolveTransferType(String value) {
        if ("VISIT_COLLECTION".equals(value) || "TRANSFER".equals(value)) {
            return value;
        }
        return null;
    }

    private Date toSqlDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    private LocalDate toLocalDate(Date value) {
        return value == null ? null : value.toLocalDate();
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
