package model.underwriting;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 공동보험 도메인 모델 — 위험 분담을 위한 공동인수 처리 정보 관리
public class Coinsurance {

    private LocalDateTime receivedAt;
    private BigDecimal retainedAmount;
    private float retainedShareRate;

    private List<Coinsurer> coinsurerList;

    public Coinsurance() {
        this.coinsurerList = new ArrayList<>();
    }

    public Coinsurance(float retainedShareRate, BigDecimal retainedAmount, LocalDateTime receivedAt) {
        this.retainedShareRate = retainedShareRate;
        this.retainedAmount = retainedAmount;
        this.receivedAt = receivedAt;
        this.coinsurerList = new ArrayList<>();
    }

    // 보험료 할당
    public void allocatePremium()          {}

    // 공동인수 신청
    public void applyForCoinsurance()      {}

    // 보유액 계산
    public void calculateRetainedAmount()  {}

    // 공동인수사 승인 확인
    public void checkApproval()            {}

    // 출재 처리
    public void processCession()           {}

    // 참여 요청 전송
    public void sendParticipationRequest() {}

    public LocalDateTime  getReceivedAt()                    { return receivedAt; }
    public void           setReceivedAt(LocalDateTime v)     { this.receivedAt = v; }
    public BigDecimal     getRetainedAmount()                { return retainedAmount; }
    public void           setRetainedAmount(BigDecimal v)    { this.retainedAmount = v; }
    public float          getRetainedShareRate()             { return retainedShareRate; }
    public void           setRetainedShareRate(float v)      { this.retainedShareRate = v; }
    public List<Coinsurer> getCoinsurerList()                { return coinsurerList; }
    public void           setCoinsurerList(List<Coinsurer> v){ this.coinsurerList = v; }

    @Override
    public String toString() {
        return "Coinsurance{retainedShareRate=" + retainedShareRate + ", coinsurerList.size=" + coinsurerList.size() + "}";
    }
}
