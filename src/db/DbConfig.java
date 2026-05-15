package db;

// DB 접속 설정 — 실제 배포 시 환경변수(db.url, db.user, db.pass)로 재정의 가능
class DbConfig {
    static final String URL  = System.getProperty("db.url",  "jdbc:postgresql://localhost:5432/boonsan");
    static final String USER = System.getProperty("db.user", "admin");
    static final String PASS = System.getProperty("db.pass", "password");
}
