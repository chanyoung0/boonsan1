package model.accident;

import enums.SubrogationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 구상권 도메인 모델 — 보험금 지급 후 제3자 구상 처리 정보 관리
public class Subrogation {

    private String depositAccount;
    private float faultRatio;
    private String offenderContact;
    private String offenderName;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentDeadline;
    private String subrogationId;
    private SubrogationStatus subrogationStatus;

    // 입금 확인
    public void confirmDeposit() {}

    // 구상 문서 생성
    public void generateSubrogationDocument() {}

    // 지급 상세 조회
    public InsurancePayment retrievePaymentDetails() { return null; }

    // 구상 청구 발송
    public void sendClaim() {}
}
