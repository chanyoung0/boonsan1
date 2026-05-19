import common.ConsoleNotifier;
import common.IdGenerator;
import common.Notifier;
import common.SequenceIdGenerator;
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
import service.accident.AccidentReportService;
import service.accident.DamageInvestigationService;
import service.compensation.CompensationEvaluationService;
import service.compensation.PartnerManagementService;
import service.contract.EndorsementService;
import service.contract.MaturityContractService;
import service.contract.PaymentCollectionService;
import service.contract.PayoutService;
import service.contract.ReinstatementService;
import service.insurance.InsuranceDesignService;
import service.underwriting.UnderwritingService;
import ui.console.ConsoleView;
import ui.console.InsuranceManagementCli;

import java.time.Clock;

// 보험 관리 시스템 진입점 — 의존성 와이어링 후 CLI 실행
public class Main {

    public static void main(String[] args) {
        // 인프라 (추후 외부 알림/DB 시퀀스 구현체로 교체 가능)
        Clock clock = Clock.systemDefaultZone();
        @SuppressWarnings("unused") IdGenerator idGenerator = new SequenceIdGenerator(clock);
        @SuppressWarnings("unused") Notifier notifier = new ConsoleNotifier();

        // 서비스 (각 유스케이스 비즈니스 로직)
        UnderwritingService underwritingService = new UnderwritingService();
        EndorsementService endorsementService = new EndorsementService();
        ReinstatementService reinstatementService = new ReinstatementService();
        PaymentCollectionService paymentCollectionService = new PaymentCollectionService();
        MaturityContractService maturityContractService = new MaturityContractService();
        AccidentReportService accidentReportService = new AccidentReportService();
        DamageInvestigationService damageInvestigationService = new DamageInvestigationService();
        InsuranceDesignService insuranceDesignService = new InsuranceDesignService();
        PayoutService payoutService = new PayoutService();
        CompensationEvaluationService compensationEvaluationService = new CompensationEvaluationService();
        PartnerManagementService partnerManagementService = new PartnerManagementService();

        // 콘솔 (유스케이스별 I/O 전담)
        UnderwritingConsole underwritingConsole = new UnderwritingConsole(underwritingService);
        EndorsementConsole endorsementConsole = new EndorsementConsole(endorsementService);
        ReinstatementConsole reinstatementConsole = new ReinstatementConsole(reinstatementService);
        PaymentCollectionConsole paymentCollectionConsole = new PaymentCollectionConsole(paymentCollectionService);
        MaturityContractConsole maturityContractConsole = new MaturityContractConsole(maturityContractService);
        AccidentReportConsole accidentReportConsole = new AccidentReportConsole(accidentReportService);
        DamageInvestigationConsole damageInvestigationConsole = new DamageInvestigationConsole(damageInvestigationService);
        InsuranceDesignConsole insuranceDesignConsole = new InsuranceDesignConsole(insuranceDesignService);
        PayoutConsole payoutConsole = new PayoutConsole(payoutService);
        CompensationEvaluationConsole compensationEvaluationConsole = new CompensationEvaluationConsole(compensationEvaluationService);
        PartnerManagementConsole partnerManagementConsole = new PartnerManagementConsole(partnerManagementService);

        // 콘솔 진입점
        ConsoleView view = new ConsoleView();
        InsuranceManagementCli cli = new InsuranceManagementCli(view,
                underwritingConsole, endorsementConsole, reinstatementConsole,
                paymentCollectionConsole, maturityContractConsole,
                accidentReportConsole, damageInvestigationConsole,
                insuranceDesignConsole, payoutConsole,
                compensationEvaluationConsole, partnerManagementConsole);
        cli.run();
    }
}
