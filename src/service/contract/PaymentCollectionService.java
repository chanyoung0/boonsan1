package service.contract;

import enums.ProcessingResult;
import model.contract.PaymentCollection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 분납/수금 관리 서비스 — 이관 유형 판정 순수 비즈니스 로직 담당
public class PaymentCollectionService {

    public static PaymentCollection createCollectionResult(String resultChoice) {
        boolean allSuccess = isAllSuccessChoice(resultChoice);
        PaymentCollection paymentCollection = new PaymentCollection(
                LocalDate.of(2024, 1, 15),
                allSuccess ? BigDecimal.valueOf(468_000L) : BigDecimal.valueOf(248_000L),
                allSuccess ? BigDecimal.ZERO : BigDecimal.valueOf(220_000L),
                allSuccess ? 0 : 1,
                allSuccess ? ProcessingResult.SUCCESS : ProcessingResult.PARTIAL,
                LocalDateTime.now()
        );
        paymentCollection.processCollection();
        paymentCollection.checkDueDate();
        paymentCollection.checkUnpaidStatus();
        return paymentCollection;
    }

    public static boolean isCollectionFullySuccessful(PaymentCollection paymentCollection) {
        return paymentCollection != null && paymentCollection.getProcessingResult() == ProcessingResult.SUCCESS;
    }

    public static String createCollectionResultSummary(PaymentCollection paymentCollection) {
        if (paymentCollection == null) {
            return "[시스템] 수금 처리 결과 없음";
        }
        return "[시스템] 수금 처리 결과"
                + "\n  처리결과: " + paymentCollection.getProcessingResult()
                + "\n  수금금액: " + formatAmount(paymentCollection.getCollectedAmount())
                + "\n  수금일시: " + paymentCollection.getCollectedAt()
                + "\n  미납금액: " + formatAmount(paymentCollection.getUnpaidAmount())
                + "\n  미납회차: " + paymentCollection.getUnpaidInstallmentCount();
    }

    public static String createUnpaidNoticeSummary(PaymentCollection paymentCollection) {
        if (paymentCollection == null) {
            return "[시스템] 미납 안내 정보 없음";
        }
        return "[시스템] 미납 안내장 발송 완료"
                + "\n  증권번호: P2024-009012"
                + "\n  피보험자 이름: 이철수"
                + "\n  미납금액: " + formatAmount(paymentCollection.getUnpaidAmount())
                + "\n  납입기한: 2024-01-31"
                + "\n  납입방법: 자동이체 재시도 또는 방문수금";
    }

    // 이관 유형 판정
    public static String determineTransferType(String choice) {
        return "2".equals(choice) ? "해지처리" : "방문수금";
    }

    public static String createTransferSummary(String choice) {
        String transferType = determineTransferType(choice);
        return "[시스템] 이관 처리 완료"
                + "\n  이관대상: P2024-009012 | 이철수"
                + "\n  이관유형: " + transferType
                + "\n  이관일시: " + LocalDateTime.now()
                + "\n  안내: 실제 계약상태 저장은 현재 DB 미연동 단계에서 보류됩니다.";
    }

    private static boolean isAllSuccessChoice(String resultChoice) {
        return "1".equals(resultChoice);
    }

    private static String formatAmount(BigDecimal amount) {
        return amount == null ? "0원" : String.format("%,d원", amount.longValue());
    }
}
