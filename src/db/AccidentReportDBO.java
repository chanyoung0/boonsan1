package db;

import model.accident.AccidentReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// AccidentReport entity database operator for accident_report CRUD.
public class AccidentReportDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "report_no, policy_number, accident_description, damage_details, "
                    + "accident_status, document_submission_status, accident_at_text, created_at";

    public AccidentReport findByReportNo(String reportNo) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM accident_report WHERE report_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAccidentReport(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<AccidentReport> findAll() {
        List<AccidentReport> reportList = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM accident_report ORDER BY report_no";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reportList.add(mapAccidentReport(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 목록 조회 실패: " + e.getMessage());
        }
        return reportList;
    }

    public String findPolicyNumberByReportNo(String reportNo) {
        return findStringColumnByReportNo(reportNo, "policy_number");
    }

    public String findStatusByReportNo(String reportNo) {
        return resolveAccidentStatus(findStringColumnByReportNo(reportNo, "accident_status"));
    }

    public String findDocumentSubmissionStatusByReportNo(String reportNo) {
        return resolveDocumentSubmissionStatus(
                findStringColumnByReportNo(reportNo, "document_submission_status"));
    }

    public boolean save(AccidentReport report) {
        throw new UnsupportedOperationException("policyNumber, accidentStatus, documentSubmissionStatus, accidentAtText 파라미터가 필요합니다.");
    }

    public boolean save(AccidentReport report, String policyNumber, String accidentStatus,
                        String documentSubmissionStatus, String accidentAtText) {
        if (report == null || report.getReportNo() == null || policyNumber == null) {
            return false;
        }
        String sql = "INSERT INTO accident_report "
                + "(report_no, policy_number, accident_description, damage_details, "
                + "accident_status, document_submission_status, accident_at_text, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setAccidentReportParams(statement, report, policyNumber, accidentStatus,
                    documentSubmissionStatus, accidentAtText);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(AccidentReport report) {
        throw new UnsupportedOperationException("policyNumber, accidentStatus, documentSubmissionStatus, accidentAtText 파라미터가 필요합니다.");
    }

    public boolean update(AccidentReport report, String policyNumber, String accidentStatus,
                          String documentSubmissionStatus, String accidentAtText) {
        if (report == null || report.getReportNo() == null || policyNumber == null) {
            return false;
        }
        String sql = "UPDATE accident_report SET "
                + "policy_number = ?, accident_description = ?, damage_details = ?, "
                + "accident_status = ?, document_submission_status = ?, accident_at_text = ?, created_at = ? "
                + "WHERE report_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            statement.setString(2, report.getAccidentDescription());
            statement.setString(3, report.getDamageDetails());
            statement.setString(4, resolveAccidentStatus(accidentStatus));
            statement.setString(5, resolveDocumentSubmissionStatus(documentSubmissionStatus));
            statement.setString(6, accidentAtText);
            statement.setTimestamp(7, toTimestamp(report.getCreatedAt()));
            statement.setString(8, report.getReportNo());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String reportNo) {
        String sql = "DELETE FROM accident_report WHERE report_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportNo);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private AccidentReport mapAccidentReport(ResultSet resultSet) throws SQLException {
        AccidentReport report = new AccidentReport();
        report.setReportNo(resultSet.getString("report_no"));
        report.setAccidentDescription(resultSet.getString("accident_description"));
        report.setDamageDetails(resultSet.getString("damage_details"));
        report.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        return report;
    }

    private void setAccidentReportParams(PreparedStatement statement, AccidentReport report,
                                         String policyNumber, String accidentStatus,
                                         String documentSubmissionStatus, String accidentAtText)
            throws SQLException {
        statement.setString(1, report.getReportNo());
        statement.setString(2, policyNumber);
        statement.setString(3, report.getAccidentDescription());
        statement.setString(4, report.getDamageDetails());
        statement.setString(5, resolveAccidentStatus(accidentStatus));
        statement.setString(6, resolveDocumentSubmissionStatus(documentSubmissionStatus));
        statement.setString(7, accidentAtText);
        statement.setTimestamp(8, toTimestamp(report.getCreatedAt()));
    }

    private String findStringColumnByReportNo(String reportNo, String columnName) {
        String sql = "SELECT " + columnName + " FROM accident_report WHERE report_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reportNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사고 접수 부가정보 조회 실패: " + e.getMessage());
        }
        return null;
    }

    private String resolveAccidentStatus(String value) {
        if ("DOCUMENT_PENDING".equals(value)
                || "REJECTED".equals(value)
                || "INVESTIGATION_REQUIRED".equals(value)) {
            return value;
        }
        return "RECEIVED";
    }

    private String resolveDocumentSubmissionStatus(String value) {
        if ("PENDING".equals(value)) {
            return value;
        }
        if ("SUBMITTED".equals(value)) {
            return value;
        }
        return null;
    }

    private Timestamp toTimestamp(java.time.LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
