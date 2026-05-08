package model.accident;

import enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OutsourceRequest {

    private String processEmployeeNo;
    private LocalDateTime requestDataTime;
    private String requestId;
    private RequestStatus requestStatus;
    private String result;
    private List<String> transferredDataList;

    public void receive() {}

    public void selectDataToTransfer() {}

    public void selectPartner() {}

    public void send() {}
}
