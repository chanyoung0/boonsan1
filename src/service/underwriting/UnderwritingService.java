package service.underwriting;

import db.AccidentHistoryDBO;
import db.InsuranceApplicationDBO;
import db.UnderwritingDBO;
import db.UnderwritingHistoryDBO;
import db.UnderwritingRequestDBO;
import db.UnderwritingResultDBO;
import enums.ApplicationStatus;
import enums.Gender;
import enums.RequestReason;
import enums.RequestStatus;
import enums.SurchargeCondition;
import enums.UnderwritingResultType;
import enums.UnderwritingStatus;
import enums.UnderwritingType;
import model.accident.AccidentHistory;
import model.underwriting.InsuranceApplication;
import model.underwriting.Underwriting;
import model.underwriting.UnderwritingHistory;
import model.underwriting.UnderwritingRequest;
import model.underwriting.UnderwritingResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 보험청약 심사 서비스 — 심사 점수 계산, 판정, DB 저장 담당
public class UnderwritingService {

    private static final long REINSURANCE_THRESHOLD = 500_000_000L;
    private static final UnderwritingDBO underwritingDBO = new UnderwritingDBO();
    private static final InsuranceApplicationDBO applicationDBO = new InsuranceApplicationDBO();
    private static final UnderwritingResultDBO resultDBO = new UnderwritingResultDBO();
    private static final UnderwritingHistoryDBO historyDBO = new UnderwritingHistoryDBO();
    private static final UnderwritingRequestDBO requestDBO = new UnderwritingRequestDBO();
    private static final AccidentHistoryDBO accidentHistoryDBO = new AccidentHistoryDBO();

    // 입력값 기반 심사점수 계산 (기본점수 100점에서 감점 합산)
    public static int calculateInputScore(String pastDisease, String medication, String surgery,
                                          String familyHistory, String smoking, String drinking,
                                          String bmi, String age, boolean hasAccident, boolean hasOtherContract) {
        int score = 100;
        if (hasValue(pastDisease))  score -= 10;
        if (isYes(medication))      score -=  8;
        if (hasValue(surgery))      score -=  7;
        if (hasValue(familyHistory))score -=  5;
        if (isYes(smoking))         score -=  5;
        if (hasValue(drinking) && !isDrinkingNone(drinking)) score -= 3;
        double bmiVal = parseBmi(bmi);
        if (bmiVal > 30)       score -= 8;
        else if (bmiVal > 25)  score -= 3;
        int ageVal = parseAge(age);
        if (ageVal > 60)       score -= 10;
        else if (ageVal > 50)  score -=  5;
        if (hasAccident)       score -=  5;
        if (hasOtherContract)  score -=  3;
        return Math.max(0, score);
    }

    // 수동심사 유형별 감점값 반환
    public static int getManualUnderwritingAdjustment(String type) {
        switch (type) {
            case "1": return -10;
            case "2": return  -5;
            case "3": return  -3;
            case "4": return  -2;
            case "5": return  -8;
            default:  return  -3;
        }
    }

    // 심사결과 판정 (점수 기준)
    public static String determineResult(int score) {
        if (score >= 85) return "승인";
        if (score >= 65) return "할증";
        return "거절";
    }

    // 자동심사 가능 여부 판단
    public static boolean canAutoReview(int score) {
        return score >= 85 || score < 65;
    }

    // 공동인수 필요 여부 판단 (70점 미만)
    public static boolean needsCoinsurance(int score) {
        return score < 70;
    }

    // 재보험 처리 필요 여부 판단 (자사 보유한도 초과)
    public static boolean needsReinsurance(long insuranceAmount) {
        return insuranceAmount > REINSURANCE_THRESHOLD;
    }

    // 심사결과를 DB에 저장하고 생성된 심사번호를 반환한다
    public static String saveUnderwritingResult(String empNo, String empName, String empDept,
                                                int score, String finalResult, boolean wasManual) {
        String underwritingId = "UW-" + System.currentTimeMillis();
        Underwriting underwriting = new Underwriting();
        underwriting.setUnderwriter(empName);
        underwriting.setTotalScore(score);
        underwriting.setUnderwritingStatus(UnderwritingStatus.COMPLETED);
        underwriting.setUnderwritingType(wasManual ? UnderwritingType.GENERAL : UnderwritingType.AUTO);
        underwriting.setUnderwrittenAt(LocalDateTime.now());
        if ("거절".equals(finalResult)) {
            underwriting.setDeductionReason("위험도 기준 초과 (총점 " + score + "점)");
        }
        boolean saved = underwritingDBO.save(underwriting, underwritingId, empNo, empName, empDept,
                resolveUnderwritingResultCode(finalResult));
        if (!saved) {
            return null;
        }
        UnderwritingResult result = new UnderwritingResult();
        result.setResultId("RES-" + System.currentTimeMillis());
        result.setUnderwritingId(underwritingId);
        result.setUnderwritingResult(resolveUnderwritingResultType(finalResult));
        result.setConfirmedAt(LocalDateTime.now());
        if ("거절".equals(finalResult)) {
            result.setRejectionReason("위험도 기준 초과 (총점 " + score + "점)");
        }
        if ("할증".equals(finalResult)) {
            result.setSurchargeCondition(SurchargeCondition.POOR_HEALTH);
        }
        resultDBO.save(result, result.getResultId(), underwritingId);
        return underwritingId;
    }

