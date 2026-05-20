package db;

import db.mapper.ReinstatementMapper;
import db.mybatis.MyBatisSessionFactory;
import model.contract.Reinstatement;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// Reinstatement 엔티티 DB 매핑 — reinstatement 테이블 CRUD 담당 (MyBatis 위임)
public class ReinstatementDBO extends DBA {

    public Reinstatement findById(String reinstatementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class).findById(reinstatementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<Reinstatement> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(Reinstatement reinstatement) {
        throw new UnsupportedOperationException("reinstatementId, policyNumber, uwResult, reinstatementStatus 파라미터가 필요합니다.");
    }

    public boolean save(Reinstatement reinstatement, String reinstatementId, String policyNumber,
                        String uwResult, String reinstatementStatus) {
        if (reinstatement == null || reinstatementId == null || policyNumber == null) {
            return false;
        }
        String uwResultName = resolveUwResultName(uwResult);
        String statusName = resolveStatusName(reinstatementStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class)
                    .insert(reinstatement, reinstatementId, policyNumber, uwResultName, statusName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Reinstatement reinstatement) {
        throw new UnsupportedOperationException("reinstatementId, policyNumber, uwResult, reinstatementStatus 파라미터가 필요합니다.");
    }

    public boolean update(Reinstatement reinstatement, String reinstatementId, String policyNumber,
                          String uwResult, String reinstatementStatus) {
        if (reinstatement == null || reinstatementId == null || policyNumber == null) {
            return false;
        }
        String uwResultName = resolveUwResultName(uwResult);
        String statusName = resolveStatusName(reinstatementStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class)
                    .update(reinstatement, reinstatementId, policyNumber, uwResultName, statusName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String reinstatementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class).delete(reinstatementId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public String findStatusById(String reinstatementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class).findStatusById(reinstatementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findPolicyNumberById(String reinstatementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(ReinstatementMapper.class).findPolicyNumberById(reinstatementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 부활 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    private String resolveUwResultName(String uwResult) {
        if ("APPROVED".equals(uwResult) || "SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult)) {
            return uwResult;
        }
        return "APPROVED";
    }

    private String resolveStatusName(String status) {
        if ("APPROVED".equals(status) || "REJECTED".equals(status) || "PENDING".equals(status)) {
            return status;
        }
        return "PENDING";
    }
}
