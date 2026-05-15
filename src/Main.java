import console.underwriting.UnderwritingConsole;
import console.contract.EndorsementConsole;
import console.contract.ReinstatementConsole;
import console.contract.PaymentCollectionConsole;
import console.contract.MaturityContractConsole;
import console.contract.PayoutConsole;
import console.accident.AccidentReportConsole;
import console.accident.DamageInvestigationConsole;
import console.insurance.InsuranceProductConsole;

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
            System.out.println("  8. 상품 개발");
            System.out.println("  9. 제지급금 관리");
            System.out.println("  10. 종료");
            System.out.println("=======================================");
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": UnderwritingConsole.run();      break;
                case "2": EndorsementConsole.run();       break;
                case "3": ReinstatementConsole.run();     break;
                case "4": PaymentCollectionConsole.run(); break;
                case "5": MaturityContractConsole.run();  break;
                case "6": AccidentReportConsole.run();    break;
                case "7": DamageInvestigationConsole.run(); break;
                case "8": InsuranceProductConsole.run();  break;
                case "9": PayoutConsole.run();            break;
                case "10":
                    System.out.println("\n시스템을 종료합니다.");
                    sc.close();
                    return;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }
}
