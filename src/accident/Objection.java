package accident;

import enums.AcceptanceStatus;
import enums.SurchargeCondition;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Objection {

    private AcceptanceStatus acceptanceStatus;
    private BigDecimal adjustedAmount;
    private String objectionId;
    private SurchargeCondition surchargeCond;
    private LocalDateTime underwritingDate;
    private String originalPaymentDetail;

    public void acceptObjection() {}

    public void receiveLegalResult() {}

    public void rejectObjection() {}

    public void transferToLegal() {}
}
