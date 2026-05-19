package service.accident;

import db.AccidentReportDBO;
import model.accident.AccidentReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

// 사고 접수 서비스 — 사고번호 생성, 서류 처리 판단, AccidentReport 영속화 담당
public class AccidentReportService {

    private static final AccidentReportDBO accidentReportDBO = new AccidentReportDBO();
    private static final Random rnd = new Random();
    private static final DateTimeFormatter ACCIDENT_AT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // 사고 접수 번호 생성
    public static String generateReportNo() {
        return "ACC-2024-" + String.format("%06d", rnd.nextInt(999999) + 1);
    }

    // 서류 나중에 제출 여부 판단
    public static boolean isDocumentDeferred(String docChoice) {
        return "2".equals(docChoice);
    }

    // 사고 일시 문자열 파싱 — 파싱 실패 시 null 반환 (DB 컬럼은 nullable)
    public static LocalDateTime parseAccidentAt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return LocalDateTime.parse(trimmed, ACCIDENT_AT_FORMAT);
        } catch (DateTimeParseException e) {
            // 날짜만 입력된 경우 자정으로 보정 시도
            try {
                return LocalDate.parse(trimmed).atTime(LocalTime.MIDNIGHT);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }
    }

    // 사고 접수 영속화 — DB 저장 결과 반환
    public static boolean saveReport(AccidentReport report) {
        return accidentReportDBO.save(report);
    }
}
