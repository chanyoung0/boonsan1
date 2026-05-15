package service.contract;

// 분납/수금 관리 서비스 — 이관 유형 판정 순수 비즈니스 로직 담당
public class PaymentCollectionService {

    // 이관 유형 판정
    public static String determineTransferType(String choice) {
        return "2".equals(choice) ? "해지처리" : "방문수금";
    }
}
