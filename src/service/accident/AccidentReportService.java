package service.accident;

import java.util.Random;

// 사고 접수 서비스 — 사고번호 생성 및 서류 처리 판단 순수 비즈니스 로직 담당
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
}
