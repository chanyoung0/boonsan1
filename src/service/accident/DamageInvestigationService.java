package service.accident;

import db.DamageInvestigationDBO;
import db.InsurancePaymentDBO;
import enums.PaymentStatus;
import model.accident.DamageInvestigation;
import model.accident.InsurancePayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 손해조사 서비스 — 이의제기 처리 판정, 손해조사/보험금 객체 저장 유스케이스 흐름 담당
public class DamageInvestigationService {

    // 이의제기 처리 결과 메시지 반환
    public String processObjection(String choice) {
        switch (choice) {
            case "1": return "기각 사유서가 DB에 저장되었습니다.";
            case "2": return "이의 제기 수용 — 사건 상태: '재조사 필요'";
            case "3": return "사건 상태: '법률과 이관' — 법률과 처리 완료 시 결과 알림 발송.";
            default:  return "기각 처리합니다.";
        }
    }

    // 구상 처리 필요 여부 판단
    public boolean needsSubrogation(String answer) {
        return "Y".equalsIgnoreCase(answer);
    }

    // DamageInvestigation 객체 생성 및 저장 — 이후 보험금 지급에 필요하므로 객체 반환
    public DamageInvestigation saveInvestigation(String adjusterNo,
            String medicalExpenseStr, String lostIncomeStr,
            String compensationStr, String repairCostStr, String faultRatioStr) {
        BigDecimal medicalExpense   = parseMoney(medicalExpenseStr);
        BigDecimal lostIncome       = parseMoney(lostIncomeStr);
        BigDecimal repairCost       = parseMoney(repairCostStr);
        BigDecimal settlementAmount = medicalExpense.add(lostIncome)
                                                    .add(parseMoney(compensationStr))
                                                    .add(repairCost);
        float faultRatio = parseRatio(faultRatioStr);

        String investigationId = "INV-" + System.currentTimeMillis();
        DamageInvestigation investigation = new DamageInvestigation(
            investigationId, adjusterNo, faultRatio,
            repairCost, medicalExpense, lostIncome, settlementAmount,
            LocalDateTime.now()
        );
        new DamageInvestigationDBO().save(investigation);
        return investigation;
    }

    // InsurancePayment 객체 생성, 이체 처리 및 저장
    public InsurancePayment processPayment(String processorEmpNo, DamageInvestigation investigation) {
        String paymentId = "PAY-" + System.currentTimeMillis();
        InsurancePayment payment = new InsurancePayment(
            paymentId,
            "신한은행 110-123-456789",
            processorEmpNo,
            investigation.getSettlementAmount(),
            investigation.getRepairCost(),
            investigation.getMedicalExpense(),
            investigation.getLostIncome(),
            BigDecimal.ZERO,
            PaymentStatus.PENDING
        );
        payment.transfer();
        new InsurancePaymentDBO().save(payment);
        return payment;
    }

    private BigDecimal parseMoney(String s) {
        try { return new BigDecimal(s.replaceAll("[^0-9.]", "")); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }

    private float parseRatio(String s) {
        try { return Float.parseFloat(s.replaceAll("[^0-9.]", "")); }
        catch (Exception e) { return 0f; }
    }
}
