package model.contract;

import enums.ContractStatus;
import enums.PaymentCycle;
import model.person.Account;

// 보험 계약 도메인 모델 — 증권발행 완료 후 생성되는 핵심 계약 엔티티
public class Contract {

    private Account autoTransferAmount;
    private ContractStatus contractStatus;
    private PaymentCycle paymentCycle;
    private Boolean hasUnpaidPremium;
    private int installmentCount;
    private String policyNumber;

    // 계약 상태 변경
    public void changeContractStatus() {}

    // 납입 상태 확인
    public void checkPaymentStatus() {}

    // 계약 실행
    public void executeContract() {}

    // 계약 정보 조회
    public void getContractInfo() {}

    // 증권번호 발행
    public void issuePolicyNumber() {}

    // 계약 갱신
    public void renewContract() {}

    // 계약 종료
    public void terminateContract() {}

    public String getPolicyNumber()           { return policyNumber; }
    public ContractStatus getContractStatus() { return contractStatus; }
}
