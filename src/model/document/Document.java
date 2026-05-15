package model.document;

import enums.DocumentStatus;

import java.time.LocalDateTime;

// 문서 추상 도메인 모델 — 사고/지급 등 부속 문서의 공통 속성 정의
public abstract class Document {

    protected String documentId;
    protected LocalDateTime createdAt;
    protected DocumentStatus status;

    public Document() {}

    // 문서 공통 필드로 초기화
    public Document(String documentId, LocalDateTime createdAt, DocumentStatus status) {
        this.documentId = documentId;
        this.createdAt = createdAt;
        this.status = status;
    }

    // 문서 저장
    public void save() {}

    // 문서 임시 저장
    public void tempSave() {}

    public String getDocumentId() { return documentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public DocumentStatus getStatus() { return status; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStatus(DocumentStatus status) { this.status = status; }
}
