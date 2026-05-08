package external;

import java.util.Random;

// 은행 API 외부 연동 Mock — 계좌이체 처리를 랜덤값으로 시뮬레이션
public class BankApiMock {

    private static final Random rnd = new Random();

    // 계좌이체 처리 — true: 이체 성공 (95% 확률)
    public static boolean transfer(String accountNo, long amount) {
        return rnd.nextInt(100) >= 5;
    }

    // 계좌 유효성 확인 — true: 유효한 계좌
    public static boolean validateAccount(String accountNo) {
        return rnd.nextInt(10) >= 1;
    }
}
