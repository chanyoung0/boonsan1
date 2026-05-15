package service.underwriting;

import common.IdGenerator;
import repository.AccidentHistoryRepository;
import repository.CoinsurerRepository;
import repository.InsuranceApplicationRepository;
import repository.InsuredPersonRepository;

import static common.ConsoleUtil.*;

// 보험청약 심사 시나리오 — 인스턴스 메서드 + 의존성 주입
public class UnderwritingService {

    private static final long REINSURANCE_THRESHOLD = 500_000_000L; // 자사 보유한도: 5억원

    private final InsuranceApplicationRepository applicationRepository;
    private final InsuredPersonRepository insuredPersonRepository;
    private final CoinsurerRepository coinsurerRepository;
    private final AccidentHistoryRepository accidentHistoryRepository;
    private final IdGenerator idGenerator;

    // 의존성 주입으로 초기화
    public UnderwritingService(InsuranceApplicationRepository applicationRepository,
                               InsuredPersonRepository insuredPersonRepository,
                               CoinsurerRepository coinsurerRepository,
                               AccidentHistoryRepository accidentHistoryRepository,
                               IdGenerator idGenerator) {
        this.applicationRepository = applicationRepository;
        this.insuredPersonRepository = insuredPersonRepository;
        this.coinsurerRepository = coinsurerRepository;
        this.accidentHistoryRepository = accidentHistoryRepository;
        this.idGenerator = idGenerator;
    }

