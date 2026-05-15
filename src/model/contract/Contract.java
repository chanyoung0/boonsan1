package model.contract;

import enums.ContractStatus;
import enums.PaymentCycle;
import model.accident.AccidentReport;
import model.document.Document;
import model.insurance.Insurance;
import model.person.Account;
import model.person.InsuredPerson;
import model.underwriting.InsuranceApplication;
import model.underwriting.Reinsurance;
import model.underwriting.UnderwritingHistory;

import java.util.ArrayList;
import java.util.List;

// 보험 계약 도메인 모델 — 증권발행 후 생성되는 핵심 계약 엔티티, 다수 종속 객체의 집약 클래스
public class Contract {

    private String policyNumber;
    private ContractStatus contractStatus;
    private PaymentCycle paymentCycle;
    private Boolean hasUnpaidPremium;
    private int installmentCount;

    // 관계 필드 (Aggregation: Contract → 부분 객체)
    private Insurance insurance;
    private InsuranceApplication application;
    private InsuredPerson insuredPerson;
    private Account autoTransferAccount;
    private Reinsurance reinsurance;
    private MaturityNotice maturityNotice;
    private final List<Endorsement> endorsements = new ArrayList<>();
    private final List<Reinstatement> reinstatements = new ArrayList<>();
    private final List<PaymentCollection> paymentCollections = new ArrayList<>();
    private final List<Payout> payouts = new ArrayList<>();
    private final List<AccidentReport> accidentReports = new ArrayList<>();
    private final List<UnderwritingHistory> underwritingHistories = new ArrayList<>();
    private final List<CompensationEvaluation> compensationEvaluations = new ArrayList<>();
    private final List<Document> documents = new ArrayList<>();

    public Contract() {}

    // 계약 기본 정보로 초기화
    public Contract(String policyNumber, ContractStatus contractStatus, PaymentCycle paymentCycle,
                    int installmentCount, boolean hasUnpaidPremium) {
        this.policyNumber = policyNumber;
        this.contractStatus = contractStatus;
        this.paymentCycle = paymentCycle;
        this.installmentCount = installmentCount;
        this.hasUnpaidPremium = hasUnpaidPremium;
    }

    // 계약 상태 변경
    public void changeContractStatus() {}

    // 납입 상태 확인 — 미납 여부 기준 정상 납입이면 true 반환
    public boolean checkPaymentStatus() {
        return Boolean.FALSE.equals(hasUnpaidPremium);
    }

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

    public String getPolicyNumber() { return policyNumber; }
    public ContractStatus getContractStatus() { return contractStatus; }
    public PaymentCycle getPaymentCycle() { return paymentCycle; }
    public Boolean getHasUnpaidPremium() { return hasUnpaidPremium; }
    public int getInstallmentCount() { return installmentCount; }
    public Insurance getInsurance() { return insurance; }
    public InsuranceApplication getApplication() { return application; }
    public InsuredPerson getInsuredPerson() { return insuredPerson; }
    public Account getAutoTransferAccount() { return autoTransferAccount; }
    public Reinsurance getReinsurance() { return reinsurance; }
    public MaturityNotice getMaturityNotice() { return maturityNotice; }
    public List<Endorsement> getEndorsements() { return endorsements; }
    public List<Reinstatement> getReinstatements() { return reinstatements; }
    public List<PaymentCollection> getPaymentCollections() { return paymentCollections; }
    public List<Payout> getPayouts() { return payouts; }
    public List<AccidentReport> getAccidentReports() { return accidentReports; }
    public List<UnderwritingHistory> getUnderwritingHistories() { return underwritingHistories; }
    public List<CompensationEvaluation> getCompensationEvaluations() { return compensationEvaluations; }
    public List<Document> getDocuments() { return documents; }

    public void setPolicyNumber(String s) { this.policyNumber = s; }
    public void setContractStatus(ContractStatus s) { this.contractStatus = s; }
    public void setPaymentCycle(PaymentCycle c) { this.paymentCycle = c; }
    public void setHasUnpaidPremium(Boolean b) { this.hasUnpaidPremium = b; }
    public void setInstallmentCount(int v) { this.installmentCount = v; }
    public void setInsurance(Insurance i) { this.insurance = i; }
    public void setApplication(InsuranceApplication a) { this.application = a; }
    public void setInsuredPerson(InsuredPerson p) { this.insuredPerson = p; }
    public void setAutoTransferAccount(Account a) { this.autoTransferAccount = a; }
    public void setReinsurance(Reinsurance r) { this.reinsurance = r; }
    public void setMaturityNotice(MaturityNotice m) { this.maturityNotice = m; }
    public void addEndorsement(Endorsement e) { this.endorsements.add(e); }
    public void addReinstatement(Reinstatement r) { this.reinstatements.add(r); }
    public void addPaymentCollection(PaymentCollection p) { this.paymentCollections.add(p); }
    public void addPayout(Payout p) { this.payouts.add(p); }
    public void addAccidentReport(AccidentReport r) { this.accidentReports.add(r); }
    public void addUnderwritingHistory(UnderwritingHistory h) { this.underwritingHistories.add(h); }
    public void addCompensationEvaluation(CompensationEvaluation c) { this.compensationEvaluations.add(c); }
    public void addDocument(Document d) { this.documents.add(d); }

    @Override
    public String toString() {
        return "Contract{policyNumber='" + policyNumber + "', status=" + contractStatus
                + ", paymentCycle=" + paymentCycle + ", unpaid=" + hasUnpaidPremium + "}";
    }
}
