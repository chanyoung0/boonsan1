package service.contract;

import db.PaymentCollectionDBO;
import enums.ProcessingResult;
import model.contract.PaymentCollection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 분납/수금 관리 서비스
public class PaymentCollectionService {

    private static final PaymentCollectionDBO paymentCollectionDBO = new PaymentCollectionDBO();
    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.of(2024, 1, 15);
    private static final String[] TARGET_POLICY_NUMBERS = {
            "P2024-001234", "P2024-005678", "P2024-009012"
    };
    private static final BigDecimal[] TARGET_AMOUNTS = {
            BigDecimal.valueOf(150_000L),
            BigDecimal.valueOf(98_000L),
            BigDecimal.valueOf(220_000L)
    };

    public static PaymentCollection createCollectionResult(String resultChoice) {
        boolean allSuccess = isAllSuccessChoice(resultChoice);
        PaymentCollection paymentCollection = new PaymentCollection(
                DEFAULT_DUE_DATE,
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

    public static List<String> saveCollectionResults(String resultChoice) {
        boolean allSuccess = isAllSuccessChoice(resultChoice);
        List<String> savedCollectionIds = new ArrayList<>();
        LocalDateTime collectedAt = LocalDateTime.now();

        for (int i = 0; i < TARGET_POLICY_NUMBERS.length; i++) {
            boolean success = allSuccess || i < 2;
            PaymentCollection paymentCollection = new PaymentCollection(
                    DEFAULT_DUE_DATE,
                    success ? TARGET_AMOUNTS[i] : BigDecimal.ZERO,
                    success ? BigDecimal.ZERO : TARGET_AMOUNTS[i],
                    success ? 0 : 1,
                    success ? ProcessingResult.SUCCESS : ProcessingResult.FAILED,
                    collectedAt
            );
            paymentCollection.processCollection();
            paymentCollection.checkDueDate();
            paymentCollection.checkUnpaidStatus();

            String collectionId = generateCollectionId(i + 1);
            boolean saved = paymentCollectionDBO.save(
                    paymentCollection,
                    collectionId,
                    TARGET_POLICY_NUMBERS[i],
                    success ? "COLLECTED" : "UNPAID",
                    null
            );
            if (!saved) {
                return new ArrayList<>();
            }
            savedCollectionIds.add(collectionId);
        }

        return savedCollectionIds;
    }

    public static boolean transferUnpaidCollection(String collectionId, String transferChoice) {
        PaymentCollection paymentCollection = paymentCollectionDBO.findById(collectionId);
        if (paymentCollection == null) {
            return false;
        }

        String policyNumber = getPolicyNumber(collectionId);
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            return false;
        }

        return paymentCollectionDBO.update(
                paymentCollection,
                collectionId,
                policyNumber,
                "TRANSFERRED",
                resolveTransferType(transferChoice)
        );
    }

    public static boolean isCollectionFullySuccessful(PaymentCollection paymentCollection) {
        return paymentCollection != null && paymentCollection.getProcessingResult() == ProcessingResult.SUCCESS;
    }

    public static List<PaymentCollection> getCollectionList() {
        return paymentCollectionDBO.findAll();
    }

    public static List<String> getCollectionIdList() {
        return paymentCollectionDBO.findAllIds();
    }

    public static PaymentCollection findCollectionById(String collectionId) {
        return paymentCollectionDBO.findById(collectionId);
    }

    public static String getPolicyNumber(String collectionId) {
        String policyNumber = paymentCollectionDBO.findPolicyNumberById(collectionId);
        return policyNumber == null ? "" : policyNumber;
    }

    public static String getCollectionStatus(String collectionId) {
        return paymentCollectionDBO.findStatusById(collectionId);
    }

    public static String getTransferType(String collectionId) {
        return paymentCollectionDBO.findTransferTypeById(collectionId);
    }

    public static String findFirstUnpaidCollectionId(List<String> collectionIds) {
        if (collectionIds == null) {
            return null;
        }
        for (String collectionId : collectionIds) {
            if ("UNPAID".equals(getCollectionStatus(collectionId))) {
                return collectionId;
            }
        }
        return null;
    }

    public static String createSavedCollectionSummary(List<String> collectionIds) {
        if (collectionIds == null || collectionIds.isEmpty()) {
            return "[시스템] 저장된 수금 처리 결과가 없습니다.";
        }

        StringBuilder builder = new StringBuilder("[시스템] 수금 처리 결과 저장 완료");
        for (String collectionId : collectionIds) {
            builder.append("\n  수금번호: ").append(collectionId)
                    .append(" | 증권번호: ").append(getPolicyNumber(collectionId))
                    .append(" | 상태: ").append(getCollectionStatus(collectionId));
        }
        return builder.toString();
    }

    public static String createCollectionListSummary() {
        List<PaymentCollection> collectionList = getCollectionList();
        List<String> collectionIdList = getCollectionIdList();
        if (collectionList.isEmpty()) {
            return "[시스템] 저장된 수금 처리 목록이 없습니다.";
        }

        StringBuilder builder = new StringBuilder("[시스템] 저장된 수금 처리 목록");
        for (int i = 0; i < collectionList.size(); i++) {
            PaymentCollection collection = collectionList.get(i);
            String collectionId = i < collectionIdList.size() ? collectionIdList.get(i) : "";
            builder.append("\n  ").append(i + 1)
                    .append(" | 수금번호: ").append(collectionId)
                    .append(" | 증권번호: ").append(getPolicyNumber(collectionId))
                    .append(" | 납입기일: ").append(collection.getDueDate())
                    .append(" | 처리결과: ").append(collection.getProcessingResult())
                    .append(" | 수금액: ").append(formatAmount(collection.getCollectedAmount()))
                    .append("원 | 미납액: ").append(formatAmount(collection.getUnpaidAmount()))
                    .append("원 | 상태: ").append(getCollectionStatus(collectionId));
            String transferType = getTransferType(collectionId);
            if (transferType != null && !transferType.trim().isEmpty()) {
                builder.append(" | 이관유형: ").append(transferType);
            }
        }
        return builder.toString();
    }

    public static String createCollectionResultSummary(PaymentCollection paymentCollection) {
        if (paymentCollection == null) {
            return "[시스템] 수금 처리 결과 없음";
        }
        return "[시스템] 수금 처리 결과"
                + "\n  처리결과: " + paymentCollection.getProcessingResult()
                + "\n  수금금액: " + formatAmount(paymentCollection.getCollectedAmount()) + "원"
                + "\n  수금일시: " + paymentCollection.getCollectedAt()
                + "\n  미납금액: " + formatAmount(paymentCollection.getUnpaidAmount()) + "원"
                + "\n  미납회차: " + paymentCollection.getUnpaidInstallmentCount();
    }

    public static String createUnpaidNoticeSummary(PaymentCollection paymentCollection) {
        if (paymentCollection == null) {
            return "[시스템] 미납 안내 정보 없음";
        }
        return "[시스템] 미납 안내장 발송 완료"
                + "\n  증권번호: P2024-009012"
                + "\n  피보험자 이름: 이철수"
                + "\n  미납금액: " + formatAmount(paymentCollection.getUnpaidAmount()) + "원"
                + "\n  납입기한: 2024-01-31"
                + "\n  납입방법: 자동이체 재시도 또는 방문수금";
    }

    public static String determineTransferType(String choice) {
        return "2".equals(choice) ? "이관" : "방문수금";
    }

    public static String createTransferSummary(String choice) {
        String transferType = determineTransferType(choice);
        return "[시스템] 이관 처리 완료"
                + "\n  이관대상: P2024-009012 | 이철수"
                + "\n  이관유형: " + transferType
                + "\n  이관일시: " + LocalDateTime.now()
                + "\n  안내: 실제 계약상태 저장은 Contract DB 전환 이후 반영합니다.";
    }

    private static boolean isAllSuccessChoice(String resultChoice) {
        return "1".equals(resultChoice);
    }

    private static String resolveTransferType(String choice) {
        return "2".equals(choice) ? "TRANSFER" : "VISIT_COLLECTION";
    }

    private static String generateCollectionId(int sequence) {
        return "COL-" + System.currentTimeMillis() + "-" + sequence;
    }

    private static String formatAmount(BigDecimal amount) {
        return amount == null ? "0" : String.format("%,d", amount.longValue());
    }
}
