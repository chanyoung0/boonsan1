package model.accident;

import enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

// 외부 조사 의뢰 도메인 모델 — 손해조사 위탁 요청 정보 관리
public class OutsourceRequest {

    private String processEmployeeNo;
    private LocalDateTime requestDataTime;
    private String requestId;
    private RequestStatus requestStatus;
    private String result;
    private List<String> transferredDataList;

    // 위탁 결과 수신
    public void receiveResult() {}

    // 전송 데이터 선택
    public void selectDataToTransfer() {}

    // 협력업체 선택
    public void selectPartner() {}

    // 위탁 요청 전송
    public void send() {}
}
