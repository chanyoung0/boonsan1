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

    public void getInsuredPersonInfo()  {}
    public void registerInsuredPerson() {}
    public void updateInsuredPerson()   {}
    public void verifyAccountInfo()     {}

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
