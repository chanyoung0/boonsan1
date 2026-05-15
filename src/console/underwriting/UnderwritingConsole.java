package console.underwriting;

import model.underwriting.InsuranceApplication;
import service.underwriting.UnderwritingService;

import static common.ConsoleUtil.*;

// 보험청약 심사 콘솔 I/O — 언더라이터 유스케이스 입출력 전담
public class UnderwritingConsole {

    public static void run() {
        UnderwritingService service = new UnderwritingService();

        line();
        System.out.println("[유스케이스] 보험청약을 심사한다");
        System.out.println("액터: 언더라이터");
        line();

        System.out.println("\n[Step 1] 피보험자 기본 정보 입력");
        String name     = input("이름");
        input("생년월일 (YYYYMMDD)");
        input("주민등록번호");
        String vehicleNo = input("차량번호");

        long insuranceAmount = (long)(rnd.nextInt(20) + 1) * 100_000_000L;

        System.out.println("\n[시스템] 자사 DB에서 피보험자 이력을 조회 중...");
        boolean isNewCustomer = rnd.nextInt(10) < 3;
        System.out.println("[시스템] 조회 결과: " + (isNewCustomer ? "신규 고객 (자사 U/W 이력 및 기존 계약 없음)" : "기존 고객 (이력 및 계약 존재)"));

        String age, gender, job, income, pastDisease, medication, surgery, familyHistory, smoking, drinking, bmi, vehicleModel;

        if (isNewCustomer) {
            System.out.println("[언더라이터] 신규 고객 데이터를 입력합니다.");
            age           = input("나이");
            gender        = input("성별 (남/여)");
            job           = input("직업");
            income        = input("연소득 (만원)");
            pastDisease   = input("과거질병이력 (없으면 Enter)");
            medication    = input("투약여부 (Y/N)");
            surgery       = input("수술이력 (없으면 Enter)");
            familyHistory = input("가족력 (없으면 Enter)");
            smoking       = input("흡연여부 (Y/N)");
            drinking      = input("음주량 (없으면 '없음')");
            bmi           = input("BMI");
            vehicleModel  = input("차량기종");
            System.out.println("[시스템] 신규 고객 정보가 자사 DB에 등록되었습니다.");
        } else {
            age = "35"; gender = "남"; job = "회사원"; income = "5,000만원";
            pastDisease = "없음"; medication = "N"; surgery = "없음";
            familyHistory = "없음"; smoking = "N"; drinking = "주 1회";
            bmi = "22.4"; vehicleModel = "현대 소나타";
            String existingPolicyNo = "P2023-004512";

            System.out.println("[시스템] 기존 U/W 이력 및 계약 정보:");
            System.out.println("  증권번호: " + existingPolicyNo + " | 계약상태: 유효 | 피보험자: " + name);
            System.out.println("  나이: " + age + " | 성별: " + gender + " | 직업: " + job);
            System.out.println("  BMI: " + bmi + " | 투약여부: " + medication + " | 흡연여부: " + smoking);
            System.out.println("  차량기종: " + vehicleModel + " | 차량번호: " + vehicleNo);

            System.out.print("\n[시스템] 갱신이 필요한 정보가 있습니까? (Y/N): ");
            if ("Y".equalsIgnoreCase(sc.nextLine().trim())) {
                String[] fieldLabels = {"직업", "연소득", "투약여부", "수술이력", "흡연여부", "음주량", "BMI", "차량기종", "차량번호"};
                String[] fieldValues = {job, income, medication, surgery, smoking, drinking, bmi, vehicleModel, vehicleNo};
                while (true) {
                    System.out.println("\n  갱신할 항목을 선택하세요 (0: 완료):");
                    for (int i = 0; i < fieldLabels.length; i++) {
                        System.out.printf("    %d. %-10s현재값: %s%n", i + 1, fieldLabels[i], fieldValues[i]);
                    }
                    System.out.print("  >> 선택: ");
                    String sel = sc.nextLine().trim();
                    if ("0".equals(sel)) break;
                    int idx;
                    try { idx = Integer.parseInt(sel) - 1; } catch (NumberFormatException e) { idx = -1; }
                    if (idx < 0 || idx >= fieldLabels.length) { System.out.println("  [오류] 올바른 번호를 입력하세요."); continue; }
                    System.out.print("  새로운 " + fieldLabels[idx] + ": ");
                    fieldValues[idx] = sc.nextLine().trim();
                }
                job = fieldValues[0]; income = fieldValues[1]; medication = fieldValues[2];
                surgery = fieldValues[3]; smoking = fieldValues[4]; drinking = fieldValues[5];
                bmi = fieldValues[6]; vehicleModel = fieldValues[7]; vehicleNo = fieldValues[8];
                System.out.println("[시스템] 수정된 정보가 자사 DB에 반영되었습니다.");
            }
        }

        System.out.println("\n[언더라이터] '신용정보 조회' 버튼을 누릅니다.");
        System.out.println("  >> <<include>> [신용정보를 조회한다] 시나리오 시작");
        int[] creditFlags = new int[2];
        if (!creditInfoInquiry(name, creditFlags)) return;

        int score = service.calculateInputScore(pastDisease, medication, surgery,
                familyHistory, smoking, drinking, bmi, age, creditFlags[0] == 1, creditFlags[1] == 1);

        System.out.println("\n[언더라이터] '심사점수 계산 및 보고서 출력' 버튼을 누릅니다.");
        System.out.println("[시스템] Rule-Based 엔진으로 자동심사를 실행 중...");
        boolean canAutoReview = false;
        System.out.println("[시스템] 자동심사 가능 여부: " + (canAutoReview ? "가능" : "불가 — 수동심사 전환"));

        if (!canAutoReview) {
            score = manualUnderwritingInput(score, service);
        }

        String  recommended          = service.determineResult(score);
        boolean coinsuranceRecommended = service.needsCoinsurance(score);

        System.out.println("\n[시스템] 자동심사 보고서:");
        System.out.println("  총점: " + score + "점  |  추천 등급: " + recommended);
        System.out.println("  공동인수 추천: " + (coinsuranceRecommended ? "예 (심사점수 기준 미달)" : "아니오"));

        if (coinsuranceRecommended) {
            System.out.println("\n[시스템] 공동인수 처리를 자동으로 시작합니다.");
            System.out.println("  >> <<extend>> [공동인수를 처리한다] 시나리오 자동 시작");
            if (!coinsuranceInput(insuranceAmount)) return;
        }

        System.out.println("\n[언더라이터] 최종 심사결과를 입력합니다.");
        String empNo     = input("사원번호");
        String empName   = input("심사자 이름");
        String empDept   = input("부서");
        System.out.println("  1. 승인  2. 할증  3. 거절");
        System.out.print(">> 선택: ");
        String finalChoice = sc.nextLine().trim();
        String finalResult = "2".equals(finalChoice) ? "할증" : "3".equals(finalChoice) ? "거절" : "승인";

        System.out.println("\n[시스템] 심사결과를 DB에 저장 중...");
        service.saveUnderwritingResult(empName, score, finalResult, coinsuranceRecommended);
        System.out.println("[시스템] 사원번호: " + empNo + " | 이름: " + empName + " | 부서: " + empDept);
        System.out.println("[시스템] 최종 심사결과: " + finalResult);

        if ("거절".equals(finalResult)) {
            System.out.println("  거절사유: 위험도 기준 초과 (총점 " + score + "점)");
            return;
        }
        if ("할증".equals(finalResult)) {
            System.out.println("  할증조건: 보험료 15% 인상");
        }

        System.out.println("\n  >> <<include>> [청약서 및 증권발행을 한다] 시나리오 시작");
        policyIssuanceFlow(name, finalResult, insuranceAmount, service);
    }

