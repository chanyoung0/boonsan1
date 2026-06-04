package com.boonsan.model.document;

import com.boonsan.enums.DocumentStatus;

import java.time.LocalDateTime;

public abstract class Document {

    protected LocalDateTime createdAt;
    protected String documentId;
    protected DocumentStatus status;

    public void save() {}

    public void tempSave() {}
}
