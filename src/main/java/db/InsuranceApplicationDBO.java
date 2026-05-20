package db;

import db.mapper.InsuranceApplicationMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.ApplicationStatus;
import model.underwriting.InsuranceApplication;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// InsuranceApplication 엔티티 DB 매핑 — insurance_application 테이블 CRUD 담당 (MyBatis 위임)
public class InsuranceApplicationDBO extends DBA {

    public InsuranceApplication findById(String applicationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceApplicationMapper.class).findById(applicationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 청약 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<InsuranceApplication> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceApplicationMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 청약 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(InsuranceApplication application) {
        throw new UnsupportedOperationException("policyNumber, applicationStatus, appliedCondition 파라미터가 필요합니다.");
    }

    public boolean save(InsuranceApplication application, String policyNumber,
                        String applicationStatus, String appliedCondition) {
        if (application == null || application.getApplicationId() == null) {
            return false;
        }
        String statusName = resolveStatusName(applicationStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceApplicationMapper.class)
                    .insert(application, policyNumber, statusName, appliedCondition) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 청약 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(InsuranceApplication application) {
        throw new UnsupportedOperationException("policyNumber, applicationStatus 파라미터가 필요합니다.");
    }

    public boolean update(InsuranceApplication application, String policyNumber, String applicationStatus) {
        if (application == null || application.getApplicationId() == null) {
            return false;
        }
        String statusName = resolveStatusName(applicationStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceApplicationMapper.class)
                    .update(application, policyNumber, statusName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 청약 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String applicationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceApplicationMapper.class).delete(applicationId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 청약 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public String findStatusById(String applicationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceApplicationMapper.class).findStatusById(applicationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 청약 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findPolicyNumberById(String applicationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceApplicationMapper.class).findPolicyNumberById(applicationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 청약 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    private String resolveStatusName(String status) {
        if ("PENDING".equals(status) || "APPROVED".equals(status)
                || "REJECTED".equals(status) || "CANCELLED".equals(status)) {
            return status;
        }
        return ApplicationStatus.PENDING.name();
    }
}