    // 신용정보 조회 I/O — ICIS API 호출 결과 표시, 실패 시 false 반환
    private static boolean creditInfoInquiry(String name, int[] creditFlags) {
        System.out.println("\n  [신용정보 조회] ICIS API 호출 중... (피보험자: " + name + ")");
        if (rnd.nextInt(10) < 1) {
            System.out.println("  [오류] ICIS API가 응답하지 않습니다.");
            System.out.println("  [언더라이터] '임시저장' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 임시저장 완료. (임시저장번호: TEMP-" + System.currentTimeMillis() + ")");
            return false;
        }
        boolean hasAccident = rnd.nextInt(10) < 3;
        System.out.println("  [시스템] 사고 이력 조회 결과: " + (hasAccident ? "이력 있음 (-5점)" : "이력 없음"));
        if (hasAccident) creditFlags[0] = 1;
        boolean hasOtherContract = rnd.nextInt(10) < 4;
        System.out.println("  [시스템] 타사 계약 조회 결과: " + (hasOtherContract ? "계약 있음 (-3점)" : "계약 없음"));
        if (hasOtherContract) creditFlags[1] = 1;
        return true;
    }

    // 수동심사 I/O — 심사 유형 입력받아 조정된 점수 반환
    private static int manualUnderwritingInput(int baseScore, UnderwritingService service) {
        System.out.println("\n[시스템] 자동심사 불가. 추가 심사 유형을 선택하세요:");
        System.out.println("  1. 진단심사  2. 특인심사  3. 일반심사  4. 이미지심사  5. 적부심사");
        System.out.print(">> 선택: ");
        String type = sc.nextLine().trim();
        int adj = service.getManualUnderwritingAdjustment(type);
        System.out.println("[시스템] 심사 결과를 점수에 반영합니다. (" + adj + "점)");
        return Math.max(0, baseScore + adj);
    }

