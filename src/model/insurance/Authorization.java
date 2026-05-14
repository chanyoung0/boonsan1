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

    public void applyAuthorizationResult() {}

    public void cancelAuthorizationRequest() {}

    public void sendAuthorizationRequest() {}

    public void updateProductStatus() {}
}
