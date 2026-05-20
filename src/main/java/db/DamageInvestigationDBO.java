package db;

import db.mapper.DamageInvestigationMapper;
import db.mybatis.MyBatisSessionFactory;
import model.accident.DamageInvestigation;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// DamageInvestigation 엔티티 DB 매핑 — damage_investigation 테이블 CRUD 담당 (MyBatis 위임)
public class DamageInvestigationDBO extends DBA {

    public DamageInvestigation findById(String investigationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class).findById(investigationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<DamageInvestigation> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(DamageInvestigation investigation) {
        throw new UnsupportedOperationException("reportNo, investigationStatus 파라미터가 필요합니다.");
    }

    public boolean save(DamageInvestigation investigation, String reportNo, String investigationStatus) {
        if (investigation == null || investigation.getInvestigationId() == null || reportNo == null) {
            return false;
        }
        String statusName = resolveStatusName(investigationStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class)
                    .insert(investigation, reportNo, statusName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(DamageInvestigation investigation) {
        throw new UnsupportedOperationException("reportNo, investigationStatus 파라미터가 필요합니다.");
    }

    public boolean update(DamageInvestigation investigation, String reportNo, String investigationStatus) {
        if (investigation == null || investigation.getInvestigationId() == null || reportNo == null) {
            return false;
        }
        String statusName = resolveStatusName(investigationStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class)
                    .update(investigation, reportNo, statusName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String investigationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class).delete(investigationId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public String findStatusById(String investigationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class).findStatusById(investigationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findReportNoById(String investigationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(DamageInvestigationMapper.class).findReportNoById(investigationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 손해조사 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    private String resolveStatusName(String status) {
        if ("PENDING".equals(status) || "APPROVED".equals(status) || "REJECTED".equals(status)) {
            return status;
        }
        return "PENDING";
    }
}
