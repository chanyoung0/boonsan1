package service.accident;

import db.mapper.AccidentReportMapper;
import db.mybatis.MyBatisSessionFactory;
import model.accident.AccidentReport;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 사고 접수 서비스는 사고번호 생성, 서류 제출 판단, 사고 접수 DB 저장 흐름을 담당한다.
public class AccidentReportService {

    private static final Random rnd = new Random();

    // 사고 접수 번호 생성
    public static String generateReportNo() {
        return "ACC-2024-" + String.format("%06d", rnd.nextInt(999999) + 1);
    }

    // 서류 나중 제출 여부 판단
    public static boolean isDocumentDeferred(String docChoice) {
        return "2".equals(docChoice);
    }

    public static AccidentReport registerAccidentReport(AccidentReport accidentReport,
                                                        String policyNumber,
                                                        String accidentStatus,
                                                        String documentSubmissionStatus,
                                                        String accidentAtText) {
        if (accidentReport == null) {
            return null;
        }
        if (accidentReport.getReportNo() == null || accidentReport.getReportNo().trim().isEmpty()) {
            accidentReport.setReportNo(generateReportNo());
        }
        if (accidentReport.getCreatedAt() == null) {
            accidentReport.setCreatedAt(LocalDateTime.now());
        }
        if (policyNumber == null) {
            return null;
        }
        String accidentStatusName = resolveAccidentStatus(accidentStatus);
        String documentStatusName = resolveDocumentSubmissionStatus(documentSubmissionStatus);

        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            int rows = s.getMapper(AccidentReportMapper.class)
                    .insert(accidentReport, policyNumber, accidentStatusName, documentStatusName, accidentAtText);
            return rows > 0 ? accidentReport : null;
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 저장 실패: " + e.getMessage());
            return null;
        }
    }

    public static AccidentReport findAccidentReportByReportNo(String reportNo) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(AccidentReportMapper.class).findByReportNo(reportNo);
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public static List<AccidentReport> getAccidentReportList() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(AccidentReportMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static String getPolicyNumber(String reportNo) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            String policyNumber = s.getMapper(AccidentReportMapper.class).findPolicyNumberByReportNo(reportNo);
            return policyNumber == null ? "" : policyNumber;
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 부가정보 조회 실패: " + e.getMessage());
            return "";
        }
    }

    public static String getAccidentStatus(String reportNo) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            String value = s.getMapper(AccidentReportMapper.class).findStatusByReportNo(reportNo);
            return resolveAccidentStatus(value);
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public static String getDocumentSubmissionStatus(String reportNo) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            String value = s.getMapper(AccidentReportMapper.class)
                    .findDocumentSubmissionStatusByReportNo(reportNo);
            String status = resolveDocumentSubmissionStatus(value);
            return status == null ? "" : status;
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 부가정보 조회 실패: " + e.getMessage());
            return "";
        }
    }

    public static String resolveAccidentStatus(String accidentStatus) {
        if ("DOCUMENT_PENDING".equals(accidentStatus)
                || "REJECTED".equals(accidentStatus)
                || "INVESTIGATION_REQUIRED".equals(accidentStatus)) {
            return accidentStatus;
        }
        return "RECEIVED";
    }

    public static String resolveDocumentSubmissionStatus(String documentSubmissionStatus) {
        if ("PENDING".equals(documentSubmissionStatus)) {
            return documentSubmissionStatus;
        }
        if ("SUBMITTED".equals(documentSubmissionStatus)) {
            return documentSubmissionStatus;
        }
        return null;
    }
}
