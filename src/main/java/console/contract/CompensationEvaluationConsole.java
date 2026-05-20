package console.contract;

import enums.EvaluationResult;
import model.contract.CompensationEvaluation;
import service.contract.CompensationEvaluationService;

import java.math.BigDecimal;
import java.util.List;

import static common.ConsoleUtil.*;

public class CompensationEvaluationConsole {

    public static void run() {
        while (true) {
            line();
            System.out.println("[보상평가 관리]");
            System.out.println("1. 보상평가자료 생성");
            System.out.println("2. 보상평가 등록");
            System.out.println("3. 보상평가 완료");
            System.out.println("4. 보상평가 종료");
            System.out.println("5. 보상평가 목록 조회");
            System.out.println("6. 이전 메뉴");
            line();
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    createEvaluationMaterial();
                    break;
                case "2":
                    registerEvaluation();
                    break;
                case "3":
                    completeEvaluation();
                    break;
                case "4":
                    closeEvaluation();
                    break;
                case "5":
                    printEvaluationList();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static void createEvaluationMaterial() {
        System.out.println("\n[유스케이스] 보상평가를 관리한다 - 보상평가자료 생성");
        String period = input("조회기간");
        String insuranceType = input("보험종목");
        String compensationType = input("보상유형");

        System.out.println("\n[시스템] 기간별 보상통계 현황");
        System.out.println(CompensationEvaluationService.createPeriodStatisticsReport(
                period, insuranceType, compensationType));

        enter();
        System.out.println("\n[시스템] 손해액 분석 결과");
        System.out.println(CompensationEvaluationService.createDamageAnalysisReport(insuranceType));

        enter();
        System.out.println("\n[시스템] 마감통계 산출 결과");
        System.out.println(CompensationEvaluationService.createClosingStatisticsReport(
                period, insuranceType, compensationType));

        enter();
        System.out.println("[시스템] 보상평가자료 생성 완료");
    }

    private static void registerEvaluation() {
        System.out.println("\n[유스케이스] 보상평가를 관리한다 - 등록");
        int evaluationMonth = inputInt("평가월");
        String submissionAgencyName = input("제출기관명");
        BigDecimal damageAmount = inputAmount("손해액");

        CompensationEvaluation evaluation = CompensationEvaluationService.createEvaluation(
                evaluationMonth,
                submissionAgencyName,
                damageAmount
        );
        if (evaluation == null) {
            System.out.println("[오류] 보상평가 등록에 실패했습니다. DB 연결 또는 입력값을 확인하세요.");
            return;
        }

        System.out.println("[시스템] 보상평가 등록 완료");
        System.out.println("  평가번호: " + evaluation.getEvaluationId());
        System.out.println("  분석결과: " + evaluation.analyzeDamageAmount());
        System.out.println("  통계: " + evaluation.getCompensationStatistics());
        System.out.println("  상태: " + evaluation.getEvaluationStatus());
    }

    private static void completeEvaluation() {
        System.out.println("\n[보상평가 완료]");
        printEvaluationList();
        String evaluationId = input("완료할 평가번호");
        EvaluationResult result = selectEvaluationResult();

        CompensationEvaluation evaluation = CompensationEvaluationService.completeEvaluation(evaluationId, result);
        if (evaluation == null) {
            System.out.println("[오류] 해당 평가번호를 찾을 수 없거나 완료할 수 없는 상태입니다.");
            return;
        }

        System.out.println("[시스템] 보상평가 완료 처리");
        System.out.println("  평가결과: " + evaluation.getEvaluationResult());
        System.out.println("  상태: " + evaluation.getEvaluationStatus());
    }

    private static void closeEvaluation() {
        System.out.println("\n[보상평가 종료]");
        printEvaluationList();
        String evaluationId = input("종료할 평가번호");

        CompensationEvaluation evaluation = CompensationEvaluationService.closeEvaluation(evaluationId);
        if (evaluation == null) {
            System.out.println("[오류] 해당 평가번호를 찾을 수 없습니다.");
            return;
        }

        System.out.println("[시스템] 보상평가 종료 처리");
        System.out.println("  상태: " + evaluation.getEvaluationStatus());
    }

    private static void printEvaluationList() {
        System.out.println("\n[보상평가 목록 조회]");
        List<CompensationEvaluation> evaluationList = CompensationEvaluationService.getEvaluationList();
        if (evaluationList.isEmpty()) {
            System.out.println("[시스템] 등록된 보상평가가 없습니다.");
            return;
        }

        for (CompensationEvaluation evaluation : evaluationList) {
            System.out.println("  평가번호: " + evaluation.getEvaluationId()
                    + " | 평가월: " + evaluation.getEvaluationMonth()
                    + " | 제출기관: " + evaluation.getSubmissionAgencyName()
                    + " | 손해액: " + formatMoney(evaluation.getDamageAmount())
                    + " | 평가결과: " + evaluation.getEvaluationResult()
                    + " | 상태: " + evaluation.getEvaluationStatus());
        }
    }

    private static EvaluationResult selectEvaluationResult() {
        while (true) {
            System.out.println("평가 결과: 1. EXCELLENT  2. GOOD  3. AVERAGE  4. POOR");
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": return EvaluationResult.EXCELLENT;
                case "2": return EvaluationResult.GOOD;
                case "3": return EvaluationResult.AVERAGE;
                case "4": return EvaluationResult.POOR;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static BigDecimal inputAmount(String label) {
        while (true) {
            String value = input(label + " (원)").replace(",", "").trim();
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("[오류] 숫자로 입력하세요.");
            }
        }
    }

    private static int inputInt(String label) {
        while (true) {
            String value = input(label).trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("[오류] 숫자로 입력하세요.");
            }
        }
    }

    private static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0원";
        }
        return formatAmount(amount.longValue());
    }
}
