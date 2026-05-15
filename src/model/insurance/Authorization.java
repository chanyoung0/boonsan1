package model.insurance;

import java.time.LocalDateTime;

// 상품 인가 도메인 모델 — 금융감독원 인가 요청 및 결과 관리
public class Authorization {

    private String requestId;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private boolean isApproved;
    private String requestReason;
    private String submissionAgencyName;

    public Authorization() {}

    // 인가 요청 기본 정보로 초기화
    public Authorization(String requestId, LocalDateTime requestedAt, String requestReason, String submissionAgencyName) {
        this.requestId = requestId;
        this.requestedAt = requestedAt;
        this.requestReason = requestReason;
        this.submissionAgencyName = submissionAgencyName;
        this.isApproved = false;
    }

    // 인가 응답 결과 반영 — 승인 여부 반환
    public boolean applyAuthorizationResult() {
        return isApproved;
    }

    // 인가 요청 취소 — 취소 가능 여부 반환 (이미 승인된 경우 false)
    public boolean cancelAuthorizationRequest() {
        return !isApproved;
    }

    // 인가 요청 전송
    public void sendAuthorizationRequest() {}

    // 상품 상태 갱신
    public void updateProductStatus() {}

    public String getRequestId() { return requestId; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public boolean isApproved() { return isApproved; }
    public String getRequestReason() { return requestReason; }
    public String getSubmissionAgencyName() { return submissionAgencyName; }

    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public void setApproved(boolean approved) { this.isApproved = approved; }
    public void setRequestReason(String requestReason) { this.requestReason = requestReason; }
    public void setSubmissionAgencyName(String s) { this.submissionAgencyName = s; }

    @Override
    public String toString() {
        return "Authorization{requestId='" + requestId + "', approved=" + isApproved + ", agency='" + submissionAgencyName + "'}";
    }
}
