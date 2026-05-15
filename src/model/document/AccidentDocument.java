package model.document;

import enums.DocumentName;
import enums.DocumentStatus;
import enums.DocumentType;
import enums.SubmissionStatus;

import java.time.LocalDateTime;

// 사고 접수 서류 도메인 모델 — 사고 접수에 필요한 서류 정보 관리
public class AccidentDocument extends Document {

    private LocalDateTime checkDueDate;
    private DocumentName documentName;
    private DocumentType documentType;
    private SubmissionStatus submissionStatus;

    public AccidentDocument() { super(); }

    public AccidentDocument(String documentId, DocumentStatus status, LocalDateTime createdAt,
                            DocumentName documentName, DocumentType documentType,
                            SubmissionStatus submissionStatus, LocalDateTime checkDueDate) {
        super(documentId, status, createdAt);
        this.documentName = documentName;
        this.documentType = documentType;
        this.submissionStatus = submissionStatus;
        this.checkDueDate = checkDueDate;
    }

    public void uploadDocument() {}

    public LocalDateTime   getCheckDueDate()                    { return checkDueDate; }
    public void            setCheckDueDate(LocalDateTime v)     { this.checkDueDate = v; }
    public DocumentName    getDocumentName()                    { return documentName; }
    public void            setDocumentName(DocumentName v)      { this.documentName = v; }
    public DocumentType    getDocumentType()                    { return documentType; }
    public void            setDocumentType(DocumentType v)      { this.documentType = v; }
    public SubmissionStatus getSubmissionStatus()               { return submissionStatus; }
    public void            setSubmissionStatus(SubmissionStatus v){ this.submissionStatus = v; }

    @Override
    public String toString() {
        return "AccidentDocument{documentId='" + documentId + "', documentName=" + documentName + ", submissionStatus=" + submissionStatus + "}";
    }
}
