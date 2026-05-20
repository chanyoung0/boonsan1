package db;

import db.mapper.AuthorizationMapper;
import db.mybatis.MyBatisSessionFactory;
import model.insurance.Authorization;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// Authorization 엔티티 DB 매핑 — authorization_request 테이블 CRUD 담당 (MyBatis 위임)
public class AuthorizationDBO extends DBA {

    public Authorization findById(String requestId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AuthorizationMapper.class).findById(requestId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 상품 인가요청 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<Authorization> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AuthorizationMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 상품 인가요청 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(Authorization authorization) {
        return save(authorization, null);
    }

    public boolean save(Authorization authorization, String productCode) {
        if (authorization == null) {
            return false;
        }
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AuthorizationMapper.class)
                    .insert(authorization, productCode) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 상품 인가요청 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Authorization authorization) {
        if (authorization == null) {
            return false;
        }
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AuthorizationMapper.class)
                    .update(authorization, null, false) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 상품 인가요청 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Authorization authorization, String productCode) {
        if (authorization == null) {
            return false;
        }
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AuthorizationMapper.class)
                    .update(authorization, productCode, true) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 상품 인가요청 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String requestId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AuthorizationMapper.class).delete(requestId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 상품 인가요청 삭제 실패: " + e.getMessage());
            return false;
        }
    }
}
