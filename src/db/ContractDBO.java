package db;

import enums.ContractStatus;
import enums.PaymentCycle;
import model.contract.Contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// Contract 엔티티 DB 매핑 — contract 테이블 CRUD 담당
// 관계 객체(insurance/insuredPerson/account/list 들)는 1차 DB 전환에서 제외.
public class ContractDBO extends DBA {

    public Contract findByPolicyNumber(String policyNumber) {
        String sql = "SELECT policy_number, contract_status, payment_cycle, has_unpaid_premium, installment_count "
                + "FROM contract WHERE policy_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapContract(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 계약 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Contract> findAll() {
        String sql = "SELECT policy_number, contract_status, payment_cycle, has_unpaid_premium, installment_count "
                + "FROM contract ORDER BY policy_number";
        List<Contract> contractList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                contractList.add(mapContract(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 계약 목록 조회 실패: " + e.getMessage());
        }
        return contractList;
    }

    public boolean save(Contract contract) {
        if (contract == null) {
            return false;
        }
        String sql = "INSERT INTO contract "
                + "(policy_number, contract_status, payment_cycle, has_unpaid_premium, installment_count) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, contract.getPolicyNumber());
            statement.setString(2, resolveContractStatusName(contract.getContractStatus()));
            statement.setString(3, resolvePaymentCycleName(contract.getPaymentCycle()));
            setNullableBoolean(statement, 4, contract.getHasUnpaidPremium());
            statement.setInt(5, contract.getInstallmentCount());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 계약 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Contract contract) {
        if (contract == null) {
            return false;
        }
        String sql = "UPDATE contract SET "
                + "contract_status = ?, payment_cycle = ?, has_unpaid_premium = ?, installment_count = ? "
                + "WHERE policy_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveContractStatusName(contract.getContractStatus()));
            statement.setString(2, resolvePaymentCycleName(contract.getPaymentCycle()));
            setNullableBoolean(statement, 3, contract.getHasUnpaidPremium());
            statement.setInt(4, contract.getInstallmentCount());
            statement.setString(5, contract.getPolicyNumber());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 계약 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String policyNumber) {
        String sql = "DELETE FROM contract WHERE policy_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 계약 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean existsByPolicyNumber(String policyNumber) {
        String sql = "SELECT 1 FROM contract WHERE policy_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 계약 존재 확인 실패: " + e.getMessage());
            return false;
        }
    }

    private Contract mapContract(ResultSet resultSet) throws SQLException {
        Contract contract = new Contract();
        contract.setPolicyNumber(resultSet.getString("policy_number"));
        contract.setContractStatus(resolveContractStatus(resultSet.getString("contract_status")));
        contract.setPaymentCycle(resolvePaymentCycle(resultSet.getString("payment_cycle")));

        boolean unpaid = resultSet.getBoolean("has_unpaid_premium");
        contract.setHasUnpaidPremium(resultSet.wasNull() ? null : unpaid);
        contract.setInstallmentCount(resultSet.getInt("installment_count"));
        return contract;
    }

    private void setNullableBoolean(PreparedStatement statement, int index, Boolean value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BOOLEAN);
        } else {
            statement.setBoolean(index, value);
        }
    }

    private String resolveContractStatusName(ContractStatus status) {
        return status == null ? null : status.name();
    }

    private ContractStatus resolveContractStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ContractStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolvePaymentCycleName(PaymentCycle cycle) {
        return cycle == null ? null : cycle.name();
    }

    private PaymentCycle resolvePaymentCycle(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return PaymentCycle.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
