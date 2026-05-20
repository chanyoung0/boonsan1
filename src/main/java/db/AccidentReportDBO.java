package db;

import db.mapper.AccidentReportMapper;
import db.mybatis.MyBatisSessionFactory;
import model.accident.AccidentReport;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// AccidentReport 엔티티 DB 매핑 — accident_report 테이블 CRUD 담당 (MyBatis 위임)
public class AccidentReportDBO extends DBA {

    public AccidentReport findByReportNo(String reportNo) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AccidentReportMapper.class).findByReportNo(reportNo);
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<AccidentReport> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AccidentReportMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String findPolicyNumberByReportNo(String reportNo) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AccidentReportMapper.class).findPolicyNumberByReportNo(reportNo);
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findStatusByReportNo(String reportNo) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            String value = session.getMapper(AccidentReportMapper.class).findStatusByReportNo(reportNo);
            return resolveAccidentStatus(value);
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findDocumentSubmissionStatusByReportNo(String reportNo) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            String value = session.getMapper(AccidentReportMapper.class)
                    .findDocumentSubmissionStatusByReportNo(reportNo);
            return resolveDocumentSubmissionStatus(value);
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 부가정보 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public boolean save(AccidentReport report) {
        throw new UnsupportedOperationException("policyNumber, accidentStatus, documentSubmissionStatus, accidentAtText 파라미터가 필요합니다.");
    }

    public boolean save(AccidentReport report, String policyNumber, String accidentStatus,
                        String documentSubmissionStatus, String accidentAtText) {
        if (report == null || report.getReportNo() == null || policyNumber == null) {
            return false;
        }
        String accidentStatusName = resolveAccidentStatus(accidentStatus);
        String documentStatusName = resolveDocumentSubmissionStatus(documentSubmissionStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AccidentReportMapper.class)
                    .insert(report, policyNumber, accidentStatusName, documentStatusName, accidentAtText) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(AccidentReport report) {
        throw new UnsupportedOperationException("policyNumber, accidentStatus, documentSubmissionStatus, accidentAtText 파라미터가 필요합니다.");
    }

    public boolean update(AccidentReport report, String policyNumber, String accidentStatus,
                          String documentSubmissionStatus, String accidentAtText) {
        if (report == null || report.getReportNo() == null || policyNumber == null) {
            return false;
        }
        String accidentStatusName = resolveAccidentStatus(accidentStatus);
        String documentStatusName = resolveDocumentSubmissionStatus(documentSubmissionStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AccidentReportMapper.class)
                    .update(report, policyNumber, accidentStatusName, documentStatusName, accidentAtText) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String reportNo) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(AccidentReportMapper.class).delete(reportNo) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 사고 접수 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private String resolveAccidentStatus(String value) {
        if ("DOCUMENT_PENDING".equals(value)
                || "REJECTED".equals(value)
                || "INVESTIGATION_REQUIRED".equals(value)) {
            return value;
        }
        return "RECEIVED";
    }

    private String resolveDocumentSubmissionStatus(String value) {
        if ("PENDING".equals(value)) {
            return value;
        }
        if ("SUBMITTED".equals(value)) {
            return value;
        }
        return null;
    }
}
