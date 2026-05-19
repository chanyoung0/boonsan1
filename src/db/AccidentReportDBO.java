package db;

import enums.AccidentDetailsType;
import model.accident.AccidentReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// AccidentReport 엔티티 DB 매핑 — accident_report 테이블 CRUD 담당
public class AccidentReportDBO extends DBA {

    public AccidentReport findByReportNo(String reportNo) {
        String sql = "SELECT report_no, policy_number, accident_description, damage_details, "
                + "accident_status, accident_at, created_at "
                + "FROM accident_report WHERE report_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReport(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<AccidentReport> findAll() {
        String sql = "SELECT report_no, policy_number, accident_description, damage_details, "
                + "accident_status, accident_at, created_at "
                + "FROM accident_report ORDER BY created_at DESC NULLS LAST, report_no";
        List<AccidentReport> reportList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reportList.add(mapReport(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 목록 조회 실패: " + e.getMessage());
        }
        return reportList;
    }

    public boolean save(AccidentReport report) {
        if (report == null) {
            return false;
        }
        String sql = "INSERT INTO accident_report "
                + "(report_no, policy_number, accident_description, damage_details, "
                + "accident_status, accident_at, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, report.getReportNo());
            statement.setString(2, report.getPolicyNumber());
            statement.setString(3, report.getAccidentDescription());
            statement.setString(4, report.getDamageDetails());
            statement.setString(5, resolveStatusName(report.getAccidentStatus()));
            statement.setTimestamp(6, toTimestamp(report.getAccidentAt()));
            statement.setTimestamp(7, toTimestamp(report.getCreatedAt()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(AccidentReport report) {
        if (report == null) {
            return false;
        }
        String sql = "UPDATE accident_report SET "
                + "policy_number = ?, accident_description = ?, damage_details = ?, "
                + "accident_status = ?, accident_at = ?, created_at = ? "
                + "WHERE report_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, report.getPolicyNumber());
            statement.setString(2, report.getAccidentDescription());
            statement.setString(3, report.getDamageDetails());
            statement.setString(4, resolveStatusName(report.getAccidentStatus()));
            statement.setTimestamp(5, toTimestamp(report.getAccidentAt()));
            statement.setTimestamp(6, toTimestamp(report.getCreatedAt()));
            statement.setString(7, report.getReportNo());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String reportNo) {
        String sql = "DELETE FROM accident_report WHERE report_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportNo);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private AccidentReport mapReport(ResultSet resultSet) throws SQLException {
        AccidentReport report = new AccidentReport(
                resultSet.getString("report_no"),
                resultSet.getString("accident_description"),
                resultSet.getString("damage_details"),
                resolveStatus(resultSet.getString("accident_status")),
                toLocalDateTime(resultSet.getTimestamp("created_at"))
        );
        report.setPolicyNumber(resultSet.getString("policy_number"));
        report.setAccidentAt(toLocalDateTime(resultSet.getTimestamp("accident_at")));
        return report;
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveStatusName(AccidentDetailsType status) {
        return status == null ? null : status.name();
    }

    private AccidentDetailsType resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return AccidentDetailsType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
