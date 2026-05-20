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
import java.util.List;

// 손해조사 서비스 — 이의제기 처리 판정, DB 저장 담당
public class DamageInvestigationService {

    private static final DamageInvestigationDBO damageInvestigationDBO = new DamageInvestigationDBO();
    private static final InsurancePaymentDBO insurancePaymentDBO = new InsurancePaymentDBO();
    private static final ObjectionDBO objectionDBO = new ObjectionDBO();
    private static final OutsourceRequestDBO outsourceRequestDBO = new OutsourceRequestDBO();

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

    // 손해조사 결과를 DB에 저장하고 생성된 조사번호를 반환한다
    public static String saveDamageInvestigation(DamageInvestigation investigation, String reportNo) {
        if (investigation == null || investigation.getInvestigationId() == null || reportNo == null) {
            return null;
        }
        boolean saved = damageInvestigationDBO.save(investigation, reportNo, "PENDING");
        return saved ? investigation.getInvestigationId() : null;
    }

    public static List<DamageInvestigation> getDamageInvestigationList() {
        return damageInvestigationDBO.findAll();
    }

    public static DamageInvestigation findDamageInvestigationById(String investigationId) {
        return damageInvestigationDBO.findById(investigationId);
    }

    public static String getInvestigationStatus(String investigationId) {
        String status = damageInvestigationDBO.findStatusById(investigationId);
        return status == null ? "" : status;
    }

    // 보험금 지급 정보를 DB에 저장하고 성공 여부를 반환한다
    public static boolean saveInsurancePayment(InsurancePayment payment, String investigationId) {
        if (payment == null || payment.getPaymentId() == null || investigationId == null) {
            return false;
        }
        payment.setInvestigationId(investigationId);
        return insurancePaymentDBO.save(payment, investigationId,
                payment.getPaymentStatus() == null ? "PENDING" : payment.getPaymentStatus().name());
    }

    // 이의 신청 정보를 DB에 저장하고 성공 여부를 반환한다
    public static boolean saveObjection(Objection objection, String paymentId) {
        if (objection == null || objection.getObjectionId() == null || paymentId == null) {
            return false;
        }
        return objectionDBO.save(objection, paymentId,
                objection.getAcceptanceStatus() == null ? "PENDING" : objection.getAcceptanceStatus().name());
    }

    // 외부 위탁 의뢰 정보를 DB에 저장하고 성공 여부를 반환한다
    public static boolean saveOutsourceRequest(OutsourceRequest request, String investigationId, String partnerId) {
        if (request == null || request.getRequestId() == null || investigationId == null) {
            return false;
        }
        request.setInvestigationId(investigationId);
        request.setPartnerId(partnerId);
        return outsourceRequestDBO.save(request, investigationId, partnerId,
                request.getRequestStatus() == null ? "PENDING" : request.getRequestStatus().name());
    }

    public static InsurancePayment findInsurancePaymentByInvestigationId(String investigationId) {
        return insurancePaymentDBO.findByInvestigationId(investigationId);
    }

    public static List<Objection> findObjectionsByPaymentId(String paymentId) {
        return objectionDBO.findByPaymentId(paymentId);
    }

    public static List<OutsourceRequest> findOutsourceRequestsByInvestigationId(String investigationId) {
        return outsourceRequestDBO.findByInvestigationId(investigationId);
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
}
