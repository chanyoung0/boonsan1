package db;

import db.mapper.PartnerMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.EvaluationGrade;
import model.partner.Partner;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// Partner 엔티티 DB 매핑 — partner 테이블 CRUD 담당 (MyBatis 위임)
public class PartnerDBO extends DBA {

    public Partner findById(String partnerId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PartnerMapper.class).findById(partnerId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<Partner> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PartnerMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PartnerMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(Partner partner) {
        if (partner == null) {
            return false;
        }
        applyDefaultEvaluationGrade(partner);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PartnerMapper.class).insert(partner) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 등록 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Partner partner) {
        if (partner == null) {
            return false;
        }
        applyDefaultEvaluationGrade(partner);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PartnerMapper.class).update(partner) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String partnerId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PartnerMapper.class).delete(partnerId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 협력업체 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private void applyDefaultEvaluationGrade(Partner partner) {
        if (partner.getEvaluationGrade() == null) {
            partner.setEvaluationGrade(EvaluationGrade.AVERAGE);
        }
    }
}
