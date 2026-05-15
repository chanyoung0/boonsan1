package service.accident;

import db.AccidentReportDBO;
import enums.AccidentDetailsType;
import model.accident.AccidentReport;

import java.time.LocalDateTime;
import java.util.Random;

// 사고 접수 서비스 — 사고번호 생성, 접수 객체 저장 유스케이스 흐름 담당
public class AccidentReportService {

    private static final Random rnd = new Random();

    // 사고 접수 번호 생성
    public static String generateReportNo() {
        return "ACC-2024-" + String.format("%06d", rnd.nextInt(999999) + 1);
    }

    // 서류 나중에 제출 여부 판단
    public static boolean isDocumentDeferred(String docChoice) {
        return "2".equals(docChoice);
    }

    // AccidentReport 객체 생성 및 저장 — 생성된 접수번호 반환
    public static String createAndSave(String accidentDescription, String damageDetails) {
        String reportNo = generateReportNo();
        AccidentReport report = new AccidentReport(
            reportNo,
            accidentDescription,
            damageDetails,
            AccidentDetailsType.VEHICLE,
            LocalDateTime.now()
        );
        new AccidentReportDBO().save(report);
        return reportNo;
    }
}
