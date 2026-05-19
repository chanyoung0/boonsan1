package service.accident;

import db.DamageInvestigationDBO;
import db.InsurancePaymentDBO;
import db.ObjectionDBO;
import db.OutsourceRequestDBO;
import model.accident.DamageInvestigation;
import model.accident.InsurancePayment;
import model.accident.Objection;
import model.accident.OutsourceRequest;

import java.math.BigDecimal;

// 손해조사 서비스 — 이의제기/입력 파싱 + 손해조사/위탁/지급/이의 영속화 담당
public class DamageInvestigationService {

    private static final DamageInvestigationDBO damageInvestigationDBO = new DamageInvestigationDBO();
    private static final OutsourceRequestDBO outsourceRequestDBO = new OutsourceRequestDBO();
    private static final InsurancePaymentDBO insurancePaymentDBO = new InsurancePaymentDBO();
    private static final ObjectionDBO objectionDBO = new ObjectionDBO();

    // 이의제기 처리 결과 메시지 반환
    public static String processObjection(String choice) {
        switch (choice) {
            case "1": return "기각 사유서가 DB에 저장되었습니다.";
            case "2": return "이의 제기 수용 — 사건 상태: '재조사 필요'";
            case "3": return "사건 상태: '법률과 이관' — 법률과 처리 완료 시 결과 알림 발송.";
            default:  return "기각 처리합니다.";
        }
    }

    // 구상 처리 필요 여부 판단
    public static boolean needsSubrogation(String answer) {
        return "Y".equalsIgnoreCase(answer);
    }

    public static boolean isYes(String answer) {
        return "Y".equalsIgnoreCase(answer) || "1".equals(answer);
    }

    public static BigDecimal parseAmount(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value.replace(",", "").replace("원", "").trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal sumAmounts(BigDecimal... amounts) {
        BigDecimal total = BigDecimal.ZERO;
        if (amounts == null) {
            return total;
        }

        for (BigDecimal amount : amounts) {
            if (amount != null) {
                total = total.add(amount);
            }
        }
        return total;
    }

    public static boolean saveInvestigation(DamageInvestigation investigation) {
        return damageInvestigationDBO.save(investigation);
    }

    public static boolean saveOutsourceRequest(OutsourceRequest request) {
        return outsourceRequestDBO.save(request);
    }

    public static boolean savePayment(InsurancePayment payment) {
        return insurancePaymentDBO.save(payment);
    }

    public static boolean updatePayment(InsurancePayment payment) {
        return insurancePaymentDBO.update(payment);
    }

    public static boolean saveObjection(Objection objection) {
        return objectionDBO.save(objection);
    }
}
