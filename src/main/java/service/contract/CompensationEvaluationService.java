package service.contract;

import db.CompensationEvaluationMapper;
import db.MyBatisSessionFactory;
import enums.CompensationStatus;
import enums.EvaluationResult;
import model.contract.CompensationEvaluation;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompensationEvaluationService {

    public static CompensationEvaluation createEvaluation(int evaluationMonth, String submissionAgencyName,
                                                          BigDecimal damageAmount) {
        String evaluationId = generateEvaluationId();
        CompensationEvaluation evaluation = new CompensationEvaluation(
                evaluationId,
                evaluationMonth,
                CompensationStatus.IN_PROGRESS,
                null,
                submissionAgencyName
        );
        evaluation.setDamageAmount(damageAmount);
        evaluation.setDamageAnalysisResult(analyzeDamageAmount(damageAmount));
        evaluation.setCompensationStatistics(
                createCompensationStatistics(evaluationMonth, submissionAgencyName, damageAmount)
        );
        evaluation.calculateCompensationStatistics();
        return saveEvaluation(evaluation) ? evaluation : null;
    }

    public static String analyzeDamageAmount(BigDecimal damageAmount) {
        if (damageAmount == null) {
            return "손해액 정보가 없어 분석을 보류합니다.";
        }
        if (damageAmount.compareTo(BigDecimal.valueOf(1_000_000L)) < 0) {
            return "소액 손해 구간입니다.";
        }
        if (damageAmount.compareTo(BigDecimal.valueOf(10_000_000L)) < 0) {
            return "일반 손해 구간입니다.";
        }
        return "고액 손해 구간입니다.";
    }

    public static String createCompensationStatistics(int evaluationMonth, String submissionAgencyName,
                                                      BigDecimal damageAmount) {
        return "평가월: " + evaluationMonth
                + " | 제출기관: " + submissionAgencyName
                + " | 손해액: " + damageAmount;
    }

    public static String createPeriodStatisticsReport(String period, String insuranceType,
                                                      String compensationType) {
        List<CompensationEvaluation> evaluationList = getEvaluationList();
        int receivedCount = evaluationList.size();
        int processedCount = 0;
        for (CompensationEvaluation evaluation : evaluationList) {
            if (evaluation.getEvaluationStatus() == CompensationStatus.COMPLETED
                    || evaluation.getEvaluationStatus() == CompensationStatus.CLOSED) {
                processedCount++;
            }
        }
        int pendingCount = receivedCount - processedCount;

        return "조회기간: " + period
                + " | 보험종목: " + insuranceType
                + " | 보상유형: " + compensationType
                + "\n  접수건수: " + receivedCount
                + "\n  처리건수: " + processedCount
                + "\n  미결건수: " + pendingCount
                + "\n  지급보험금 합계: 산출 기준 없음 (보험금 지급 데이터 미연결)"
                + "\n  평균처리일수: 산출 기준 없음";
    }

    public static String createDamageAnalysisReport(String insuranceType) {
        List<CompensationEvaluation> evaluationList = getEvaluationList();
        BigDecimal totalDamageAmount = calculateTotalDamageAmount(evaluationList);
        return "보험종목: " + insuranceType
                + "\n  손해액 합계: " + totalDamageAmount
                + "\n  손해액 분석: " + analyzeDamageAmount(totalDamageAmount)
                + "\n  손해율: 산출 기준 없음 (수입보험료 데이터 없음)"
                + "\n  전월 대비 증감: 산출 기준 없음"
                + "\n  손해액 추이: 현재 등록된 평가 목록 " + evaluationList.size() + "건 기준";
    }

    public static String createClosingStatisticsReport(String period, String insuranceType,
                                                       String compensationType) {
        List<CompensationEvaluation> evaluationList = getEvaluationList();
        return "조회기간: " + period
                + " | 보험종목: " + insuranceType
                + " | 보상유형: " + compensationType
                + "\n  월별 접수현황: 현재 등록된 평가 목록 " + evaluationList.size() + "건 기준"
                + "\n  처리현황: " + countByStatus(evaluationList, CompensationStatus.COMPLETED) + "건 완료, "
                + countByStatus(evaluationList, CompensationStatus.CLOSED) + "건 종료"
                + "\n  지급현황: 산출 기준 없음 (보험금 지급 데이터 미연결)"
                + "\n  손해액 합계: " + calculateTotalDamageAmount(evaluationList)
                + "\n  손해율: 산출 기준 없음 (수입보험료 데이터 없음)";
    }

    public static CompensationEvaluation completeEvaluation(String evaluationId, EvaluationResult result) {
        CompensationEvaluation evaluation = findEvaluationById(evaluationId);
        if (evaluation == null || evaluation.getEvaluationStatus() == CompensationStatus.CLOSED) {
            return null;
        }
        evaluation.setEvaluationResult(result);
        evaluation.saveEvaluationResult();
        return updateEvaluation(evaluation) ? evaluation : null;
    }

    public static CompensationEvaluation closeEvaluation(String evaluationId) {
        CompensationEvaluation evaluation = findEvaluationById(evaluationId);
        if (evaluation == null) {
            return null;
        }
        evaluation.setEvaluationStatus(CompensationStatus.CLOSED);
        return updateEvaluation(evaluation) ? evaluation : null;
    }

    public static List<CompensationEvaluation> getEvaluationList() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(CompensationEvaluationMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static CompensationEvaluation findEvaluationById(String evaluationId) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(CompensationEvaluationMapper.class).findById(evaluationId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 조회 실패: " + e.getMessage());
            return null;
        }
    }

    private static boolean saveEvaluation(CompensationEvaluation evaluation) {
        if (evaluation == null) {
            return false;
        }
        String statusName = resolveCompensationStatusName(evaluation);
        String resultName = resolveEvaluationResultName(evaluation);
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(CompensationEvaluationMapper.class)
                    .insert(evaluation, statusName, resultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 저장 실패: " + e.getMessage());
            return false;
        }
    }

    private static boolean updateEvaluation(CompensationEvaluation evaluation) {
        if (evaluation == null) {
            return false;
        }
        String statusName = resolveCompensationStatusName(evaluation);
        String resultName = resolveEvaluationResultName(evaluation);
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(CompensationEvaluationMapper.class)
                    .update(evaluation, statusName, resultName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보상평가 수정 실패: " + e.getMessage());
            return false;
        }
    }

    private static BigDecimal calculateTotalDamageAmount(List<CompensationEvaluation> evaluationList) {
        BigDecimal total = BigDecimal.ZERO;
        for (CompensationEvaluation evaluation : evaluationList) {
            if (evaluation.getDamageAmount() != null) {
                total = total.add(evaluation.getDamageAmount());
            }
        }
        return total;
    }

    private static int countByStatus(List<CompensationEvaluation> evaluationList,
                                     CompensationStatus status) {
        int count = 0;
        for (CompensationEvaluation evaluation : evaluationList) {
            if (evaluation.getEvaluationStatus() == status) {
                count++;
            }
        }
        return count;
    }

    private static String generateEvaluationId() {
        return "CE-" + System.currentTimeMillis();
    }

    private static String resolveCompensationStatusName(CompensationEvaluation evaluation) {
        if (evaluation == null || evaluation.getEvaluationStatus() == null) {
            return CompensationStatus.IN_PROGRESS.name();
        }
        return evaluation.getEvaluationStatus().name();
    }

    private static String resolveEvaluationResultName(CompensationEvaluation evaluation) {
        if (evaluation == null || evaluation.getEvaluationResult() == null) {
            return null;
        }
        return evaluation.getEvaluationResult().name();
    }
}
