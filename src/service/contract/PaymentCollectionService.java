package service.contract;

import db.PaymentCollectionDBO;
import enums.ProcessingResult;
import model.contract.PaymentCollection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 분납/수금 관리 서비스 — 이관 유형 판정, 수금 결과 객체 저장 유스케이스 흐름 담당
public class PaymentCollectionService {

    // 이관 유형 판정
    public static String determineTransferType(String choice) {
        return "2".equals(choice) ? "해지처리" : "방문수금";
    }

    // 수금 결과 PaymentCollection 객체 생성 및 저장
    public static void saveCollectionResult(boolean allSuccess) {
        PaymentCollectionDBO dbo = new PaymentCollectionDBO();
        LocalDate dueDate = LocalDate.of(2024, 1, 15);

        if (!allSuccess) {
            PaymentCollection partial = new PaymentCollection(
                dueDate, new BigDecimal("248000"), BigDecimal.ZERO,
                0, ProcessingResult.PARTIAL, LocalDateTime.now()
            );
            dbo.save(partial);

            PaymentCollection failed = new PaymentCollection(
                dueDate, BigDecimal.ZERO, new BigDecimal("220000"),
                1, ProcessingResult.FAILED, LocalDateTime.now()
            );
            dbo.save(failed);
        } else {
            PaymentCollection success = new PaymentCollection(
                dueDate, new BigDecimal("468000"), BigDecimal.ZERO,
                0, ProcessingResult.SUCCESS, LocalDateTime.now()
            );
            dbo.save(success);
        }
    }
}
