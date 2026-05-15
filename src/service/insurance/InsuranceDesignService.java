package service.insurance;

import db.AuthorizationDBO;
import db.InsuranceDBO;
import model.insurance.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

// 상품 설계 서비스 — 보험 상품 설계 저장 및 금융감독원 인가 요청 유스케이스 흐름 담당
public class InsuranceDesignService {

    private final Random rnd = new Random();

    // 상품 유형에 맞는 Insurance 객체 생성 및 저장
    public Insurance saveInsurance(String typeChoice, String productCode, String insurancePeriod,
                                   String insuredAmountStr, String premiumStr) {
        BigDecimal insuredAmount  = parseSafe(insuredAmountStr, 100_000_000L);
        BigDecimal premium        = parseSafe(premiumStr, 150_000L);
        BigDecimal maturityRefund = premium.multiply(new BigDecimal("0.1"));

        Insurance insurance;
        switch (typeChoice) {
            case "2":
                insurance = new FireInsurance(productCode, insurancePeriod, insuredAmount, premium, maturityRefund, "아파트", "서울");
                break;
            case "3":
                insurance = new MarineInsurance(productCode, insurancePeriod, insuredAmount, premium, maturityRefund, "화물선", "부산-도쿄");
                break;
            default:
                insurance = new AutoInsurance(productCode, insurancePeriod, insuredAmount, premium, maturityRefund, 30, "승용차");
        }
        new InsuranceDBO().save(insurance);
        return insurance;
    }

    // Authorization 객체 생성 및 금융감독원에 요청 전송
    public Authorization requestAuthorization(String requestReason, String submissionAgencyName) {
        FinancialSupervisoryService fss = new FinancialSupervisoryService("FSS-001", "금융감독원");
        Authorization auth = new Authorization(
            "AUTH-" + System.currentTimeMillis(),
            requestReason, submissionAgencyName, LocalDateTime.now(), fss
        );
        auth.sendAuthorizationRequest();
        new AuthorizationDBO().save(auth);
        return auth;
    }

    // 금융감독원 인가 결과 시뮬레이션 — 70% 승인, 20% 보완 요청, 10% 불허
    public String simulateAuthorizationResult() {
        int r = rnd.nextInt(10);
        if (r < 7) return "승인";
        if (r < 9) return "보완 요청";
        return "불허";
    }

    private BigDecimal parseSafe(String s, long defaultVal) {
        try { return new BigDecimal(s.replaceAll("[^0-9]", "")); }
        catch (Exception e) { return BigDecimal.valueOf(defaultVal); }
    }
}
