package model.accident;

import enums.RequestStatus;
import model.partner.Partner;

import java.time.LocalDateTime;
import java.util.List;

// 외부 조사 의뢰 도메인 모델 — 손해조사 위탁 요청 정보 관리
public class OutsourceRequest {

    private LocalDateTime requestDateTime;
    private String requestId;
    private RequestStatus requestStatus;
    private String result;
    private List<String> transferredDataList;

    private Partner partner;

    public OutsourceRequest() {}

    public OutsourceRequest(String requestId, RequestStatus requestStatus,
                            LocalDateTime requestDateTime, Partner partner) {
        this.requestId = requestId;
        this.requestStatus = requestStatus;
        this.requestDateTime = requestDateTime;
        this.partner = partner;
    }

    // 위탁 결과 수신
    public void receiveResult()        {}

    // 전송 데이터 선택
    public void selectDataToTransfer() {}

    // 협력업체 선택
    public void selectPartner()        {}

    // 위탁 요청 전송
    public void send()                 {}

    public LocalDateTime  getRequestDateTime()                  { return requestDateTime; }
    public void           setRequestDateTime(LocalDateTime v)   { this.requestDateTime = v; }
    public String         getRequestId()                        { return requestId; }
    public void           setRequestId(String v)                { this.requestId = v; }
    public RequestStatus  getRequestStatus()                    { return requestStatus; }
    public void           setRequestStatus(RequestStatus v)     { this.requestStatus = v; }
    public String         getResult()                           { return result; }
    public void           setResult(String v)                   { this.result = v; }
    public List<String>   getTransferredDataList()              { return transferredDataList; }
    public void           setTransferredDataList(List<String> v){ this.transferredDataList = v; }
    public Partner        getPartner()                          { return partner; }
    public void           setPartner(Partner v)                 { this.partner = v; }

    @Override
    public String toString() {
        return "OutsourceRequest{requestId='" + requestId + "', requestStatus=" + requestStatus + "}";
    }
}
