package model.document;

import enums.DocumentName;
import enums.DocumentStatus;
import enums.DocumentType;
import enums.SubmissionStatus;

import java.time.LocalDateTime;

// 사고 접수 서류 도메인 모델 — 사고 신고 시 제출되는 부속 서류 관리
public class AccidentDocument extends Document {

    private DocumentName documentName;
    private DocumentType documentType;
    private SubmissionStatus submissionStatus;
    private LocalDateTime checkDueDate;

    public AccidentDocument() {}

    // 사고 서류 식별/제출 상태로 초기화
    public AccidentDocument(String documentId, LocalDateTime createdAt, DocumentStatus status,
                            DocumentName documentName, DocumentType documentType,
                            SubmissionStatus submissionStatus, LocalDateTime checkDueDate) {
        super(documentId, createdAt, status);
        this.documentName = documentName;
        this.documentType = documentType;
        this.submissionStatus = submissionStatus;
        this.checkDueDate = checkDueDate;
    }

    // 서류 업로드 처리
    public void uploadDocument() {}

    public DocumentName getDocumentName() { return documentName; }
    public DocumentType getDocumentType() { return documentType; }
    public SubmissionStatus getSubmissionStatus() { return submissionStatus; }
    public LocalDateTime getCheckDueDate() { return checkDueDate; }

    public void setDocumentName(DocumentName documentName) { this.documentName = documentName; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    public void setSubmissionStatus(SubmissionStatus submissionStatus) { this.submissionStatus = submissionStatus; }
    public void setCheckDueDate(LocalDateTime checkDueDate) { this.checkDueDate = checkDueDate; }

    @Override
    public String toString() {
        return "AccidentDocument{id='" + documentId + "', name=" + documentName
                + ", type=" + documentType + ", submission=" + submissionStatus + "}";
    }
}
