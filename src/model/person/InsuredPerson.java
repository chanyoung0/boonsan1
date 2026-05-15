package model.person;

// 피보험자 도메인 모델 — 보장 대상 고객 정보 관리
public class InsuredPerson {

    private String name;
    private String residentRegistrationNumber;
    private String contact;
    private Account accountInfo;

    public InsuredPerson() {}

    // 피보험자 기본 정보로 초기화
    public InsuredPerson(String name, String residentRegistrationNumber, String contact, Account accountInfo) {
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.contact = contact;
        this.accountInfo = accountInfo;
    }

    // 피보험자 정보 등록
    public void registerInsuredPerson() {}

    // 피보험자 정보 수정
    public void updateInsuredPerson() {}

    // 계좌 정보 검증
    public void verifyAccountInfo() {}

    public String getName() { return name; }
    public String getResidentRegistrationNumber() { return residentRegistrationNumber; }
    public String getContact() { return contact; }
    public Account getAccountInfo() { return accountInfo; }

    public void setName(String name) { this.name = name; }
    public void setResidentRegistrationNumber(String rrn) { this.residentRegistrationNumber = rrn; }
    public void setContact(String contact) { this.contact = contact; }
    public void setAccountInfo(Account accountInfo) { this.accountInfo = accountInfo; }

    @Override
    public String toString() {
        return "InsuredPerson{name='" + name + "', rrn='" + residentRegistrationNumber + "', contact='" + contact + "'}";
    }
}
