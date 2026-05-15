package model.insurance;

// 금융감독원 도메인 모델 — 보험 상품 인가 요청 및 결과 수신 처리
public class FinancialSupervisoryService {

    private String institutionCode;
    private String institutionName;

    public FinancialSupervisoryService() {}

    public FinancialSupervisoryService(String institutionCode, String institutionName) {
        this.institutionCode = institutionCode;
        this.institutionName = institutionName;
    }

    // 인가 요청 수신
    public void receiveAuthorizationRequest() {}

    // 인가 결과 전송
    public void sendAuthorizationResult() {}

    public String getInstitutionCode()             { return institutionCode; }
    public void   setInstitutionCode(String v)     { this.institutionCode = v; }
    public String getInstitutionName()             { return institutionName; }
    public void   setInstitutionName(String v)     { this.institutionName = v; }

    @Override
    public String toString() {
        return "FinancialSupervisoryService{institutionCode='" + institutionCode + "', institutionName='" + institutionName + "'}";
    }
}
