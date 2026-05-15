package model.person;

// 피보험자 도메인 모델 — 보험 계약의 피보험자 신상 및 계좌 정보 관리
public class InsuredPerson {

    private Account accountInfo;
    private String contact;
    private String name;
    private String residentRegistrationNumber;

    public InsuredPerson() {}

    public InsuredPerson(String name, String residentRegistrationNumber, String contact, Account accountInfo) {
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.contact = contact;
        this.accountInfo = accountInfo;
    }

    // 피보험자 정보 조회
    public void getInsuredPersonInfo() {
        if (name == null || name.isEmpty())
            throw new IllegalStateException("피보험자 이름이 없습니다.");
        if (residentRegistrationNumber == null || residentRegistrationNumber.isEmpty())
            throw new IllegalStateException("주민등록번호가 없습니다.");
    }

    // 피보험자 등록
    public void registerInsuredPerson() {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("피보험자 이름이 필요합니다.");
        if (residentRegistrationNumber == null || residentRegistrationNumber.isEmpty())
            throw new IllegalArgumentException("주민등록번호가 필요합니다.");
        if (contact == null || contact.isEmpty())
            throw new IllegalArgumentException("연락처가 필요합니다.");
    }

    // 피보험자 정보 수정
    public void updateInsuredPerson() {
        if (name == null || name.isEmpty())
            throw new IllegalStateException("수정할 피보험자 이름이 없습니다.");
    }

    // 계좌 정보 검증
    public void verifyAccountInfo() {
        if (accountInfo == null)
            throw new IllegalStateException("계좌 정보가 등록되지 않았습니다.");
    }

    public Account getAccountInfo()                         { return accountInfo; }
    public void    setAccountInfo(Account v)                { this.accountInfo = v; }
    public String  getContact()                             { return contact; }
    public void    setContact(String v)                     { this.contact = v; }
    public String  getName()                                { return name; }
    public void    setName(String v)                        { this.name = v; }
    public String  getResidentRegistrationNumber()          { return residentRegistrationNumber; }
    public void    setResidentRegistrationNumber(String v)  { this.residentRegistrationNumber = v; }

    @Override
    public String toString() {
        return "InsuredPerson{name='" + name + "', contact='" + contact + "'}";
    }
}
