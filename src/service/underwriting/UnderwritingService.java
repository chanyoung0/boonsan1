package service.underwriting;

import db.ContractDBO;
import db.InsuranceApplicationDBO;
import db.UnderwritingDBO;
import db.UnderwritingResultDBO;
import enums.ContractStatus;
import enums.PaymentCycle;
import enums.SurchargeCondition;
import enums.UnderwritingResultType;
import enums.UnderwritingStatus;
import enums.UnderwritingType;
import model.contract.Contract;
import model.underwriting.InsuranceApplication;
import model.underwriting.Underwriting;
import model.underwriting.UnderwritingResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 보험청약 심사 서비스 — 심사 점수 계산, 판정, 객체 저장 유스케이스 흐름 담당
public class UnderwritingService {

    private static final long REINSURANCE_THRESHOLD = 500_000_000L;

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

    // 공동인수 필요 여부 판단 (70점 미만)
    public static boolean needsCoinsurance(int score) {
        return score < 70;
    }

    // 재보험 처리 필요 여부 판단 (자사 보유한도 초과)
    public static boolean needsReinsurance(long insuranceAmount) {
        return insuranceAmount > REINSURANCE_THRESHOLD;
    }

    // Underwriting + UnderwritingResult 객체 생성 및 저장
    public static void saveUnderwritingResult(String empName, int score, String finalResult, boolean coinsuranceRecommended) {
        Underwriting underwriting = new Underwriting();
        underwriting.setUnderwriter(empName);
        underwriting.setTotalScore(score);
        underwriting.setUnderwritingType("거절".equals(finalResult) ? UnderwritingType.GENERAL : UnderwritingType.AUTO);
        underwriting.setUnderwritingStatus(UnderwritingStatus.COMPLETED);
        underwriting.setUnderwrittenAt(LocalDateTime.now());
        underwriting.setCoinsuranceRecommended(coinsuranceRecommended);
        if ("거절".equals(finalResult)) {
            underwriting.setDeductionReason("위험도 기준 초과 (총점 " + score + "점)");
        }

        UnderwritingResult uwResult = new UnderwritingResult();
        uwResult.setUnderwritingResult(
            "거절".equals(finalResult) ? UnderwritingResultType.REJECTED :
            "할증".equals(finalResult) ? UnderwritingResultType.SURCHARGE : UnderwritingResultType.APPROVED);
        uwResult.setConfirmedAt(LocalDateTime.now());
        if ("할증".equals(finalResult)) uwResult.setSurchargeCondition(SurchargeCondition.POOR_HEALTH);
        if ("거절".equals(finalResult)) uwResult.setRejectionReason("위험도 기준 초과 (총점 " + score + "점)");
        underwriting.setUnderwritingResult(uwResult);

        new UnderwritingResultDBO().save(uwResult);
        new UnderwritingDBO().save(underwriting);
    }

    // InsuranceApplication 생성 및 저장 — 이후 Contract 연결에 필요하므로 객체 반환
    public static InsuranceApplication createAndSaveApplication(String name, long insuranceAmount, String appliedCondition) {
        InsuranceApplication application = new InsuranceApplication();
        application.setProductCode("AUTO-001");
        application.setInsuredPersonInfo(name);
        application.setInsuredAmount(BigDecimal.valueOf(insuranceAmount));
        application.setPremium(BigDecimal.valueOf(insuranceAmount / 1000));
        application.setPaymentCycle("월납");
        application.setAppliedCondition(appliedCondition);
        application.setTermsVersion("v2024.1");
        application.receiveApplication();
        new InsuranceApplicationDBO().save(application);
        return application;
    }

    // Contract 생성 및 저장
    public static void createAndSaveContract(String policyNo, InsuranceApplication application) {
        Contract contract = new Contract();
        contract.setPolicyNumber(policyNo);
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setPaymentCycle(PaymentCycle.MONTHLY);
        contract.setHasUnpaidPremium(false);
        contract.setInsuranceApplication(application);
        new ContractDBO().save(contract);
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