    // ======================================================
    // 1. 보험청약 심사
    // 액터: 언더라이터
    // ======================================================
    public void run() {
        line();
        System.out.println("[유스케이스] 보험청약을 심사한다");
        System.out.println("액터: 언더라이터");
        line();

        System.out.println("\n[Step 1] 피보험자 기본 정보 입력");
        String name = input("이름");
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
            System.out.println("  ── 계약 기본정보 ───────────────────────────────────────");
            System.out.println("  증권번호      : " + existingPolicyNo);
            System.out.println("  계약상태      : 유효");
            System.out.println("  청약일        : 2023-01-03        승낙일      : 2023-01-05");
            System.out.println("  보험기간      : 2023-01-05 ~ 2033-01-05");
            System.out.println("  납입기간      : 10년              납입주기    : 월납");
            System.out.println("  ── 계약자 / 피보험자 / 수익자 ──────────────────────────");
            System.out.println("  계약자        : " + name + " (생년월일: 1989-07-12, 연락처: 010-9876-5432)");
            System.out.println("  피보험자      : " + name + " (동일)");
            System.out.println("  수익자        : " + name + " (동일)");
            System.out.println("  ── 상품 / 보장 정보 ─────────────────────────────────────");
            System.out.println("  상품명        : 자동차종합보험");
            System.out.println("  주계약내용    : 대인배상 무한, 대물배상 2,000만원, 자손 1,500만원");
            System.out.println("  특약목록      : 상해특약, 입원특약, 긴급출동특약");
            System.out.println("  보험가입금액  : 5,000만원");
            System.out.println("  보험료        : 120,000원/월");
            System.out.println("  ── 납입 / 미납 정보 ─────────────────────────────────────");
            System.out.println("  납입일        : 매월 15일");
            System.out.println("  납입금액      : 120,000원");
            System.out.println("  미납여부      : 없음");
            System.out.println("  ── 자동이체 정보 ────────────────────────────────────────");
            System.out.println("  자동이체      : 신한은행 110-123-456789 (예금주: " + name + ")");
            System.out.println("  ── 피보험자 상세 ────────────────────────────────────────");
            System.out.println("  나이: " + age + " | 성별: " + gender + " | 직업: " + job + " | 연소득: " + income);
            System.out.println("  BMI: " + bmi + " | 투약여부: " + medication + " | 흡연여부: " + smoking + " | 음주량: " + drinking);
            System.out.println("  수술이력: " + surgery + " | 가족력: " + familyHistory);
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
                    if (idx < 0 || idx >= fieldLabels.length) {
                        System.out.println("  [오류] 올바른 번호를 입력하세요.");
                        continue;
                    }
                    System.out.print("  새로운 " + fieldLabels[idx] + ": ");
                    fieldValues[idx] = sc.nextLine().trim();
                    System.out.println("  [시스템] " + fieldLabels[idx] + " → '" + fieldValues[idx] + "'(으)로 수정 예정.");
                }
                job = fieldValues[0]; income = fieldValues[1]; medication = fieldValues[2];
                surgery = fieldValues[3]; smoking = fieldValues[4]; drinking = fieldValues[5];
                bmi = fieldValues[6]; vehicleModel = fieldValues[7]; vehicleNo = fieldValues[8];
                System.out.println("[시스템] 수정된 정보가 자사 DB에 반영되었습니다.");
            }
        }

        int deductDisease      = hasValue(pastDisease) ? -10 : 0;
        int deductMedication   = isYes(medication)     ?  -8 : 0;
        int deductSurgery      = hasValue(surgery)     ?  -7 : 0;
        int deductFamilyHistory= hasValue(familyHistory)?  -5 : 0;
        int deductSmoking      = isYes(smoking)        ?  -5 : 0;
        int deductDrinking     = hasDrinking(drinking) ?  -3 : 0;
        double bmiVal = parseBmi(bmi);
        int deductBmi = bmiVal > 30 ? -8 : bmiVal > 25 ? -3 : 0;
        int ageVal = parseAge(age);
        int deductAge = ageVal > 60 ? -10 : ageVal > 50 ? -5 : 0;

        int inputDeduction = deductDisease + deductMedication + deductSurgery
                + deductFamilyHistory + deductSmoking + deductDrinking + deductBmi + deductAge;

        System.out.println("\n[언더라이터] '신용정보 조회' 버튼을 누릅니다.");
        System.out.println("  >> <<include>> [신용정보를 조회한다] 시나리오 시작");
        int[] creditFlags = new int[2];
        int creditDeduction = creditInfoInquiry(name, creditFlags);
        if (creditDeduction == Integer.MIN_VALUE) {
            return;
        }
        int deductAccident = creditFlags[0] == 1 ? -5 : 0;
        int deductContract = creditFlags[1] == 1 ? -3 : 0;

        int score = 100 + inputDeduction + creditDeduction;

        System.out.println("\n[언더라이터] '심사점수 계산 및 보고서 출력' 버튼을 누릅니다.");
        System.out.println("[시스템] Rule-Based 엔진으로 자동심사를 실행 중...");
        boolean canAutoReview = false;
        System.out.println("[시스템] 자동심사 가능 여부: " + (canAutoReview ? "가능" : "불가 — 수동심사 전환"));

        if (!canAutoReview) {
            score = manualUnderwriting(score);
        } else {
            System.out.println("[시스템] 자동심사 정상 완료.");
        }

        String recommended = score >= 85 ? "승인" : score >= 65 ? "할증" : "거절";
        boolean coinsuranceRecommended = score < 70;

        System.out.println("\n[시스템] 자동심사 보고서:");
        System.out.println("  ── 항목별 심사 결과 ─────────────────────────────────────────");
        printScoreRow("과거질병이력", hasValue(pastDisease) ? pastDisease : "없음", deductDisease);
        printScoreRow("투약여부",     medication,                                   deductMedication);
        printScoreRow("수술이력",     hasValue(surgery) ? surgery : "없음",         deductSurgery);
        printScoreRow("가족력",       hasValue(familyHistory) ? familyHistory : "없음", deductFamilyHistory);
        printScoreRow("흡연여부",     smoking,                                      deductSmoking);
        printScoreRow("음주량",       drinking,                                     deductDrinking);
        printScoreRow("BMI",          bmi + (bmiVal > 30 ? " (비만)" : bmiVal > 25 ? " (과체중)" : " (정상)"), deductBmi);
        printScoreRow("나이",         age + "세",                                   deductAge);
        printScoreRow("사고이력",     creditFlags[0] == 1 ? "있음" : "없음",        deductAccident);
        printScoreRow("타사계약",     creditFlags[1] == 1 ? "있음" : "없음",        deductContract);
        System.out.println("  ── 심사 종합 ─────────────────────────────────────────────────");
        System.out.println("  기본점수         : 100점");
        System.out.printf ("  총 감점          : %d점%n", (inputDeduction + creditDeduction));
        System.out.println("  ──────────────────────────────────────────────────────────────");
        System.out.println("  총점             : " + score + "점");
        System.out.println("  추천 등급        : " + recommended);
        System.out.println("  ── 공동인수 판단 ─────────────────────────────────────────────");
        System.out.println("  공동인수 추천 기준: 심사점수 70점 미만");
        System.out.println("  현재 심사점수    : " + score + "점");
        System.out.println("  공동인수 추천    : " + (coinsuranceRecommended ? "예 (심사점수 기준 미달)" : "아니오 (기준 충족)"));

        if (coinsuranceRecommended) {
            System.out.println("\n[시스템] 심사점수(" + score + "점) 기준 미달 — 공동인수 처리를 자동으로 시작합니다.");
            System.out.println("  >> <<extend>> [공동인수를 처리한다] 시나리오 자동 시작");
            if (!coinsuranceProcess(insuranceAmount)) {
                return;
            }
        }

        System.out.println("\n[언더라이터] 최종 심사결과를 입력합니다.");
        String empNo   = input("사원번호");
        String empName = input("심사자 이름");
        String empDept = input("부서");
        System.out.println("  최종 심사결과:");
        System.out.println("  1. 승인  2. 할증  3. 거절");
        System.out.print(">> 선택: ");
        String finalChoice = sc.nextLine().trim();
        String finalResult = "2".equals(finalChoice) ? "할증" : "3".equals(finalChoice) ? "거절" : "승인";

        System.out.println("\n[시스템] 심사결과를 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.println("[오류] 저장 실패.");
            System.out.println("[언더라이터] '다시 시도' 버튼을 누릅니다.");
            enter();
            if (!simulateDbSave()) {
                System.out.println("[시스템] 관리자에게 오류를 통보하고 시스템을 종료합니다.");
                return;
            }
        }

        System.out.println("[시스템] 심사결과 문서:");
        System.out.println("  ── 심사자 정보 ────────────────────────────────────");
        System.out.println("  사원번호: " + empNo + " | 이름: " + empName + " | 부서: " + empDept);
        System.out.println("  ── 피보험자 정보 ──────────────────────────────────");
        System.out.println("  이름: " + name + " | 나이: " + age + " | 성별: " + gender);
        System.out.println("  직업: " + job + " | 연소득: " + income + " | BMI: " + bmi);
        System.out.println("  투약여부: " + medication + " | 흡연여부: " + smoking + " | 음주량: " + drinking);
        System.out.println("  과거질병이력: " + pastDisease);
        System.out.println("  수술이력: " + surgery + " | 가족력: " + familyHistory);
        System.out.println("  차량기종: " + vehicleModel + " | 차량번호: " + vehicleNo);
        System.out.println("  ── 심사 결과 ──────────────────────────────────────");
        System.out.println("  최종 심사결과: " + finalResult);

        if ("거절".equals(finalResult)) {
            System.out.println("  거절사유: 위험도 기준 초과 (총점 " + score + "점)");
            System.out.println("[시스템] 거절 처리로 청약이 종료됩니다.");
            return;
        }
        if ("할증".equals(finalResult)) {
            System.out.println("  할증조건: 보험료 15% 인상");
        }

        System.out.println("\n  >> <<include>> [청약서 및 증권발행을 한다] 시나리오 시작");
        policyIssuance(name, finalResult, insuranceAmount);
    }

    // ======================================================
    // [신용정보를 조회한다] — <<include>>
    // ======================================================
    private int creditInfoInquiry(String name, int[] creditFlags) {
        System.out.println("\n  [신용정보 조회] ICIS API 호출 중... (피보험자: " + name + ")");

        if (rnd.nextInt(10) < 1) {
            System.out.println("  [오류] ICIS API가 응답하지 않아 정보를 가져오는 것을 실패했습니다.");
            System.out.println("  [언더라이터] '임시저장' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 현재 상태가 임시저장되었습니다. (임시저장번호: TEMP-" + System.currentTimeMillis() + ")");
            System.out.println("  [시스템] 시스템을 종료합니다.");
            return Integer.MIN_VALUE;
        }

        int deduction = 0;

        boolean hasAccident = rnd.nextInt(10) < 3;
        System.out.println("  [시스템] 사고 이력 조회 결과: " + (hasAccident ? "이력 있음" : "이력 없음"));
        if (hasAccident) {
            System.out.println("  [시스템] 사고 이력 상세 (보고서):");
            System.out.println("    사고접수번호  : ACC-2022-003481");
            System.out.println("    접수일        : 2022-05-10         사고일시    : 2022-05-09 16:30");
            System.out.println("    사고장소      : 서울시 강남구 테헤란로 123번지");
            System.out.println("    사고유형      : 자동차 추돌 (대물)");
            System.out.println("    진단명        : 경추 염좌           진단코드    : S13.4");
            System.out.println("    치료내용      : 물리치료 12회, 약물치료 2주");
            System.out.println("    입원기간      : 3일                 수술여부    : 없음");
            System.out.println("    청구금액      : 1,500,000원         인정금액    : 1,200,000원");
            System.out.println("    지급일        : 2022-06-15");
            System.out.println("  [언더라이터] '심사 반영' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 사고 이력을 점수에 반영합니다. (-5점)");
            creditFlags[0] = 1;
            deduction -= 5;
        }

        System.out.println("\n  [언더라이터] '타사 계약 조회' 버튼을 누릅니다.");
        boolean hasOtherContract = rnd.nextInt(10) < 4;
        System.out.println("  [시스템] 타사 계약 조회 결과: " + (hasOtherContract ? "계약 있음" : "계약 없음"));
        if (hasOtherContract) {
            System.out.println("  [시스템] 타사 계약 정보 (보고서):");
            System.out.println("    보험사    : 한화생명             상품명    : 종신보험");
            System.out.println("    증권번호  : HL-2020-123456       상태      : 유효");
            System.out.println("    보험기간  : 2020-03-01 ~ 2040-03-01");
            System.out.println("    가입금액  : 1억원                보험료    : 150,000원/월");
            System.out.println("  [언더라이터] '심사 반영' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 타사 계약 정보를 심사에 반영합니다. (-3점)");
            creditFlags[1] = 1;
            deduction -= 3;
        }

        return deduction;
    }

    // ======================================================
    // A3: 수동심사
    // ======================================================
    private int manualUnderwriting(int baseScore) {
        System.out.println("\n[시스템] 자동심사 불가 항목이 존재합니다. 추가 심사 유형을 선택하세요:");
        System.out.println("  1. 진단심사  2. 특인심사  3. 일반심사  4. 이미지심사  5. 적부심사");
        System.out.print(">> 선택: ");
        String type = sc.nextLine().trim();
        int adj;

        switch (type) {
            case "1":
                System.out.println("[시스템] 진단심사 입력 폼:");
                input("  진단명");
                input("  진단코드");
                input("  진단일 (YYYY-MM-DD)");
                input("  담당의사");
                input("  진단결과");
                adj = -10;
                break;
            case "2":
                System.out.println("[시스템] 특인심사 입력 폼:");
                input("  특인사유");
                input("  특인조건");
                input("  할증율 (%)");
                input("  부담보항목");
                input("  특인승인자");
                adj = -5;
                break;
            case "3":
                System.out.println("[시스템] 일반심사 입력 폼:");
                input("  심사항목");
                input("  심사결과");
                input("  심사의견");
                adj = -3;
                break;
            case "4":
                System.out.println("[시스템] 이미지심사 - 첨부서류 이미지 출력됨");
                input("  심사결과 (이상없음/이상있음)");
                input("  심사의견");
                adj = -2;
                break;
            case "5":
                System.out.println("[시스템] 적부심사 입력 폼:");
                input("  계약적합성 항목");
                input("  적부결과");
                input("  적부의견");
                input("  현장조사결과");
                adj = -8;
                break;
            default:
                System.out.println("[시스템] 일반심사로 처리합니다.");
                adj = -3;
        }
        System.out.println("[시스템] 심사 결과를 점수에 반영합니다. (" + adj + "점)");
        return Math.max(0, baseScore + adj);
    }

    // ======================================================
    // [공동인수를 처리한다] — <<extend>>
    // ======================================================
    private boolean coinsuranceProcess(long insuranceAmount) {
        System.out.println("\n  [공동인수 처리]");

        if (rnd.nextInt(10) < 1) {
            System.out.println("  [오류] 공동인수사 시스템 연결에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
            System.out.println("  [언더라이터] '임시저장' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 임시저장 완료. (임시저장번호: TEMP-" + System.currentTimeMillis() + ")");
            System.out.println("  [시스템] 시스템을 종료합니다.");
            return false;
        }

        String coinsurerName;
        while (true) {
            System.out.println("  [시스템] 공동인수 가능 보험사 목록 (추천):");
            System.out.println("    1. 삼성화재   (최대 지분: 40%)");
            System.out.println("    2. DB손해보험 (최대 지분: 30%)");
            System.out.println("    3. 현대해상   (최대 지분: 35%)");
            System.out.println("    4. 직접 지정");
            System.out.print("  >> 공동인수사 선택: ");
            String choice = sc.nextLine().trim();

            if ("4".equals(choice)) {
                System.out.println("  [시스템] 전체 등록 보험사 목록:");
                System.out.println("  ┌──────────────────┬────────────────┬──────────────────┐");
                System.out.println("  │ 보험사명         │ 공동인수 가능  │ 최대 인수 지분율 │");
                System.out.println("  ├──────────────────┼────────────────┼──────────────────┤");
                System.out.println("  │ 삼성화재         │ 가능           │ 40%              │");
                System.out.println("  │ DB손해보험       │ 가능           │ 30%              │");
                System.out.println("  │ 현대해상         │ 가능           │ 35%              │");
                System.out.println("  │ 한화손해보험     │ 가능           │ 25%              │");
                System.out.println("  │ 롯데손해보험     │ 가능           │ 20%              │");
                System.out.println("  │ MG손해보험       │ 불가           │ -                │");
                System.out.println("  │ 흥국화재         │ 가능           │ 15%              │");
                System.out.println("  └──────────────────┴────────────────┴──────────────────┘");
                coinsurerName = input("  보험사명 입력");
            } else {
                coinsurerName = "2".equals(choice) ? "DB손해보험" : "3".equals(choice) ? "현대해상" : "삼성화재";
            }

            int myShare;
            int coinsurerShare;
            try { myShare = Integer.parseInt(input("  자사 보유 지분율 (%)").trim()); } catch (NumberFormatException e) { myShare = 80; }
            try { coinsurerShare = Integer.parseInt(input("  " + coinsurerName + " 지분율 (%)").trim()); } catch (NumberFormatException e) { coinsurerShare = 20; }

            long myHolding = insuranceAmount * myShare / 100;
            long coinsurerHolding = insuranceAmount * coinsurerShare / 100;
            System.out.println("  [시스템] 보험가입금액    : " + formatAmount(insuranceAmount));
            System.out.println("  [시스템] 자사 보유액     : " + formatAmount(myHolding) + " (지분 " + myShare + "%)");
            System.out.println("  [시스템] " + coinsurerName + " 보유액: " + formatAmount(coinsurerHolding) + " (지분 " + coinsurerShare + "%)");

            System.out.println("  [언더라이터] '공동인수 접수' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 공동인수 참여 요청을 " + coinsurerName + "에 전송 중...");

            if (rnd.nextInt(100) < 20) {
                System.out.println("  [시스템] " + coinsurerName + " 참여 거절.");
                System.out.println("  ── 거절 정보 ──────────────────────────────────────────");
                System.out.println("  거절 보험사    : " + coinsurerName);
                System.out.println("  거절 사유      : 내부 인수 한도 초과 / 위험등급 부적합");
                System.out.println("  [시스템] 언더라이터에게 거절 알림을 발송합니다.");
                System.out.println("  [알림] " + coinsurerName + " 공동인수 거절. 대체 공동인수사를 선택하거나 지분 구조를 재조정하세요.");
                System.out.println("  [언더라이터] 대체 공동인수사를 선택하거나 지분 구조를 재조정합니다.");
                continue;
            }

            System.out.println("  [시스템] " + coinsurerName + " 승인 완료.");
            break;
        }

        String ciNo = "CI-2024-" + String.format("%04d", rnd.nextInt(9000) + 1000);
        System.out.println("  [시스템] 공동인수 접수 완료 (공동인수 접수번호: " + ciNo + ")");
        System.out.println("  [시스템] 공동인수 정보가 DB에 저장되었습니다.");
        return true;
    }

    // ======================================================
    // [청약서 및 증권발행을 한다] — <<include>>
    // ======================================================
    private void policyIssuance(String name, String finalResult, long insuranceAmount) {
        System.out.println("\n  [청약서 및 증권발행]");

        String appNo = "APP-2024-" + String.format("%06d", rnd.nextInt(999999) + 1);
        String appliedCondition = "할증".equals(finalResult) ? "할증체 (보험료 15% 인상)" : "표준체 (조건 없음)";

        System.out.println("  [시스템] 청약 확정 화면:");
        System.out.println("    청약번호      : " + appNo);
        System.out.println("    피보험자      : " + name);
        System.out.println("    상품명        : 자동차보험");
        System.out.println("    보험가입금액  : " + formatAmount(insuranceAmount));
        System.out.println("    보험료        : 150,000원/월");
        System.out.println("    심사결과      : " + finalResult);
        System.out.println("    적용조건      : " + appliedCondition);

        System.out.print("  [언더라이터] 청약서 내용에 오류가 있습니까? (Y/N): ");
        if ("Y".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println("  [언더라이터] '청약서 수정' 버튼을 누릅니다.");
            System.out.println("  [시스템] 수정 가능한 문서 형태로 전환합니다.");
            input("  수정할 항목 및 내용");
            System.out.println("  [시스템] 관리자에게 오류 리포트를 전달하고 문서를 업데이트합니다.");
        }

        System.out.println("  [언더라이터] '승인' 버튼을 누릅니다.");
        enter();
        String policyNo = "P2024-" + String.format("%06d", rnd.nextInt(999999) + 1);
        System.out.println("  [시스템] 증권번호 자동 채번: " + policyNo);
        System.out.println("  [시스템] 보험증권 데이터:");
        System.out.println("    증권번호: " + policyNo + " | 계약일: 2024-01-15");
        System.out.println("    보장 개시일: 2024-02-01 | 보장 만료일: 2025-02-01");

        System.out.println("  [언더라이터] '발행 승인' 버튼을 누릅니다.");
        enter();

        System.out.println("  [시스템] 계약 정보를 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.println("  [오류] 저장 실패.");
            System.out.println("  [언더라이터] '다시 시도' 버튼을 누릅니다.");
            enter();
            if (!simulateDbSave()) {
                System.out.println("  [시스템] 관리자에게 오류를 통보하고 시스템을 종료합니다.");
                return;
            }
        }

        System.out.println("  [시스템] 청약서 확정 및 증권발행 완료.");
        System.out.println("  ── DB 저장 정보 ───────────────────────────────────────────");
        System.out.println("    청약번호      : " + appNo);
        System.out.println("    증권번호      : " + policyNo);
        System.out.println("    계약 상태     : 유효");
        System.out.println("    계약자 정보   : " + name + " (연락처: 010-9876-5432)");
        System.out.println("    수익자 정보   : " + name + " (동일)");
        System.out.println("    상품코드      : AUTO-2024-001");
        System.out.println("    납입주기      : 월납");
        System.out.println("    보장 범위     : 대인배상 무한, 대물배상 2,000만원, 자손 1,500만원");
        System.out.println("    특약 사항     : 상해특약, 입원특약, 긴급출동특약");
        System.out.println("    적용 조건     : " + appliedCondition);
        System.out.println("    약관 버전     : 2024-표준약관 v3.2");

        System.out.println("\n  [시스템] 재보험 처리 필요 여부 자동 판단 중...");
        System.out.println("  [시스템] 보험가입금액: " + formatAmount(insuranceAmount)
                + " / 자사 보유한도: " + formatAmount(REINSURANCE_THRESHOLD));

        if (insuranceAmount > REINSURANCE_THRESHOLD) {
            System.out.println("  [시스템] 보험가입금액이 자사 보유한도(" + formatAmount(REINSURANCE_THRESHOLD)
                    + ")를 초과 — 재보험 처리가 자동으로 시작됩니다.");
            System.out.println("  >> <<extend>> [재보험 처리를 한다] 시나리오 자동 시작");
            if (!reinsuranceProcess(policyNo, insuranceAmount)) {
                return;
            }
        } else {
            System.out.println("  [시스템] \"재보험 적용 대상이 아님\"");
            System.out.println("  ── 판단 근거 ──────────────────────────────────────────────");
            System.out.println("    보험가입금액  : " + formatAmount(insuranceAmount));
            System.out.println("    자사 보유한도  : " + formatAmount(REINSURANCE_THRESHOLD));
            System.out.println("    판단 근거     : 보험가입금액이 자사 보유한도 이하");
            System.out.println("    위험등급      : 일반");
            System.out.println("  [언더라이터] '결과 상세' 버튼을 누릅니다.");
            enter();
            System.out.println("  ── 재보험 제외 근거 ───────────────────────────────────────");
            System.out.println("    보험가입금액  : " + formatAmount(insuranceAmount) + " (자사 보유한도 이하)");
            System.out.println("    자사 보유한도 : " + formatAmount(REINSURANCE_THRESHOLD));
            System.out.println("    위험등급      : 일반 (재보험 적용 기준 미충족)");
            System.out.println("  [언더라이터] '확인' 버튼을 누릅니다.");
            enter();
        }

        System.out.println("  [언더라이터] '보험증권 발행 완료' 버튼을 누릅니다.");
        enter();
        System.out.println("  [시스템] 청약번호 상태: '심사 완료' | 미심사 청약 목록에서 제거됨");
    }

    // ======================================================
    // [재보험 처리를 한다] — <<extend>>
    // ======================================================
    private boolean reinsuranceProcess(String policyNo, long insuranceAmount) {
        System.out.println("\n  [재보험 처리]");
        System.out.println("  [언더라이터] 재보험 처리 대상 계약을 선택합니다.");
        System.out.println("  [시스템] 계약 및 위험 정보:");
        System.out.println("    계약번호: " + policyNo + " | 위험등급: 고위험");
        System.out.println("  [시스템] 보험가입금액이 자사 보유한도 초과 — 재보험 적용 대상으로 확정.");

        System.out.println("  [언더라이터] 재보험 조건을 설정합니다.");
        input("  재보험 방식 (예: 비례재보험)");
        String reinsuranceRatioStr = input("  재보험 비율 (%)");
        String reinsurerName = input("  재보험사명");

        System.out.println("  [시스템] 재보험사에 요청 정보를 전송 중...");
        if (rnd.nextInt(10) < 1) {
            System.out.println("  ── 재보험사 응답 실패 ──────────────────────────────────────");
            System.out.println("    실패 사유  : " + reinsurerName + " 시스템 응답 없음 (Timeout)");
            System.out.println("    재시도 안내: 잠시 후 다시 시도하거나 관리자에게 문의하세요.");
            System.out.println("  [언더라이터] '확인' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 관리자에게 오류를 통보합니다.");
            System.out.println("  [시스템] 임시저장 중...");
            System.out.println("    계약번호      : " + policyNo);
            System.out.println("    임시저장일시  : 2024-01-15 14:30:00");
            System.out.println("    임시저장번호  : TEMP-" + System.currentTimeMillis());
            System.out.println("  [시스템] 시스템을 종료합니다.");
            return false;
        }

        int reinsuranceRatio;
        try { reinsuranceRatio = Integer.parseInt(reinsuranceRatioStr.replace("%", "").trim()); } catch (NumberFormatException e) { reinsuranceRatio = 30; }
        long reinsurancePremium = insuranceAmount * reinsuranceRatio / 100 / 10;

        System.out.println("  [시스템] 재보험사 응답: 인수 가능 / 재보험비율 " + reinsuranceRatio
                + "% / 재보험료 " + formatAmount(reinsurancePremium));

        System.out.print("  [언더라이터] 재보험 조건을 수정하시겠습니까? (Y/N): ");
        if ("Y".equalsIgnoreCase(sc.nextLine().trim())) {
            input("  수정할 비율 또는 조건");
            System.out.println("  [시스템] 수정된 조건으로 재보험사에 재요청하고 결과를 출력합니다.");
        }

        System.out.println("  [언더라이터] '재보험 조건 검증 확인' 버튼을 누릅니다.");
        enter();

        int retentionRatio = 100 - reinsuranceRatio;
        long retentionAmount = insuranceAmount * retentionRatio / 100;
        System.out.println("  [시스템] 재보험료를 회계장부에 계상합니다.");
        System.out.println("    재보험료      : " + formatAmount(reinsurancePremium));
        System.out.println("    출재비율      : " + reinsuranceRatio + "%");
        System.out.println("    보유비율      : " + retentionRatio + "%");
        System.out.println("    보유금액      : " + formatAmount(retentionAmount));
        System.out.println("    계상금액      : " + formatAmount(reinsurancePremium));
        System.out.println("    계정과목      : 출재보험료 (재보험 출재계정)");
        System.out.println("    계상일자      : 2024-01-15");

        System.out.println("  [언더라이터] '저장' 버튼을 누릅니다.");
        enter();
        System.out.println("  [시스템] 재보험 처리 결과가 DB에 저장되었습니다.");

        String clearingDate = input("  청산 예정일 (YYYY-MM-DD)");
        input("  청산 금액 (원)");
        input("  청산 방식");
        System.out.println("  [시스템] 청산 정보가 저장되고 계약 상태: '재보험처리완료'");

        System.out.println("  ── 재보험 처리 완료 ─────────────────────────────────────────");
        System.out.println("    계약번호      : " + policyNo);
        System.out.println("    재보험사      : " + reinsurerName);
        System.out.println("    재보험료      : " + formatAmount(reinsurancePremium));
        System.out.println("    청산예정일    : " + clearingDate);
        System.out.println("    처리완료일시  : 2024-01-15 15:00:00");

        return true;
    }

    private void printScoreRow(String label, String value, int deduction) {
        String deductStr = deduction < 0 ? "(감점 " + deduction + "점)" : "(±0)";
        System.out.printf("  %-14s : %-28s %s%n", label, value, deductStr);
    }

    private boolean isYes(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        String v = val.trim().toUpperCase();
        return v.equals("Y") || v.equals("예") || v.startsWith("Y");
    }

    private boolean hasValue(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        String v = val.trim();
        return !v.equalsIgnoreCase("없음") && !v.equalsIgnoreCase("N")
                && !v.equalsIgnoreCase("아니오") && !v.equalsIgnoreCase("none");
    }

    private boolean hasDrinking(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        String v = val.trim();
        if (v.equalsIgnoreCase("없음") || v.equalsIgnoreCase("N") || v.equalsIgnoreCase("아니오")) return false;
        if (v.matches("주\\s*0\\s*회.*")) return false;
        return true;
    }

    private int parseAge(String ageStr) {
        try { return Integer.parseInt(ageStr.replaceAll("[^0-9]", "")); }
        catch (NumberFormatException e) { return 0; }
    }

    private double parseBmi(String bmiStr) {
        try { return Double.parseDouble(bmiStr.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
