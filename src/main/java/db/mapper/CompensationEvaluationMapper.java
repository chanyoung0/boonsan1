package db.mapper;

import model.contract.CompensationEvaluation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 보상평가 MyBatis Mapper — CompensationEvaluationDBO가 위임하는 SQL 인터페이스
public interface CompensationEvaluationMapper {

    CompensationEvaluation findById(String evaluationId);

    List<CompensationEvaluation> findAll();

    List<String> findAllIds();

    int insert(@Param("ce") CompensationEvaluation evaluation,
               @Param("evaluationStatus") String evaluationStatus,
               @Param("evaluationResult") String evaluationResult);

    int update(@Param("ce") CompensationEvaluation evaluation,
               @Param("evaluationStatus") String evaluationStatus,
               @Param("evaluationResult") String evaluationResult);

    int delete(String evaluationId);
}