    // 심사 이력을 DB에 저장한다
    public static boolean saveUnderwritingHistory(UnderwritingHistory history, String underwritingId) {
        if (history == null || underwritingId == null) {
            return false;
        }
        String historyId = "HIST-" + System.currentTimeMillis();
        history.setHistoryId(historyId);
        history.setUnderwritingId(underwritingId);
        history.setInquiredAt(LocalDateTime.now());
        return historyDBO.save(history, historyId, underwritingId);
    }

    // 심사 요청을 DB에 저장한다
    public static boolean saveUnderwritingRequest(String underwritingId, boolean wasManual, String finalResult) {
        if (underwritingId == null) {
            return false;
        }
        UnderwritingRequest request = new UnderwritingRequest();
        request.setRequestId("REQ-" + System.currentTimeMillis());
        request.setRequestReason(RequestReason.NEW_APPLICATION);
        request.setRequestStatus(RequestStatus.COMPLETED);
        request.setUnderwritingType(wasManual ? UnderwritingType.GENERAL : UnderwritingType.AUTO);
        request.setUnderwritingResult(resolveUnderwritingResultType(finalResult));
        request.setAppliedAt(LocalDateTime.now());
        return requestDBO.save(request, request.getRequestId());
    }

    // 청약 정보를 DB에 저장하고 성공 여부를 반환한다
    public static boolean saveInsuranceApplication(String applicationId, String policyNumber,
                                                   String appliedCondition) {
        InsuranceApplication application = new InsuranceApplication();
        application.setApplicationId(applicationId);
        application.setApplicationStatus(ApplicationStatus.APPROVED);
        application.setAppliedCondition(appliedCondition);
        application.setAppliedAt(LocalDateTime.now());
        return applicationDBO.save(application, policyNumber, "APPROVED", appliedCondition);
    }

    public static List<AccidentHistory> getAccidentHistoryList() {
        return accidentHistoryDBO.findAll();
    }

    public static List<AccidentHistory> findAccidentHistoriesByHistoryId(String historyId) {
        return accidentHistoryDBO.findByHistoryId(historyId);
    }

    public static List<Underwriting> getUnderwritingList() {
        return underwritingDBO.findAll();
    }

    public static Underwriting findUnderwritingById(String underwritingId) {
        return underwritingDBO.findById(underwritingId);
    }

    public static String getUnderwritingStatus(String underwritingId) {
        String status = underwritingDBO.findStatusById(underwritingId);
        return status == null ? "" : status;
    }

    public static String getUnderwritingResult(String underwritingId) {
        String result = underwritingDBO.findResultById(underwritingId);
        return result == null ? "" : result;
    }

    private static UnderwritingResultType resolveUnderwritingResultType(String finalResult) {
        if ("할증".equals(finalResult)) return UnderwritingResultType.SURCHARGE;
        if ("거절".equals(finalResult)) return UnderwritingResultType.REJECTED;
        return UnderwritingResultType.APPROVED;
    }

    private static String resolveUnderwritingResultCode(String finalResult) {
        if ("할증".equals(finalResult)) return "SURCHARGE";
        if ("거절".equals(finalResult)) return "REJECTED";
        return "APPROVED";
    }

    private static boolean isYes(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        String v = val.trim().toUpperCase();
        return v.equals("Y") || v.equals("예") || v.startsWith("Y");
    }

    private static boolean hasValue(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        String v = val.trim();
        return !v.equalsIgnoreCase("없음") && !v.equalsIgnoreCase("N")
                && !v.equalsIgnoreCase("아니오") && !v.equalsIgnoreCase("none");
    }

    private static boolean isDrinkingNone(String val) {
        if (val == null) return true;
        String v = val.trim();
        return v.equalsIgnoreCase("없음") || v.equalsIgnoreCase("N")
                || v.equalsIgnoreCase("아니오") || v.matches("주\\s*0\\s*회.*");
    }

    private static int parseAge(String s) {
        try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (NumberFormatException e) { return 0; }
    }

    private static double parseBmi(String s) {
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return 0.0; }
    }
}
