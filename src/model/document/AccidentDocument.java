package model.document;

import enums.DocumentName;
import enums.DocumentType;
import enums.SubmissionStatus;

import java.time.LocalDateTime;

public class AccidentDocument extends Document {

    private LocalDateTime checkDueDate;
    private DocumentName documentName;
    private DocumentType documentType;
    private SubmissionStatus submissionStatus;

    public void uploadDocument() {}
}
