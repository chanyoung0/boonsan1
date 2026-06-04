package com.boonsan.model.document;

import com.boonsan.enums.DocumentName;
import com.boonsan.enums.DocumentType;
import com.boonsan.enums.SubmissionStatus;

import java.time.LocalDateTime;

public class AccidentDocument extends Document {

    private LocalDateTime checkDueDate;
    private DocumentName documentName;
    private DocumentType documentType;
    private SubmissionStatus submissionStatus;

    public void uploadDocument() {}
}
