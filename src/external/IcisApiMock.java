package external;

import java.util.Random;

// ICIS(신용정보원) API 외부 연동 Mock — 사고이력·타사계약 조회를 랜덤값으로 시뮬레이션
public class IcisApiMock {

    private static final Random rnd = new Random();

    // 사고이력 조회 — true: 이력 있음 (30% 확률)
    public static boolean hasAccidentHistory() {
        return rnd.nextInt(10) < 3;
    }

    // 타사계약 조회 — true: 계약 있음 (40% 확률)
    public static boolean hasOtherContract() {
        return rnd.nextInt(10) < 4;
    }

    // API 응답 가능 여부 — false: 응답 없음 (10% 확률)
    public static boolean isAvailable() {
        return rnd.nextInt(10) >= 1;
    }
}
