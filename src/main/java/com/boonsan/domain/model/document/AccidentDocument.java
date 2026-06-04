package com.boonsan.domain.model.document;

import com.boonsan.domain.enums.DocumentName;
import com.boonsan.domain.enums.DocumentType;
import com.boonsan.domain.enums.SubmissionStatus;

import java.time.LocalDateTime;

public class AccidentDocument extends Document {

    private LocalDateTime checkDueDate;
    private DocumentName documentName;
    private DocumentType documentType;
    private SubmissionStatus submissionStatus;

    public void uploadDocument() {}
}
