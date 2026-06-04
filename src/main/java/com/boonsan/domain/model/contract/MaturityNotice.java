package com.boonsan.domain.model.contract;

import com.boonsan.domain.enums.DeliveryMethod;

import java.time.LocalDateTime;

public class MaturityNotice {

    private LocalDateTime checkedAt;
    private DeliveryMethod deliveryMethod;
    private Boolean renewalIntention;
    private LocalDateTime sentAt;

    public void checkRenewalIntention() {}

    public void sendNotice() {}
}
