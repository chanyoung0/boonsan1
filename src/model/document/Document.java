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

    public void save()     {}
    public void tempSave() {}

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
