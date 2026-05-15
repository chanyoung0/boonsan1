package enums;

// 재보험 회계 처리 상태 — 계상 → 청산 단계 표현
public enum AccountingStatus {
    PENDING,      // 계상 전
    RECOGNIZED,   // 출재보험료 계상 완료
    SETTLED       // 청산 완료
}
