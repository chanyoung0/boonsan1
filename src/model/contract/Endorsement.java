package model.contract;

import enums.ChangeReason;
import enums.EndorsementType;

import java.time.LocalDateTime;

// 배서 도메인 모델 — 계약 내용 변경 처리 정보 관리
public class Endorsement {

    private LocalDateTime appliedAt;
    private ChangeReason changeReason;
    private EndorsementType endorsementType;
    private String newContent;
    private String previousContent;
    private LocalDateTime processedAt;

    public Endorsement() {}

    public Endorsement(EndorsementType endorsementType, ChangeReason changeReason,
                       String previousContent, String newContent, LocalDateTime appliedAt) {
        this.endorsementType = endorsementType;
        this.changeReason = changeReason;
        this.previousContent = previousContent;
        this.newContent = newContent;
        this.appliedAt = appliedAt;
    }

    // 배서 적용
    public void applyEndorsement() {
        if (endorsementType == null)
            throw new IllegalStateException("배서 유형이 설정되지 않았습니다.");
        if (this.appliedAt == null) this.appliedAt = LocalDateTime.now();
    }

    // 배서 처리
    public void processEndorsement() {
        if (appliedAt == null)
            throw new IllegalStateException("배서 적용일이 없습니다.");
        this.processedAt = LocalDateTime.now();
    }

    // 변경 사항 검증
    public void verifyChanges() {
        if (previousContent == null || newContent == null)
            throw new IllegalStateException("변경 전/후 내용이 모두 필요합니다.");
        if (previousContent.equals(newContent))
            throw new IllegalStateException("변경 전과 변경 후 내용이 동일합니다.");
    }

    public LocalDateTime  getAppliedAt()                    { return appliedAt; }
    public void           setAppliedAt(LocalDateTime v)     { this.appliedAt = v; }
    public ChangeReason   getChangeReason()                 { return changeReason; }
    public void           setChangeReason(ChangeReason v)   { this.changeReason = v; }
    public EndorsementType getEndorsementType()             { return endorsementType; }
    public void           setEndorsementType(EndorsementType v){ this.endorsementType = v; }
    public String         getNewContent()                   { return newContent; }
    public void           setNewContent(String v)           { this.newContent = v; }
    public String         getPreviousContent()              { return previousContent; }
    public void           setPreviousContent(String v)      { this.previousContent = v; }
    public LocalDateTime  getProcessedAt()                  { return processedAt; }
    public void           setProcessedAt(LocalDateTime v)   { this.processedAt = v; }

    @Override
    public String toString() {
        return "Endorsement{endorsementType=" + endorsementType + ", changeReason=" + changeReason + "}";
    }
}
