package external;

import java.util.Random;

// 공동인수사 시스템 외부 연동 Mock — 공동인수 참여 요청/승인을 랜덤값으로 시뮬레이션
public class CoinsuranceSystemMock {

    private static final Random rnd = new Random();

    // 시스템 연결 가능 여부 — false: 연결 실패 (10% 확률)
    public static boolean isConnected() {
        return rnd.nextInt(10) >= 1;
    }

    // 공동인수사 참여 승인 여부 — false: 거절 (20% 확률)
    public static boolean isApproved() {
        return rnd.nextInt(100) >= 20;
    }
}
