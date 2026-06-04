package com.boonsan.underwriting.service;

import com.boonsan.enums.ApplicationStatus;
import com.boonsan.enums.UnderwritingResultType;
import com.boonsan.enums.UnderwritingStatus;
import com.boonsan.enums.UnderwritingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.boonsan.underwriting.dto.UnderwritingApplicationCreateRequest;
import com.boonsan.underwriting.dto.UnderwritingApplicationResponse;
import com.boonsan.underwriting.dto.UnderwritingAutoScoreResponse;
import com.boonsan.underwriting.dto.UnderwritingDeductionItemResponse;
import com.boonsan.underwriting.dto.UnderwritingFinalizeRequest;
import com.boonsan.underwriting.dto.UnderwritingHistoryResponse;
import com.boonsan.underwriting.dto.UnderwritingReviewResponse;
import com.boonsan.underwriting.mapper.UnderwritingMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UnderwritingApplicationService {

    private static final int BASE_SCORE = 100;
    private static final int APPROVAL_SCORE = 85;
    private static final int SURCHARGE_SCORE = 65;
    private static final int COINSURANCE_RECOMMEND_SCORE = 70;

    private final UnderwritingMapper underwritingMapper;

    public UnderwritingApplicationService(UnderwritingMapper underwritingMapper) {
        this.underwritingMapper = underwritingMapper;
    }

    @Transactional
    public UnderwritingApplicationResponse createApplication(UnderwritingApplicationCreateRequest request) {
        String applicationId = generateId("APP");
        LocalDateTime appliedAt = LocalDateTime.now();
        String insuredPersonInfo = buildInsuredPersonInfo(request);
        String applicationStatus = ApplicationStatus.PENDING.name();

        underwritingMapper.insertApplication(
                applicationId,
                applicationStatus,
                appliedAt,
                normalizeOptionalText(request.getAppliedCondition()),
                request.getInsuredAmount(),
                insuredPersonInfo,
                requireText(request.getPaymentCycle(), "paymentCycle"),
                request.getPremium(),
                requireText(request.getProductCode(), "productCode"),
                normalizeOptionalText(request.getSpecialContractList()),
                requireText(request.getTermsVersion(), "termsVersion"),
                requireText(request.getInsuredPersonName(), "insuredPersonName"),
                request.getAge(),
                requireText(request.getGender(), "gender"),
                requireText(request.getOccupation(), "occupation"),
                request.getAnnualIncome(),
                normalizeOptionalText(request.getPastMedicalHistory()),
                request.isMedicated(),
                normalizeOptionalText(request.getSurgeryHistory()),
                normalizeOptionalText(request.getFamilyHistory()),
                request.isSmoker(),
                normalizeOptionalText(request.getAlcoholConsumption()),
                request.getBmi(),
                normalizeOptionalText(request.getVehicleModel()),
                normalizeOptionalText(request.getVehicleNumber()),
                request.isHasAccidentHistory(),
                request.isHasOtherContract()
        );

        insertHistory(
                applicationId,
                null,
                "APPLICATION_RECEIVED",
                "보험청약이 접수되었습니다.",
                null,
                applicationStatus,
                appliedAt
        );

        UnderwritingApplicationResponse response = requireApplication(applicationId);
        response.setNextStepMessage("자동심사 점수 계산을 실행할 수 있습니다.");
        return response;
    }

    @Transactional(readOnly = true)
    public UnderwritingApplicationResponse getApplication(String applicationId) {
        UnderwritingApplicationResponse response = requireApplication(applicationId);
        response.setNextStepMessage(buildApplicationNextStepMessage(response));
        return response;
    }

    @Transactional
    public UnderwritingAutoScoreResponse calculateAutoScore(String applicationId) {
        String normalizedApplicationId = requireText(applicationId, "applicationId");
        UnderwritingApplicationResponse application = requireApplication(normalizedApplicationId);
        UnderwritingReviewResponse existing = underwritingMapper.findLatestReviewByApplicationId(normalizedApplicationId);
        if (existing != null && existing.getFinalResult() != null) {
            throw new IllegalArgumentException("Final underwriting result already exists: " + normalizedApplicationId);
        }

        ScoreCalculation calculation = calculateScore(application);
        String reviewId = generateId("UWR");
        LocalDateTime createdAt = LocalDateTime.now();
        boolean autoReviewAvailable = calculation.totalScore >= APPROVAL_SCORE;
        boolean coinsuranceRecommended = calculation.totalScore < COINSURANCE_RECOMMEND_SCORE;
        String recommendedResult = toRecommendedResult(calculation.totalScore).name();

        underwritingMapper.insertReview(
                reviewId,
                normalizedApplicationId,
                UnderwritingStatus.IN_PROGRESS.name(),
                UnderwritingType.AUTO.name(),
                calculation.totalScore,
                calculation.totalDeduction,
                recommendedResult,
                autoReviewAvailable,
                coinsuranceRecommended,
                buildItemizedScoresText(calculation.items),
                createdAt
        );

        insertHistory(
                normalizedApplicationId,
                reviewId,
                "AUTO_SCORE_CALCULATED",
                "자동심사 점수 계산 및 보고서가 생성되었습니다.",
                calculation.totalScore,
                recommendedResult,
                createdAt
        );

        UnderwritingAutoScoreResponse response = new UnderwritingAutoScoreResponse();
        response.setApplicationId(normalizedApplicationId);
        response.setReviewId(reviewId);
        response.setTotalScore(calculation.totalScore);
        response.setTotalDeduction(calculation.totalDeduction);
        response.setRecommendedResult(recommendedResult);
        response.setAutoReviewAvailable(autoReviewAvailable);
        response.setManualReviewRequired(!autoReviewAvailable);
        response.setCoinsuranceRecommended(coinsuranceRecommended);
        response.setDeductionItems(calculation.items);
        response.setReportSummary(buildReportSummary(calculation.totalScore, recommendedResult, autoReviewAvailable));
        response.setCoinsuranceMessage(coinsuranceRecommended
                ? "심사점수 70점 미만으로 공동인수 추천 대상입니다. 실제 공동인수 처리는 다음 단계 예정입니다."
                : "공동인수 추천 대상이 아닙니다.");
        response.setReinsuranceMessage("재보험 실제 처리는 이번 단계에서 수행하지 않습니다.");
        response.setPolicyIssueMessage("증권 발행 실제 처리는 다음 단계 예정입니다.");
        response.setCreatedAt(createdAt);
        return response;
    }

    @Transactional
    public UnderwritingReviewResponse finalizeReview(String applicationId, UnderwritingFinalizeRequest request) {
        String normalizedApplicationId = requireText(applicationId, "applicationId");
        UnderwritingApplicationResponse application = requireApplication(normalizedApplicationId);
        UnderwritingReviewResponse latest = underwritingMapper.findLatestReviewByApplicationId(normalizedApplicationId);
        if (latest == null) {
            throw new IllegalArgumentException("Auto underwriting score must be calculated before final review.");
        }
        if (latest.getFinalResult() != null) {
            throw new IllegalArgumentException("Final underwriting result already exists: " + normalizedApplicationId);
        }

        UnderwritingResultType finalResult = parseFinalResult(request.getFinalResult());
        LocalDateTime finalizedAt = LocalDateTime.now();
        String appliedCondition = buildAppliedCondition(finalResult, request);
        String applicationStatus = finalResult == UnderwritingResultType.REJECTED
                ? ApplicationStatus.REJECTED.name()
                : ApplicationStatus.APPROVED.name();

        int updated = underwritingMapper.finalizeLatestReview(
                normalizedApplicationId,
                finalResult.name(),
                UnderwritingStatus.COMPLETED.name(),
                requireText(request.getUnderwriterId(), "underwriterId"),
                requireText(request.getUnderwriterName(), "underwriterName"),
                requireText(request.getDepartment(), "department"),
                normalizeOptionalText(request.getUnderwritingOpinion()),
                normalizeOptionalText(request.getSurchargeCondition()),
                normalizeOptionalText(request.getRejectionReason()),
                finalizedAt
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Final underwriting result already exists: " + normalizedApplicationId);
        }

        underwritingMapper.updateApplicationAfterFinalReview(normalizedApplicationId, applicationStatus, appliedCondition);
        insertHistory(
                normalizedApplicationId,
                latest.getReviewId(),
                "FINAL_REVIEW_SAVED",
                "최종 심사 결과가 저장되었습니다.",
                latest.getTotalScore(),
                finalResult.name(),
                finalizedAt
        );

        UnderwritingReviewResponse response = underwritingMapper.findLatestReviewByApplicationId(normalizedApplicationId);
        response.setNextStepMessage(buildFinalNextStepMessage(application, finalResult));
        return response;
    }

    @Transactional(readOnly = true)
    public List<UnderwritingHistoryResponse> getHistory(String applicationId) {
        String normalizedApplicationId = requireText(applicationId, "applicationId");
        requireApplication(normalizedApplicationId);
        return underwritingMapper.findHistoryByApplicationId(normalizedApplicationId);
    }

    private UnderwritingApplicationResponse requireApplication(String applicationId) {
        String normalizedApplicationId = requireText(applicationId, "applicationId");
        UnderwritingApplicationResponse response = underwritingMapper.findApplicationById(normalizedApplicationId);
        if (response == null) {
            throw new NoSuchElementException("Insurance application not found: " + normalizedApplicationId);
        }
        return response;
    }

    private ScoreCalculation calculateScore(UnderwritingApplicationResponse application) {
        List<UnderwritingDeductionItemResponse> items = new ArrayList<>();
        addDeduction(items, "과거질병이력", displayValue(application.getPastMedicalHistory(), "없음"),
                hasValue(application.getPastMedicalHistory()) ? -10 : 0, "과거질병이력 있음");
        addDeduction(items, "투약여부", application.isMedicated() ? "Y" : "N",
                application.isMedicated() ? -8 : 0, "투약여부 Y");
        addDeduction(items, "수술이력", displayValue(application.getSurgeryHistory(), "없음"),
                hasValue(application.getSurgeryHistory()) ? -7 : 0, "수술이력 있음");
        addDeduction(items, "가족력", displayValue(application.getFamilyHistory(), "없음"),
                hasValue(application.getFamilyHistory()) ? -5 : 0, "가족력 있음");
        addDeduction(items, "흡연여부", application.isSmoker() ? "Y" : "N",
                application.isSmoker() ? -5 : 0, "흡연여부 Y");
        addDeduction(items, "음주량", displayValue(application.getAlcoholConsumption(), "없음"),
                hasDrinking(application.getAlcoholConsumption()) ? -3 : 0, "음주량 있음");

        double bmi = application.getBmi() == null ? 0.0 : application.getBmi().doubleValue();
        int bmiDeduction = bmi > 30 ? -8 : bmi > 25 ? -3 : 0;
        addDeduction(items, "BMI", String.valueOf(application.getBmi()), bmiDeduction,
                bmi > 30 ? "비만" : bmi > 25 ? "과체중" : "정상");

        int age = application.getAge() == null ? 0 : application.getAge();
        int ageDeduction = age > 60 ? -10 : age > 50 ? -5 : 0;
        addDeduction(items, "나이", age + "세", ageDeduction, age > 60 ? "60세 초과" : age > 50 ? "50세 초과" : "기준 충족");

        addDeduction(items, "사고이력", application.isHasAccidentHistory() ? "있음" : "없음",
                application.isHasAccidentHistory() ? -5 : 0, "Mock/입력값 기반 신용정보 조회");
        addDeduction(items, "타사계약", application.isHasOtherContract() ? "있음" : "없음",
                application.isHasOtherContract() ? -3 : 0, "Mock/입력값 기반 타사계약 조회");

        int totalDeduction = items.stream().mapToInt(UnderwritingDeductionItemResponse::getDeduction).sum();
        float totalScore = Math.max(0, BASE_SCORE + totalDeduction);
        return new ScoreCalculation(items, totalDeduction, totalScore);
    }

    private void addDeduction(
            List<UnderwritingDeductionItemResponse> items,
            String itemName,
            String itemValue,
            int deduction,
            String reason
    ) {
        items.add(new UnderwritingDeductionItemResponse(itemName, itemValue, deduction, reason));
    }

    private UnderwritingResultType toRecommendedResult(float score) {
        if (score >= APPROVAL_SCORE) {
            return UnderwritingResultType.APPROVED;
        }
        if (score >= SURCHARGE_SCORE) {
            return UnderwritingResultType.SURCHARGE;
        }
        return UnderwritingResultType.REJECTED;
    }

    private UnderwritingResultType parseFinalResult(String finalResult) {
        try {
            return UnderwritingResultType.valueOf(requireText(finalResult, "finalResult"));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("finalResult must be APPROVED, SURCHARGE, or REJECTED.");
        }
    }

    private String buildInsuredPersonInfo(UnderwritingApplicationCreateRequest request) {
        return requireText(request.getInsuredPersonName(), "insuredPersonName")
                + " / " + request.getAge() + "세 / "
                + requireText(request.getGender(), "gender")
                + " / " + requireText(request.getOccupation(), "occupation");
    }

    private String buildItemizedScoresText(List<UnderwritingDeductionItemResponse> items) {
        StringBuilder builder = new StringBuilder();
        for (UnderwritingDeductionItemResponse item : items) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(item.getItemName())
                    .append(": ")
                    .append(item.getItemValue())
                    .append(" / ")
                    .append(item.getDeduction())
                    .append("점 / ")
                    .append(item.getReason());
        }
        return builder.toString();
    }

    private String buildReportSummary(float score, String result, boolean autoReviewAvailable) {
        return "총점 " + Math.round(score) + "점, 추천 결과 " + result
                + ", 자동심사 " + (autoReviewAvailable ? "가능" : "불가 - 수동심사 필요");
    }

    private String buildAppliedCondition(UnderwritingResultType finalResult, UnderwritingFinalizeRequest request) {
        if (finalResult == UnderwritingResultType.SURCHARGE) {
            return hasValue(request.getSurchargeCondition())
                    ? request.getSurchargeCondition().trim()
                    : "할증체";
        }
        if (finalResult == UnderwritingResultType.REJECTED) {
            return hasValue(request.getRejectionReason())
                    ? "거절: " + request.getRejectionReason().trim()
                    : "거절";
        }
        return "표준체";
    }

    private String buildApplicationNextStepMessage(UnderwritingApplicationResponse response) {
        if (ApplicationStatus.APPROVED.name().equals(response.getApplicationStatus())) {
            return "심사 완료 청약입니다. 증권 발행은 다음 단계 예정입니다.";
        }
        if (ApplicationStatus.REJECTED.name().equals(response.getApplicationStatus())) {
            return "거절 처리된 청약입니다.";
        }
        return "자동심사 또는 최종심사를 진행할 수 있습니다.";
    }

    private String buildFinalNextStepMessage(UnderwritingApplicationResponse application, UnderwritingResultType finalResult) {
        if (finalResult == UnderwritingResultType.REJECTED) {
            return "거절 처리로 청약 심사가 종료되었습니다.";
        }
        if (application.getInsuredAmount() != null
                && application.getInsuredAmount().compareTo(BigDecimal.valueOf(500_000_000L)) > 0) {
            return "보험가입금액이 자사 보유한도를 초과할 수 있습니다. 재보험 실제 처리는 다음 단계 예정입니다.";
        }
        return "증권 발행 및 계약 생성은 다음 단계 예정입니다.";
    }

    private void insertHistory(
            String applicationId,
            String reviewId,
            String eventType,
            String eventMessage,
            Float score,
            String result,
            LocalDateTime createdAt
    ) {
        underwritingMapper.insertHistory(
                generateId("UWH"),
                applicationId,
                reviewId,
                eventType,
                eventMessage,
                score,
                result,
                createdAt
        );
    }

    private String generateId(String prefix) {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return prefix + "-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }

    private String requireText(String value, String fieldName) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean hasValue(String value) {
        String normalized = normalizeOptionalText(value);
        return normalized != null
                && !"없음".equalsIgnoreCase(normalized)
                && !"N".equalsIgnoreCase(normalized)
                && !"아니오".equalsIgnoreCase(normalized)
                && !"none".equalsIgnoreCase(normalized);
    }

    private boolean hasDrinking(String value) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            return false;
        }
        if ("없음".equalsIgnoreCase(normalized) || "N".equalsIgnoreCase(normalized) || "아니오".equalsIgnoreCase(normalized)) {
            return false;
        }
        return !normalized.matches("주\\s*0\\s*회.*");
    }

    private String displayValue(String value, String fallback) {
        String normalized = normalizeOptionalText(value);
        return normalized == null ? fallback : normalized;
    }

    private static class ScoreCalculation {
        private final List<UnderwritingDeductionItemResponse> items;
        private final int totalDeduction;
        private final float totalScore;

        private ScoreCalculation(List<UnderwritingDeductionItemResponse> items, int totalDeduction, float totalScore) {
            this.items = items;
            this.totalDeduction = totalDeduction;
            this.totalScore = totalScore;
        }
    }
}
