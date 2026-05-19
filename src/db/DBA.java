package db;

import java.util.Arrays;

// DB 접근 기반 클래스 — DB 연결 및 SQL 실행의 모든 공통 로직 담당
public class DBA {

    // DB 연결
    protected void connect() {
        System.out.println("[DB] 연결 중... (" + DbConfig.URL + ")");
    }

    // DB 연결 종료
    protected void disconnect() {
        System.out.println("[DB] 연결 종료.");
    }

    // DB 로그인
    protected void login(String user, String password) {
        System.out.println("[DB] 로그인: " + user);
    }

    // SELECT 실행 (파라미터 바인딩)
    protected void executeSelect(String sql, Object... params) {
        System.out.println("[DB] SELECT: " + sql + formatParams(params));
    }

    // INSERT 실행 (파라미터 바인딩)
    protected void executeInsert(String sql, Object... params) {
        System.out.println("[DB] INSERT: " + sql + formatParams(params));
    }

    // UPDATE 실행 (파라미터 바인딩)
    protected void executeUpdate(String sql, Object... params) {
        System.out.println("[DB] UPDATE: " + sql + formatParams(params));
    }

    // DELETE 실행 (파라미터 바인딩)
    protected void executeDelete(String sql, Object... params) {
        System.out.println("[DB] DELETE: " + sql + formatParams(params));
    }

    private static String formatParams(Object[] params) {
        if (params == null || params.length == 0) return "";
        return " | params=" + Arrays.toString(params);
    }
}
