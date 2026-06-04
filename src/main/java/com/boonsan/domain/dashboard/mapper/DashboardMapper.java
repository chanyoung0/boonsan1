package com.boonsan.domain.dashboard.mapper;

public interface DashboardMapper {

    int countUnderwritingInProgress();

    int countPaymentApprovalPending();

    int countSubrogationInProgress();

    int countObjectionReceived();
}
