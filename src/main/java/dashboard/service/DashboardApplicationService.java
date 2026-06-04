package dashboard.service;

import dashboard.dto.DashboardSummaryResponse;
import dashboard.mapper.DashboardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardApplicationService {

    private final DashboardMapper dashboardMapper;

    public DashboardApplicationService(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setUnderwritingInProgress(dashboardMapper.countUnderwritingInProgress());
        response.setPaymentApprovalPending(dashboardMapper.countPaymentApprovalPending());
        response.setSubrogationInProgress(dashboardMapper.countSubrogationInProgress());
        response.setObjectionReceived(dashboardMapper.countObjectionReceived());
        return response;
    }
}
