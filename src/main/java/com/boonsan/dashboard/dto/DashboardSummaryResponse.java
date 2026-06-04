package com.boonsan.dashboard.dto;

public class DashboardSummaryResponse {

    private int underwritingInProgress;
    private int paymentApprovalPending;
    private int subrogationInProgress;
    private int objectionReceived;

    public int getUnderwritingInProgress() {
        return underwritingInProgress;
    }

    public void setUnderwritingInProgress(int underwritingInProgress) {
        this.underwritingInProgress = underwritingInProgress;
    }

    public int getPaymentApprovalPending() {
        return paymentApprovalPending;
    }

    public void setPaymentApprovalPending(int paymentApprovalPending) {
        this.paymentApprovalPending = paymentApprovalPending;
    }

    public int getSubrogationInProgress() {
        return subrogationInProgress;
    }

    public void setSubrogationInProgress(int subrogationInProgress) {
        this.subrogationInProgress = subrogationInProgress;
    }

    public int getObjectionReceived() {
        return objectionReceived;
    }

    public void setObjectionReceived(int objectionReceived) {
        this.objectionReceived = objectionReceived;
    }
}
