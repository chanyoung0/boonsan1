package common;

// 도메인 객체 식별자 발급 추상 — 1단계는 UUID/Random, 추후 DB sequence로 교체
public interface IdGenerator {

    // 접두어 기반 식별자 발급 (예: nextId("APP") → "APP-2024-000123")
    String nextId(String prefix);
}
