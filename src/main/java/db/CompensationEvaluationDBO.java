package db;

import db.mapper.CompensationEvaluationMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.CompensationStatus;
import model.contract.CompensationEvaluation;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// CompensationEvaluation 엔티티 DB 매핑 — compensation_evaluation 테이블 CRUD 담당 (MyBatis 위임)
public class CompensationEvaluationDBO extends DBA {

    public CompensationEvaluation findById(String evaluationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CompensationEvaluationMapper.class).findById(evaluationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<CompensationEvaluation> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CompensationEvaluationMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CompensationEvaluationMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 ID 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(CompensationEvaluation evaluation) {
        if (evaluation == null) {
            return false;
        }
        String statusName = resolveCompensationStatusName(evaluation);
        String resultName = resolveEvaluationResultName(evaluation);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CompensationEvaluationMapper.class)
                    .insert(evaluation, statusName, resultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(CompensationEvaluation evaluation) {
        if (evaluation == null) {
            return false;
        }
        String statusName = resolveCompensationStatusName(evaluation);
        String resultName = resolveEvaluationResultName(evaluation);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CompensationEvaluationMapper.class)
                    .update(evaluation, statusName, resultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String evaluationId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CompensationEvaluationMapper.class).delete(evaluationId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private String resolveCompensationStatusName(CompensationEvaluation evaluation) {
        if (evaluation == null || evaluation.getEvaluationStatus() == null) {
            return CompensationStatus.IN_PROGRESS.name();
        }
        return evaluation.getEvaluationStatus().name();
    }

    private String resolveEvaluationResultName(CompensationEvaluation evaluation) {
        if (evaluation == null || evaluation.getEvaluationResult() == null) {
            return null;
        }
        return evaluation.getEvaluationResult().name();
    }
}
