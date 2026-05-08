package model.document;

import enums.DocumentType;
import enums.SubmissionStatus;

import java.time.LocalDateTime;

public class AccidentDocument extends Document {

    private DocumentType documentType;
    private SubmissionStatus submissionStatus;
    private LocalDateTime submitAt;

    public void uploadDocument() {}
}
