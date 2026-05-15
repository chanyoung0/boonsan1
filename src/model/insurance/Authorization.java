package model.insurance;

import java.time.LocalDateTime;

// 상품 인가 도메인 모델 — 금융감독원 인가 요청 및 결과 반영 정보 관리
public class Authorization {

    private LocalDateTime approvedAt;
    private boolean isApproved;
    private LocalDateTime requestedAt;
    private String requestId;
    private String requestReason;
    private String submissionAgencyName;

    private FinancialSupervisoryService financialSupervisoryService;

    public Authorization() {}

    public Authorization(String requestId, String requestReason, String submissionAgencyName,
                         LocalDateTime requestedAt, FinancialSupervisoryService financialSupervisoryService) {
        this.requestId = requestId;
        this.requestReason = requestReason;
        this.submissionAgencyName = submissionAgencyName;
        this.requestedAt = requestedAt;
        this.financialSupervisoryService = financialSupervisoryService;
    }

    // 인가 결과 반영 — 성공 여부 반환
    public boolean applyAuthorizationResult() { return isApproved; }

    // 인가 요청 취소 — 성공 여부 반환
    public boolean cancelAuthorizationRequest() { return true; }

    // 인가 요청 전송
    public void sendAuthorizationRequest() {}

    // 상품 상태 갱신
    public void updateProductStatus() {}

    public LocalDateTime                getApprovedAt()                          { return approvedAt; }
    public void                         setApprovedAt(LocalDateTime v)           { this.approvedAt = v; }
    public boolean                      isApproved()                             { return isApproved; }
    public void                         setApproved(boolean v)                   { this.isApproved = v; }
    public LocalDateTime                getRequestedAt()                         { return requestedAt; }
    public void                         setRequestedAt(LocalDateTime v)          { this.requestedAt = v; }
    public String                       getRequestId()                           { return requestId; }
    public void                         setRequestId(String v)                   { this.requestId = v; }
    public String                       getRequestReason()                       { return requestReason; }
    public void                         setRequestReason(String v)               { this.requestReason = v; }
    public String                       getSubmissionAgencyName()                { return submissionAgencyName; }
    public void                         setSubmissionAgencyName(String v)        { this.submissionAgencyName = v; }
    public FinancialSupervisoryService  getFinancialSupervisoryService()         { return financialSupervisoryService; }
    public void                         setFinancialSupervisoryService(FinancialSupervisoryService v) { this.financialSupervisoryService = v; }

    @Override
    public String toString() {
        return "Authorization{requestId='" + requestId + "', isApproved=" + isApproved + "}";
    }
}
