package com.boonsan.domain.model.underwriting;

import com.boonsan.domain.enums.RejectionReason;
import com.boonsan.domain.enums.RequestReason;
import com.boonsan.domain.enums.RequestStatus;
import com.boonsan.domain.enums.SurchargeCondition;
import com.boonsan.domain.enums.UnderwritingResultType;
import com.boonsan.domain.enums.UnderwritingType;

import java.time.LocalDateTime;

// 언더라이팅 요청 도메인 모델 — 배서/부활 심사 요청 정보 관리
public class UnderwritingRequest {

    private LocalDateTime appliedAt;
    private LocalDateTime appliedId;
    private RejectionReason rejectionReason;
    private RequestReason requestReason;
    private RequestStatus requestStatus;
    private SurchargeCondition surchargeCondition;
    private UnderwritingResultType underwritingResult;
    private UnderwritingType underwritingType;

    // U/W 요청 상태 변경
    public void changeUWStatus() {}

    // U/W 결과 등록
    public void registerUWResult() {}

    // 언더라이팅 요청
    public void requestUnderwriting() {}
}
