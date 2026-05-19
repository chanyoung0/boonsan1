package ui.console;

import console.accident.AccidentReportConsole;
import console.accident.DamageInvestigationConsole;
import console.compensation.CompensationEvaluationConsole;
import console.compensation.PartnerManagementConsole;
import console.contract.EndorsementConsole;
import console.contract.MaturityContractConsole;
import console.contract.PaymentCollectionConsole;
import console.contract.PayoutConsole;
import console.contract.ReinstatementConsole;
import console.insurance.InsuranceDesignConsole;
import console.underwriting.UnderwritingConsole;

import static common.ConsoleUtil.sc;

// 보험 관리 시스템 콘솔 진입점 — 메뉴 루프 + Console 디스패치
public class InsuranceManagementCli {

    private final ConsoleView view;
    private final UnderwritingConsole underwritingConsole;
    private final EndorsementConsole endorsementConsole;
    private final ReinstatementConsole reinstatementConsole;
    private final PaymentCollectionConsole paymentCollectionConsole;
    private final MaturityContractConsole maturityContractConsole;
    private final AccidentReportConsole accidentReportConsole;
    private final DamageInvestigationConsole damageInvestigationConsole;
    private final InsuranceDesignConsole insuranceDesignConsole;
    private final PayoutConsole payoutConsole;
    private final CompensationEvaluationConsole compensationEvaluationConsole;
    private final PartnerManagementConsole partnerManagementConsole;

    // 의존성 주입으로 초기화
    public InsuranceManagementCli(ConsoleView view,
                                  UnderwritingConsole underwritingConsole,
                                  EndorsementConsole endorsementConsole,
                                  ReinstatementConsole reinstatementConsole,
                                  PaymentCollectionConsole paymentCollectionConsole,
                                  MaturityContractConsole maturityContractConsole,
                                  AccidentReportConsole accidentReportConsole,
                                  DamageInvestigationConsole damageInvestigationConsole,
                                  InsuranceDesignConsole insuranceDesignConsole,
                                  PayoutConsole payoutConsole,
                                  CompensationEvaluationConsole compensationEvaluationConsole,
                                  PartnerManagementConsole partnerManagementConsole) {
        this.view = view;
        this.underwritingConsole = underwritingConsole;
        this.endorsementConsole = endorsementConsole;
        this.reinstatementConsole = reinstatementConsole;
        this.paymentCollectionConsole = paymentCollectionConsole;
        this.maturityContractConsole = maturityContractConsole;
        this.accidentReportConsole = accidentReportConsole;
        this.damageInvestigationConsole = damageInvestigationConsole;
        this.insuranceDesignConsole = insuranceDesignConsole;
        this.payoutConsole = payoutConsole;
        this.compensationEvaluationConsole = compensationEvaluationConsole;
        this.partnerManagementConsole = partnerManagementConsole;
    }

    // 메뉴 루프 실행
    public void run() {
        view.printSystemBanner();

        while (true) {
            view.printMainMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":  underwritingConsole.run();           break;
                case "2":  endorsementConsole.run();            break;
                case "3":  reinstatementConsole.run();          break;
                case "4":  paymentCollectionConsole.run();      break;
                case "5":  maturityContractConsole.run();       break;
                case "6":  accidentReportConsole.run();         break;
                case "7":  damageInvestigationConsole.run();    break;
                case "8":  insuranceDesignConsole.run();        break;
                case "9":  payoutConsole.run();                 break;
                case "10": compensationEvaluationConsole.run(); break;
                case "11": partnerManagementConsole.run();      break;
                case "12":
                    view.printExitMessage();
                    sc.close();
                    return;
                default:
                    view.printInvalidChoice();
            }
        }
    }
}
