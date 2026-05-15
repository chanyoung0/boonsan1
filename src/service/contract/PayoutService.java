package service.contract;

import db.PayoutDBO;
import enums.CalculationBasis;
import enums.PaymentType;
import model.contract.Payout;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

// 제지급금 관리 서비스 — 지급금 산출, 이체 처리, 지급결과 저장 유스케이스 흐름 담당
public class PayoutService {

    private static final Random rnd = new Random();

    // 지급 가능 여부 확인 (랜덤 시뮬레이션, 90% 지급 가능)
    public static boolean isPayable() {
        return rnd.nextInt(10) < 9;
    }

    // 지급유형 텍스트 변환
    public static String resolvePaymentTypeLabel(String choice) {
        switch (choice) {
            case "1": return "만기환급금";
            case "2": return "중도인출금";
            case "3": return "해지환급금";
            case "4": return "배당금";
            default:  return "만기환급금";
        }
    }

    // 지급금 산출 금액 반환 (시뮬레이션)
    public static BigDecimal calculateAmount(String choice) {
        switch (choice) {
            case "1": return new BigDecimal("3500000");
            case "2": return new BigDecimal("500000");
            case "3": return new BigDecimal("2800000");
            case "4": return new BigDecimal("120000");
            default:  return new BigDecimal("3500000");
        }
    }

    // Payout 객체 생성 및 저장
    public static Payout savePayout(String processor, String paymentTypeChoice, BigDecimal calculatedAmount) {
        PaymentType paymentType = "2".equals(paymentTypeChoice) ? PaymentType.INSTALLMENT :
                                  "3".equals(paymentTypeChoice) ? PaymentType.LOAN_SETTLEMENT :
                                  PaymentType.LUMP_SUM;
        CalculationBasis basis  = "1".equals(paymentTypeChoice) ? CalculationBasis.MATURITY_REFUND :
                                  "2".equals(paymentTypeChoice) ? CalculationBasis.MID_SURRENDER :
                                  "3".equals(paymentTypeChoice) ? CalculationBasis.SURRENDER :
                                  CalculationBasis.DIVIDEND;

        Payout payout = new Payout(processor, paymentType, basis, calculatedAmount, calculatedAmount,
                                   "없음", LocalDateTime.now());
        payout.setPaidAt(LocalDateTime.now());
        new PayoutDBO().save(payout);
        return payout;
    }
}
