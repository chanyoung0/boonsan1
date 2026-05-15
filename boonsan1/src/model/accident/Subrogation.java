package model.accident;

import enums.SubrogationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Subrogation {

    private String depositAccount;
    private float faultRatio;
    private String offenderContact;
    private String offenderName;
    private LocalDateTime paymentDeadline;
    private BigDecimal subrogationAmount;
    private SubrogationStatus subrogationStatus;

    public void confirmDeposit() {}

    public void generateSubrogationDetails() {}

    public void retrieveSubrogationDetails() {}

    public void retrieveSums() {}
}
