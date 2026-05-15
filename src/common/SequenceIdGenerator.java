package common;

import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

/**
 * IdGenerator 인메모리 구현 — 접두어별 단조 증가 시퀀스로 식별자 발급.
 * TODO: Replace with JPA-backed implementation (DB sequence / IDENTITY) when DB is wired.
 */
public class SequenceIdGenerator implements IdGenerator {

    private final Clock clock;
    private final AtomicLong counter = new AtomicLong(1);

    // 시간 의존성을 주입받아 결정적 ID 생성 가능 (테스트/시드 데이터에 유리)
    public SequenceIdGenerator(Clock clock) {
        this.clock = clock;
    }

    @Override
    public String nextId(String prefix) {
        int year = LocalDate.now(clock).getYear();
        return String.format("%s-%d-%06d", prefix, year, counter.getAndIncrement());
    }
}
