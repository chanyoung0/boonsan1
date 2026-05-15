package model.accident;

import enums.RequestStatus;
import model.partner.Partner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 외부 조사 의뢰 도메인 모델 — 손해조사 위탁 요청 정보 관리
public class OutsourceRequest {

    private String requestId;
    private LocalDateTime requestDateTime;
    private RequestStatus requestStatus;
    private String result;
    private final List<String> transferredDataList = new ArrayList<>();
    private Partner partner;

    public OutsourceRequest() {}

    // 위탁 요청 기본 정보로 초기화
    public OutsourceRequest(String requestId, LocalDateTime requestDateTime, Partner partner) {
        this.requestId = requestId;
        this.requestDateTime = requestDateTime;
        this.partner = partner;
        this.requestStatus = RequestStatus.PENDING;
    }

    // 위탁 결과 수신
    public void receiveResult() {}

    // 전송 데이터 선택
    public void selectDataToTransfer() {}

    // 협력업체 선택
    public void selectPartner() {}

    // 위탁 요청 전송
    public void send() {}

    public String getRequestId() { return requestId; }
    public LocalDateTime getRequestDateTime() { return requestDateTime; }
    public RequestStatus getRequestStatus() { return requestStatus; }
    public String getResult() { return result; }
    public List<String> getTransferredDataList() { return transferredDataList; }
    public Partner getPartner() { return partner; }

    public void setRequestId(String s) { this.requestId = s; }
    public void setRequestDateTime(LocalDateTime t) { this.requestDateTime = t; }
    public void setRequestStatus(RequestStatus s) { this.requestStatus = s; }
    public void setResult(String s) { this.result = s; }
    public void addTransferredData(String s) { this.transferredDataList.add(s); }
    public void setPartner(Partner p) { this.partner = p; }

    @Override
    public String toString() {
        return "OutsourceRequest{id='" + requestId + "', status=" + requestStatus
                + ", partner=" + (partner != null ? partner.getPartnerName() : null) + "}";
    }
}
