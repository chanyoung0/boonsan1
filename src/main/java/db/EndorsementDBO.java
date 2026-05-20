package db;

import db.mapper.EndorsementMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.EndorsementType;
import model.contract.Endorsement;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// Endorsement 엔티티 DB 매핑 — endorsement 테이블 CRUD 담당 (MyBatis 위임)
public class EndorsementDBO extends DBA {

    public Endorsement findById(String endorsementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class).findById(endorsementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<Endorsement> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(Endorsement endorsement) {
        throw new UnsupportedOperationException("endorsementId, policyNumber, endorsementTypeChoice, changeReason, uwResult 파라미터가 필요합니다.");
    }

    public boolean save(Endorsement endorsement, String endorsementId, String policyNumber,
                        String endorsementTypeChoice, String changeReason, String uwResult) {
        if (endorsement == null || endorsementId == null || policyNumber == null) {
            return false;
        }
        String endorsementType = resolveEndorsementTypeChoice(endorsementTypeChoice);
        String uwResultName = resolveUwResultName(uwResult);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class)
                    .insert(endorsement, endorsementId, policyNumber,
                            endorsementType, changeReason, uwResultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Endorsement endorsement) {
        throw new UnsupportedOperationException("endorsementId, policyNumber, endorsementTypeChoice, changeReason, uwResult 파라미터가 필요합니다.");
    }

    public boolean update(Endorsement endorsement, String endorsementId, String policyNumber,
                          String endorsementTypeChoice, String changeReason, String uwResult) {
        if (endorsement == null || endorsementId == null || policyNumber == null) {
            return false;
        }
        String endorsementType = resolveEndorsementTypeChoice(endorsementTypeChoice);
        String uwResultName = resolveUwResultName(uwResult);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class)
                    .update(endorsement, endorsementId, policyNumber,
                            endorsementType, changeReason, uwResultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String endorsementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class).delete(endorsementId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public String findPolicyNumberById(String endorsementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class).findPolicyNumberById(endorsementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findEndorsementTypeById(String endorsementId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(EndorsementMapper.class).findEndorsementTypeById(endorsementId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 배서 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    private String resolveEndorsementTypeChoice(String choice) {
        switch (choice == null ? "" : choice) {
            case "1": return EndorsementType.COVERAGE_CHANGE.name();
            case "2": return EndorsementType.PREMIUM_CHANGE.name();
            case "3": return EndorsementType.SPECIAL_CONTRACT_CHANGE.name();
            case "4": return EndorsementType.SPECIAL_CONTRACT_CHANGE.name();
            default:  return choice;
        }
    }

    private String resolveUwResultName(String uwResult) {
        if (uwResult == null) return null;
        if (uwResult.startsWith("할증")) return "SURCHARGE";
        if (uwResult.startsWith("거절")) return "REJECTED";
        if (uwResult.startsWith("승인")) return "APPROVED";
        if ("SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult) || "APPROVED".equals(uwResult)) {
            return uwResult;
        }
        return null;
    }
}
