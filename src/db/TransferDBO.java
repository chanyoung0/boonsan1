package db;

import enums.TransferType;
import model.contract.Transfer;
import model.person.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Transfer 엔티티 DB 매핑 — transfer 테이블 CRUD 담당
// Manager 관계는 assignee_id 컬럼만 저장하고 객체 복원은 보류.
public class TransferDBO extends DBA {

    public Transfer findById(String transferId) {
        String sql = "SELECT transfer_id, payment_collection_id, transfer_type, assignee_id, transferred_at "
                + "FROM transfer WHERE transfer_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transferId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTransfer(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이관 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Transfer> findAll() {
        String sql = "SELECT transfer_id, payment_collection_id, transfer_type, assignee_id, transferred_at "
                + "FROM transfer ORDER BY transferred_at DESC NULLS LAST, transfer_id";
        List<Transfer> transferList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                transferList.add(mapTransfer(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이관 목록 조회 실패: " + e.getMessage());
        }
        return transferList;
    }

    public boolean save(Transfer transfer) {
        if (transfer == null) {
            return false;
        }
        String sql = "INSERT INTO transfer "
                + "(transfer_id, payment_collection_id, transfer_type, assignee_id, transferred_at) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transfer.getTransferId());
            statement.setString(2, transfer.getPaymentCollectionId());
            statement.setString(3, resolveTypeName(transfer.getTransferType()));
            statement.setString(4, resolveAssigneeId(transfer.getAssignee()));
            statement.setTimestamp(5, toTimestamp(transfer.getTransferredAt()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이관 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Transfer transfer) {
        if (transfer == null) {
            return false;
        }
        String sql = "UPDATE transfer SET "
                + "payment_collection_id = ?, transfer_type = ?, assignee_id = ?, transferred_at = ? "
                + "WHERE transfer_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transfer.getPaymentCollectionId());
            statement.setString(2, resolveTypeName(transfer.getTransferType()));
            statement.setString(3, resolveAssigneeId(transfer.getAssignee()));
            statement.setTimestamp(4, toTimestamp(transfer.getTransferredAt()));
            statement.setString(5, transfer.getTransferId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이관 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String transferId) {
        String sql = "DELETE FROM transfer WHERE transfer_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transferId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이관 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Transfer mapTransfer(ResultSet resultSet) throws SQLException {
        Transfer transfer = new Transfer(
                resolveType(resultSet.getString("transfer_type")),
                null,
                toLocalDateTime(resultSet.getTimestamp("transferred_at"))
        );
        transfer.setTransferId(resultSet.getString("transfer_id"));
        transfer.setPaymentCollectionId(resultSet.getString("payment_collection_id"));
        // TODO: assignee_id 로 ManagerDBO 조회하여 assignee 객체 복원
        return transfer;
    }

    private String resolveAssigneeId(Manager assignee) {
        if (assignee == null) {
            return null;
        }
        // TODO: Manager 모델에 employeeNumber/id 가 있으면 사용. 현재는 toString() 폴백.
        return assignee.toString();
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveTypeName(TransferType type) {
        return type == null ? null : type.name();
    }

    private TransferType resolveType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return TransferType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
