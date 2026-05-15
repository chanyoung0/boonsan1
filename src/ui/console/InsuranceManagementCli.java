package ui.console;

import service.accident.AccidentReportService;
import service.accident.DamageInvestigationService;
import service.contract.EndorsementService;
import service.contract.MaturityContractService;
import service.contract.PaymentCollectionService;
import service.contract.ReinstatementService;
import service.underwriting.UnderwritingService;

import static common.ConsoleUtil.sc;

// 보험 관리 시스템 콘솔 진입점 — 메뉴 루프 + Service 디스패치
public class InsuranceManagementCli {

    private final ConsoleView view;
    private final UnderwritingService underwritingService;
    private final EndorsementService endorsementService;
    private final ReinstatementService reinstatementService;
    private final PaymentCollectionService paymentCollectionService;
    private final MaturityContractService maturityContractService;
    private final AccidentReportService accidentReportService;
    private final DamageInvestigationService damageInvestigationService;

    // 의존성 주입으로 초기화
    public InsuranceManagementCli(ConsoleView view,
                                  UnderwritingService underwritingService,
                                  EndorsementService endorsementService,
                                  ReinstatementService reinstatementService,
                                  PaymentCollectionService paymentCollectionService,
                                  MaturityContractService maturityContractService,
                                  AccidentReportService accidentReportService,
                                  DamageInvestigationService damageInvestigationService) {
        this.view = view;
        this.underwritingService = underwritingService;
        this.endorsementService = endorsementService;
        this.reinstatementService = reinstatementService;
        this.paymentCollectionService = paymentCollectionService;
        this.maturityContractService = maturityContractService;
        this.accidentReportService = accidentReportService;
        this.damageInvestigationService = damageInvestigationService;
    }

    // 메뉴 루프 실행
    public void run() {
        view.printSystemBanner();

        while (true) {
            view.printMainMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": underwritingService.run();        break;
                case "2": endorsementService.run();         break;
                case "3": reinstatementService.run();       break;
                case "4": paymentCollectionService.run();   break;
                case "5": maturityContractService.run();    break;
                case "6": accidentReportService.run();      break;
                case "7": damageInvestigationService.run(); break;
                case "8":
                    view.printExitMessage();
                    sc.close();
                    return;
                default:
                    view.printInvalidChoice();
            }
        }
    }
}
