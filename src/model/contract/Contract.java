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
import model.underwriting.UnderwritingRequest;

import java.util.ArrayList;
import java.util.List;

// 보험 계약 도메인 모델 — 증권발행 완료 후 생성되는 핵심 계약 엔티티
public class Contract {

    private Account autoTransferAmount;
    private ContractStatus contractStatus;
    private PaymentCycle paymentCycle;
    private Boolean hasUnpaidPremium;
    private int installmentCount;
    private String policyNumber;

    private Insurance insurance;
    private InsuredPerson insuredPerson;
    private Account account;
    private InsuranceApplication insuranceApplication;
    private Reinstatement reinstatement;
    private Reinsurance reinsurance;
    private UnderwritingHistory underwritingHistory;
    private MaturityNotice maturityNotice;
    private List<Endorsement> endorsementList;
    private List<AccidentReport> accidentReportList;
    private List<PaymentCollection> paymentCollectionList;
    private List<UnderwritingRequest> underwritingRequestList;
    private List<Payout> payoutList;
    private List<Document> documentList;

    public Contract() {
        this.endorsementList = new ArrayList<>();
        this.accidentReportList = new ArrayList<>();
        this.paymentCollectionList = new ArrayList<>();
        this.underwritingRequestList = new ArrayList<>();
        this.payoutList = new ArrayList<>();
        this.documentList = new ArrayList<>();
    }

    public Contract(String policyNumber, ContractStatus contractStatus, PaymentCycle paymentCycle,
                    Boolean hasUnpaidPremium, int installmentCount,
                    Insurance insurance, InsuredPerson insuredPerson, Account account,
                    InsuranceApplication insuranceApplication) {
        this();
        this.policyNumber = policyNumber;
        this.contractStatus = contractStatus;
        this.paymentCycle = paymentCycle;
        this.hasUnpaidPremium = hasUnpaidPremium;
        this.installmentCount = installmentCount;
        this.insurance = insurance;
        this.insuredPerson = insuredPerson;
        this.account = account;
        this.insuranceApplication = insuranceApplication;
    }

    // 계약 상태 변경
    public void changeContractStatus() {}

    // 납입 상태 확인 — 미납 여부 반환
    public boolean checkPaymentStatus() { return hasUnpaidPremium != null && hasUnpaidPremium; }

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

    public Account                    getAutoTransferAmount()                         { return autoTransferAmount; }
    public void                       setAutoTransferAmount(Account v)                { this.autoTransferAmount = v; }
    public ContractStatus             getContractStatus()                             { return contractStatus; }
    public void                       setContractStatus(ContractStatus v)             { this.contractStatus = v; }
    public PaymentCycle               getPaymentCycle()                               { return paymentCycle; }
    public void                       setPaymentCycle(PaymentCycle v)                 { this.paymentCycle = v; }
    public Boolean                    getHasUnpaidPremium()                           { return hasUnpaidPremium; }
    public void                       setHasUnpaidPremium(Boolean v)                  { this.hasUnpaidPremium = v; }
    public int                        getInstallmentCount()                           { return installmentCount; }
    public void                       setInstallmentCount(int v)                      { this.installmentCount = v; }
    public String                     getPolicyNumber()                               { return policyNumber; }
    public void                       setPolicyNumber(String v)                       { this.policyNumber = v; }
    public Insurance                  getInsurance()                                  { return insurance; }
    public void                       setInsurance(Insurance v)                       { this.insurance = v; }
    public InsuredPerson              getInsuredPerson()                              { return insuredPerson; }
    public void                       setInsuredPerson(InsuredPerson v)               { this.insuredPerson = v; }
    public Account                    getAccount()                                    { return account; }
    public void                       setAccount(Account v)                           { this.account = v; }
    public InsuranceApplication       getInsuranceApplication()                       { return insuranceApplication; }
    public void                       setInsuranceApplication(InsuranceApplication v) { this.insuranceApplication = v; }
    public Reinstatement              getReinstatement()                              { return reinstatement; }
    public void                       setReinstatement(Reinstatement v)               { this.reinstatement = v; }
    public Reinsurance                getReinsurance()                                { return reinsurance; }
    public void                       setReinsurance(Reinsurance v)                   { this.reinsurance = v; }
    public UnderwritingHistory        getUnderwritingHistory()                        { return underwritingHistory; }
    public void                       setUnderwritingHistory(UnderwritingHistory v)   { this.underwritingHistory = v; }
    public MaturityNotice             getMaturityNotice()                             { return maturityNotice; }
    public void                       setMaturityNotice(MaturityNotice v)             { this.maturityNotice = v; }
    public List<Endorsement>          getEndorsementList()                            { return endorsementList; }
    public void                       setEndorsementList(List<Endorsement> v)         { this.endorsementList = v; }
    public List<AccidentReport>       getAccidentReportList()                         { return accidentReportList; }
    public void                       setAccidentReportList(List<AccidentReport> v)   { this.accidentReportList = v; }
    public List<PaymentCollection>    getPaymentCollectionList()                      { return paymentCollectionList; }
    public void                       setPaymentCollectionList(List<PaymentCollection> v){ this.paymentCollectionList = v; }
    public List<UnderwritingRequest>  getUnderwritingRequestList()                    { return underwritingRequestList; }
    public void                       setUnderwritingRequestList(List<UnderwritingRequest> v){ this.underwritingRequestList = v; }
    public List<Payout>               getPayoutList()                                 { return payoutList; }
    public void                       setPayoutList(List<Payout> v)                   { this.payoutList = v; }
    public List<Document>             getDocumentList()                               { return documentList; }
    public void                       setDocumentList(List<Document> v)               { this.documentList = v; }

    @Override
    public String toString() {
        return "Contract{policyNumber='" + policyNumber + "', contractStatus=" + contractStatus + "}";
    }
}
