package model.document;

import enums.DocumentStatus;

import java.time.LocalDateTime;

// 문서 추상 클래스 — 사고접수서류/지급품의서의 공통 속성 정의
public abstract class Document {

    protected LocalDateTime createdAt;
    protected String documentId;
    protected DocumentStatus status;

    protected Document() {}

    protected Document(String documentId, DocumentStatus status, LocalDateTime createdAt) {
        this.documentId = documentId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // 문서 저장
    public void save() {
        if (documentId == null || documentId.isEmpty())
            throw new IllegalStateException("문서 ID가 없습니다.");
        this.status = DocumentStatus.SUBMITTED;
    }

    // 임시 저장
    public void tempSave() {
        if (documentId == null || documentId.isEmpty())
            this.documentId = "DOC-" + System.currentTimeMillis();
        this.status = DocumentStatus.DRAFT;
        if (this.createdAt == null) this.createdAt = java.time.LocalDateTime.now();
    }

    public String         getDocumentId()              { return documentId; }
    public void           setDocumentId(String v)      { this.documentId = v; }
    public DocumentStatus getStatus()                  { return status; }
    public void           setStatus(DocumentStatus v)  { this.status = v; }
    public LocalDateTime  getCreatedAt()               { return createdAt; }
    public void           setCreatedAt(LocalDateTime v){ this.createdAt = v; }

    @Override
    public String toString() {
        return "Document{documentId='" + documentId + "', status=" + status + "}";
    }
}