    // 공동인수 I/O — 공동인수사 선택 및 결과 표시, 실패 시 false 반환
    private static boolean coinsuranceInput(long insuranceAmount) {
        System.out.println("\n  [공동인수 처리]");
        if (rnd.nextInt(10) < 1) {
            System.out.println("  [오류] 공동인수사 시스템 연결에 실패하였습니다.");
            enter();
            System.out.println("  [시스템] 임시저장 완료.");
            return false;
        }
        while (true) {
            System.out.println("  [시스템] 공동인수 가능 보험사 목록:");
            System.out.println("    1. 삼성화재  2. DB손해보험  3. 현대해상  4. 직접 지정");
            System.out.print("  >> 공동인수사 선택: ");
            String choice = sc.nextLine().trim();
            String coinsurerName = "2".equals(choice) ? "DB손해보험" : "3".equals(choice) ? "현대해상"
                    : "4".equals(choice) ? input("  보험사명 입력") : "삼성화재";
            int myShare; int coinsurerShare;
            try { myShare = Integer.parseInt(input("  자사 보유 지분율 (%)").trim()); } catch (NumberFormatException e) { myShare = 80; }
            try { coinsurerShare = Integer.parseInt(input("  " + coinsurerName + " 지분율 (%)").trim()); } catch (NumberFormatException e) { coinsurerShare = 20; }
            System.out.println("  [시스템] 자사 보유액: " + formatAmount(insuranceAmount * myShare / 100)
                    + " | " + coinsurerName + " 보유액: " + formatAmount(insuranceAmount * coinsurerShare / 100));
            enter();
            System.out.println("  [시스템] " + coinsurerName + "에 참여 요청 전송 중...");
            if (rnd.nextInt(100) < 20) {
                System.out.println("  [시스템] " + coinsurerName + " 참여 거절. 대체 공동인수사를 선택하세요.");
                continue;
            }
            System.out.println("  [시스템] " + coinsurerName + " 승인 완료.");
            break;
        }
        System.out.println("  [시스템] 공동인수 접수 완료 (접수번호: CI-2024-" + String.format("%04d", rnd.nextInt(9000) + 1000) + ")");
        return true;
    }

    // 청약서 및 증권발행 I/O — 입력 수집 후 서비스에 위임
    private static void policyIssuanceFlow(String name, String finalResult, long insuranceAmount, UnderwritingService service) {
        System.out.println("\n  [청약서 및 증권발행]");
        String appliedCondition = "할증".equals(finalResult) ? "할증체 (보험료 15% 인상)" : "표준체 (조건 없음)";
        String appNo = "APP-2024-" + String.format("%06d", rnd.nextInt(999999) + 1);

        System.out.println("  [시스템] 청약번호: " + appNo + " | 피보험자: " + name + " | 적용조건: " + appliedCondition);
        System.out.print("  [언더라이터] 청약서 내용에 오류가 있습니까? (Y/N): ");
        if ("Y".equalsIgnoreCase(sc.nextLine().trim())) { input("  수정할 항목 및 내용"); }
        enter();

        String policyNo = "P2024-" + String.format("%06d", rnd.nextInt(999999) + 1);
        System.out.println("  [시스템] 증권번호: " + policyNo);
        enter();

        System.out.println("  [시스템] 계약 정보를 DB에 저장 중...");
        InsuranceApplication application = service.createAndSaveApplication(name, insuranceAmount, appliedCondition);
        service.createAndSaveContract(policyNo, application);
        System.out.println("  [시스템] 청약번호: " + appNo + " | 증권번호: " + policyNo + " | 계약 상태: 유효");

        if (service.needsReinsurance(insuranceAmount)) {
            System.out.println("\n  >> <<extend>> [재보험 처리를 한다] 시나리오 자동 시작");
            reinsuranceInput(policyNo, insuranceAmount);
        } else {
            System.out.println("  [시스템] 재보험 적용 대상이 아님 (보험가입금액 자사 보유한도 이하)");
        }
        enter();
        System.out.println("  [시스템] 청약번호 상태: '심사 완료'");
    }

    // 재보험 처리 I/O
    private static void reinsuranceInput(String policyNo, long insuranceAmount) {
        System.out.println("\n  [재보험 처리]");
        System.out.println("  [시스템] 계약번호: " + policyNo + " | 위험등급: 고위험");
        input("  재보험 방식");
        String ratioStr      = input("  재보험 비율 (%)");
        String reinsurerName = input("  재보험사명");
        System.out.println("  [시스템] 재보험사에 요청 정보를 전송 중...");
        if (rnd.nextInt(10) < 1) {
            System.out.println("  [오류] " + reinsurerName + " 응답 없음. 임시저장 후 종료합니다.");
            return;
        }
        int ratio; try { ratio = Integer.parseInt(ratioStr.replace("%", "").trim()); } catch (NumberFormatException e) { ratio = 30; }
        long premium = insuranceAmount * ratio / 100 / 10;
        System.out.println("  [시스템] 재보험료: " + formatAmount(premium) + " | 출재비율: " + ratio + "%");
        System.out.print("  [언더라이터] 재보험 조건을 수정하시겠습니까? (Y/N): ");
        if ("Y".equalsIgnoreCase(sc.nextLine().trim())) { input("  수정할 비율 또는 조건"); }
        enter();
        String clearingDate = input("  청산 예정일 (YYYY-MM-DD)");
        System.out.println("  [시스템] 재보험 처리 완료: " + reinsurerName + " | 청산예정일: " + clearingDate);
    }
}
