package external;

import java.util.Random;

// 재보험사 시스템 외부 연동 Mock — 재보험 요청/응답을 랜덤값으로 시뮬레이션
public class ReinsuranceSystemMock {

    private static final Random rnd = new Random();

    // 재보험사 응답 가능 여부 — false: 응답 없음 (10% 확률)
    public static boolean isAvailable() {
        return rnd.nextInt(10) >= 1;
    }

    // 재보험 인수 가능 여부 — true: 인수 가능
    public static boolean canAccept() {
        return rnd.nextInt(10) >= 1;
    }
}
