package db;

import model.person.InsuredPerson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// InsuredPerson entity database operator for insured_person CRUD.
public class InsuredPersonDBO extends DBA {

    private static final String SELECT_COLUMNS = "rrn, name, contact";

    public InsuredPerson findByRrn(String rrn) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insured_person WHERE rrn = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rrn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapInsuredPerson(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 피보험자 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<InsuredPerson> findAll() {
        List<InsuredPerson> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insured_person ORDER BY rrn";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapInsuredPerson(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 피보험자 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public void save(InsuredPerson person) {
        if (person == null || person.getResidentRegistrationNumber() == null) return;
        String sql = "INSERT INTO insured_person (rrn, name, contact) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.getResidentRegistrationNumber());
            statement.setString(2, person.getName());
            statement.setString(3, person.getContact());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB 오류] 피보험자 저장 실패: " + e.getMessage());
        }
    }

    public void update(InsuredPerson person) {
        if (person == null || person.getResidentRegistrationNumber() == null) return;
        String sql = "UPDATE insured_person SET name = ?, contact = ? WHERE rrn = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.getName());
            statement.setString(2, person.getContact());
            statement.setString(3, person.getResidentRegistrationNumber());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB 오류] 피보험자 수정 실패: " + e.getMessage());
        }
    }

    public void delete(String rrn) {
        String sql = "DELETE FROM insured_person WHERE rrn = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rrn);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB 오류] 피보험자 삭제 실패: " + e.getMessage());
        }
    }

    private InsuredPerson mapInsuredPerson(ResultSet resultSet) throws SQLException {
        InsuredPerson person = new InsuredPerson();
        person.setResidentRegistrationNumber(resultSet.getString("rrn"));
        person.setName(resultSet.getString("name"));
        person.setContact(resultSet.getString("contact"));
        return person;
    }
}
