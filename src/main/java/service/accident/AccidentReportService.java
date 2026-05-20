package service.accident;

import db.AccidentReportDBO;
import model.accident.AccidentReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

// 사고 접수 서비스는 사고번호 생성, 서류 제출 판단, 사고 접수 DB 저장 흐름을 담당한다.
public class AccidentReportService {

    private static final Random rnd = new Random();
    private static final AccidentReportDBO accidentReportDBO = new AccidentReportDBO();

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

        boolean saved = accidentReportDBO.save(
                accidentReport,
                policyNumber,
                resolveAccidentStatus(accidentStatus),
                resolveDocumentSubmissionStatus(documentSubmissionStatus),
                accidentAtText
        );
        return saved ? accidentReport : null;
    }

    public static AccidentReport findAccidentReportByReportNo(String reportNo) {
        return accidentReportDBO.findByReportNo(reportNo);
    }

    public static List<AccidentReport> getAccidentReportList() {
        return accidentReportDBO.findAll();
    }

    public static String getPolicyNumber(String reportNo) {
        String policyNumber = accidentReportDBO.findPolicyNumberByReportNo(reportNo);
        return policyNumber == null ? "" : policyNumber;
    }

    public static String getAccidentStatus(String reportNo) {
        return accidentReportDBO.findStatusByReportNo(reportNo);
    }

    public static String getDocumentSubmissionStatus(String reportNo) {
        String status = accidentReportDBO.findDocumentSubmissionStatusByReportNo(reportNo);
        return status == null ? "" : status;
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
