package console.compensation;

import service.compensation.CompensationEvaluationService;

import static common.ConsoleUtil.*;

// 보상 평가 관리 콘솔 I/O — 보상담당자 유스케이스 입출력 전담
public class CompensationEvaluationConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 보상 평가를 관리한다");
        System.out.println("액터: 보상담당자");
        line();

        System.out.println("\n[보상담당자] '보상평가 관리' 메뉴를 선택합니다.");
        enter();

        System.out.println("[시스템] 보상평가 조회 조건 입력 화면");
        String period        = input("조회기간 (예: 2024-01)");
        String insuranceType = input("보험종목 (예: 자동차, 화재, 전체)");
        input("보상유형 (예: 대물, 대인, 전체)");

        System.out.println("\n[시스템] 기간별 보상통계 현황 (" + period + "):");
        System.out.println("  접수건수: 324건 | 처리건수: 298건 | 미결건수: 26건");
        System.out.println("  지급보험금 합계: 1,250,000,000원 | 평균처리일수: 4.2일");

        System.out.println("\n[보상담당자] '손해액 분석' 버튼을 누릅니다.");
        enter();
        System.out.println("[시스템] 손해액 분석 결과:");
        System.out.println("  보험종목별 손해액: " + insuranceType + " 125,000,000원");
        System.out.println("  손해율: 62.5% | 전월 대비 증감: +2.3% | 손해액 추이: 증가");

        System.out.println("\n[보험담당자] '마감 통계 산출' 버튼을 누릅니다.");
        enter();

        boolean dbOk = simulateDbSave();
        if (!dbOk) {
            System.out.println("[시스템] [오류] 데이터 집계 실패 — 관리자에게 오류를 통보합니다.");
            System.out.println("[보상담당자] '다시 시도' 버튼을 누릅니다.");
            enter();
            System.out.println("[시스템] 재시도 성공.");
        }

        System.out.println("[시스템] 마감통계 데이터 집계 완료:");
        System.out.println("  월별 접수현황: 324건 | 처리현황: 298건 | 지급현황: 1,250,000,000원 | 손해율: 62.5%");

        System.out.println("\n[보상담당자] '결과 확인' 버튼을 누릅니다.");
        enter();

        System.out.println("[시스템] 보상평가자료 생성 완료 | 결과를 DB에 저장 중...");
        CompensationEvaluationService.saveEvaluation(period, insuranceType);
        System.out.println("[시스템] DB 저장 완료.");
    }
}
