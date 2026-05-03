import insurance.UnderwritingService;
import contract.EndorsementService;
import contract.ReinstatementService;
import contract.PaymentCollectionService;
import contract.MaturityContractService;
import accident.AccidentReportService;
import accident.DamageInvestigationService;

import static common.ConsoleUtil.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("       신동아화재 보험 관리 시스템               ");
        System.out.println("================================================");

        while (true) {
            System.out.println();
            System.out.println("============== 메인 메뉴 ==============");
            System.out.println("  1. 보험청약 심사");
            System.out.println("  2. 배서 관리");
            System.out.println("  3. 부활 관리");
            System.out.println("  4. 분납/수금 관리");
            System.out.println("  5. 만기계약 관리");
            System.out.println("  6. 사고 접수");
            System.out.println("  7. 손해조사");
            System.out.println("  8. 종료");
            System.out.println("=======================================");
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": UnderwritingService.run();      break;
                case "2": EndorsementService.run();       break;
                case "3": ReinstatementService.run();     break;
                case "4": PaymentCollectionService.run(); break;
                case "5": MaturityContractService.run();  break;
                case "6": AccidentReportService.run();    break;
                case "7": DamageInvestigationService.run(); break;
                case "8":
                    System.out.println("\n시스템을 종료합니다.");
                    sc.close();
                    return;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }
}
