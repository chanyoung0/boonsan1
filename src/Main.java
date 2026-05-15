import common.ConsoleNotifier;
import common.IdGenerator;
import common.Notifier;
import common.SequenceIdGenerator;
import repository.AccidentHistoryRepository;
import repository.AccidentReportRepository;
import repository.AccountRepository;
import repository.CoinsurerRepository;
import repository.ContractRepository;
import repository.DamageInvestigationRepository;
import repository.InsuranceApplicationRepository;
import repository.InsurancePaymentRepository;
import repository.InsuredPersonRepository;
import repository.PartnerRepository;
import repository.inmemory.InMemoryAccidentHistoryRepository;
import repository.inmemory.InMemoryAccidentReportRepository;
import repository.inmemory.InMemoryAccountRepository;
import repository.inmemory.InMemoryCoinsurerRepository;
import repository.inmemory.InMemoryContractRepository;
import repository.inmemory.InMemoryDamageInvestigationRepository;
import repository.inmemory.InMemoryInsuranceApplicationRepository;
import repository.inmemory.InMemoryInsurancePaymentRepository;
import repository.inmemory.InMemoryInsuredPersonRepository;
import repository.inmemory.InMemoryPartnerRepository;
import service.accident.AccidentReportService;
import service.accident.DamageInvestigationService;
import service.contract.EndorsementService;
import service.contract.MaturityContractService;
import service.contract.PaymentCollectionService;
import service.contract.ReinstatementService;
import service.underwriting.UnderwritingService;
import ui.console.ConsoleView;
import ui.console.InsuranceManagementCli;

import java.time.Clock;

// 보험 관리 시스템 진입점 — 의존성 와이어링 후 CLI 실행
public class Main {

    public static void main(String[] args) {
        // 인프라
        Clock clock = Clock.systemDefaultZone();
        IdGenerator idGenerator = new SequenceIdGenerator(clock);
        Notifier notifier = new ConsoleNotifier();

        // 인메모리 저장소 (추후 JPA 구현체로 교체 가능)
        ContractRepository contractRepo = new InMemoryContractRepository();
        InsuranceApplicationRepository applicationRepo = new InMemoryInsuranceApplicationRepository();
        AccidentReportRepository accidentReportRepo = new InMemoryAccidentReportRepository();
        InsuredPersonRepository insuredPersonRepo = new InMemoryInsuredPersonRepository();
        AccidentHistoryRepository accidentHistoryRepo = new InMemoryAccidentHistoryRepository();
        CoinsurerRepository coinsurerRepo = new InMemoryCoinsurerRepository();
        PartnerRepository partnerRepo = new InMemoryPartnerRepository();
        AccountRepository accountRepo = new InMemoryAccountRepository();
        DamageInvestigationRepository damageInvestigationRepo = new InMemoryDamageInvestigationRepository();
        InsurancePaymentRepository insurancePaymentRepo = new InMemoryInsurancePaymentRepository();

        // 사용 안 함 경고 회피용 참조 (DB 전환 시 실제 서비스 의존성으로 확장 예정)
        @SuppressWarnings("unused") AccountRepository _accountRefHolder = accountRepo;
        @SuppressWarnings("unused") InsurancePaymentRepository _paymentRefHolder = insurancePaymentRepo;

        // 서비스
        UnderwritingService underwritingService = new UnderwritingService(
                applicationRepo, insuredPersonRepo, coinsurerRepo, accidentHistoryRepo, idGenerator);
        EndorsementService endorsementService = new EndorsementService(contractRepo, idGenerator);
        ReinstatementService reinstatementService = new ReinstatementService(contractRepo, idGenerator);
        PaymentCollectionService paymentCollectionService = new PaymentCollectionService(contractRepo, notifier);
        MaturityContractService maturityContractService = new MaturityContractService(contractRepo);
        AccidentReportService accidentReportService = new AccidentReportService(accidentReportRepo, idGenerator);
        DamageInvestigationService damageInvestigationService = new DamageInvestigationService(
                accidentReportRepo, damageInvestigationRepo, partnerRepo, idGenerator, notifier);

        // 콘솔 진입점
        ConsoleView view = new ConsoleView();
        InsuranceManagementCli cli = new InsuranceManagementCli(view,
                underwritingService, endorsementService, reinstatementService,
                paymentCollectionService, maturityContractService,
                accidentReportService, damageInvestigationService);
        cli.run();
    }
}
