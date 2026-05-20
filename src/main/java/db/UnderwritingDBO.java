package db;

import db.mapper.UnderwritingMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.UnderwritingStatus;
import enums.UnderwritingType;
import model.underwriting.Underwriting;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// Underwriting 엔티티 DB 매핑 — underwriting 테이블 CRUD 담당 (MyBatis 위임)
public class UnderwritingDBO extends DBA {

    public Underwriting findById(String underwritingId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class).findById(underwritingId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<Underwriting> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(Underwriting underwriting) {
        throw new UnsupportedOperationException("underwritingId, empNo, empName, empDept, uwResult 파라미터가 필요합니다.");
    }

    public boolean save(Underwriting underwriting, String underwritingId, String empNo,
                        String empName, String empDept, String uwResult) {
        if (underwriting == null || underwritingId == null) {
            return false;
        }
        applyDefaults(underwriting);
        String resultName = resolveResultName(uwResult);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class)
                    .insert(underwriting, underwritingId, empNo, empName, empDept, resultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Underwriting underwriting) {
        throw new UnsupportedOperationException("underwritingId, empNo, empName, empDept, uwResult 파라미터가 필요합니다.");
    }

    public boolean update(Underwriting underwriting, String underwritingId, String empNo,
                          String empName, String empDept, String uwResult) {
        if (underwriting == null || underwritingId == null) {
            return false;
        }
        applyDefaults(underwriting);
        String resultName = resolveResultName(uwResult);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class)
                    .update(underwriting, underwritingId, empNo, empName, empDept, resultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String underwritingId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class).delete(underwritingId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public String findStatusById(String underwritingId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class).findStatusById(underwritingId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findResultById(String underwritingId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(UnderwritingMapper.class).findResultById(underwritingId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 심사 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    private void applyDefaults(Underwriting underwriting) {
        if (underwriting.getUnderwritingStatus() == null) {
            underwriting.setUnderwritingStatus(UnderwritingStatus.COMPLETED);
        }
        if (underwriting.getUnderwritingType() == null) {
            underwriting.setUnderwritingType(UnderwritingType.AUTO);
        }
    }

    private String resolveResultName(String uwResult) {
        if ("APPROVED".equals(uwResult) || "SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult)) {
            return uwResult;
        }
        return "APPROVED";
    }
}
