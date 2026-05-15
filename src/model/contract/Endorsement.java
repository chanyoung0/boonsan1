package model.contract;

import enums.ChangeReason;
import enums.EndorsementType;
import model.underwriting.UnderwritingRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 배서 도메인 모델 — 계약 내용 변경 신청 및 처리 정보 관리
public class Endorsement {

    private LocalDateTime appliedAt;
    private ChangeReason changeReason;
    private EndorsementType endorsementType;
    private String newContent;
    private String previousContent;
    private LocalDateTime processedAt;
    private final List<UnderwritingRequest> underwritingRequests = new ArrayList<>();

    public Endorsement() {}

    // 배서 신청 기본 정보로 초기화
    public Endorsement(EndorsementType endorsementType, ChangeReason changeReason, String previousContent, String newContent) {
        this.endorsementType = endorsementType;
        this.changeReason = changeReason;
        this.previousContent = previousContent;
        this.newContent = newContent;
        this.appliedAt = LocalDateTime.now();
    }

    // 배서 신청
    public void applyEndorsement() {}

    // 배서 처리
    public void processEndorsement() {}

    // 변경 내용 검증
    public void verifyChanges() {}

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public ChangeReason getChangeReason() { return changeReason; }
    public EndorsementType getEndorsementType() { return endorsementType; }
    public String getNewContent() { return newContent; }
    public String getPreviousContent() { return previousContent; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public List<UnderwritingRequest> getUnderwritingRequests() { return underwritingRequests; }

    public void setAppliedAt(LocalDateTime t) { this.appliedAt = t; }
    public void setChangeReason(ChangeReason r) { this.changeReason = r; }
    public void setEndorsementType(EndorsementType t) { this.endorsementType = t; }
    public void setNewContent(String s) { this.newContent = s; }
    public void setPreviousContent(String s) { this.previousContent = s; }
    public void setProcessedAt(LocalDateTime t) { this.processedAt = t; }
    public void addUnderwritingRequest(UnderwritingRequest r) { this.underwritingRequests.add(r); }

    @Override
    public String toString() {
        return "Endorsement{type=" + endorsementType + ", reason=" + changeReason
                + ", appliedAt=" + appliedAt + "}";
    }
}
