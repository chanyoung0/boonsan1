package db;

// DB 접근 기반 클래스 — DB 연결 및 SQL 실행의 모든 공통 로직 담당
public class DBA {

    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/boonsan";
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "password";

    // DB 연결
    protected void connect() {
        System.out.println("[DB] 연결 중... (" + DB_URL + ")");
    }

    // DB 연결 종료
    protected void disconnect() {
        System.out.println("[DB] 연결 종료.");
    }

    // DB 로그인
    protected void login(String user, String password) {
        System.out.println("[DB] 로그인: " + user);
    }

    // SELECT 실행
    protected void executeSelect(String sql) {
        System.out.println("[DB] SELECT: " + sql);
    }

    // INSERT 실행
    protected void executeInsert(String sql) {
        System.out.println("[DB] INSERT: " + sql);
    }

    // UPDATE 실행
    protected void executeUpdate(String sql) {
        System.out.println("[DB] UPDATE: " + sql);
    }

    // DELETE 실행
    protected void executeDelete(String sql) {
        System.out.println("[DB] DELETE: " + sql);
    }
}
