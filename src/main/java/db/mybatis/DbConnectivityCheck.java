package db.mybatis;

import db.mapper.AccidentReportMapper;
import db.mapper.AuthorizationMapper;
import db.mapper.CompensationEvaluationMapper;
import db.mapper.DamageInvestigationMapper;
import db.mapper.EndorsementMapper;
import db.mapper.InsuranceApplicationMapper;
import db.mapper.InsuranceMapper;
import db.mapper.MaturityNoticeMapper;
import db.mapper.PartnerMapper;
import db.mapper.PaymentCollectionMapper;
import db.mapper.PayoutMapper;
import db.mapper.ReinstatementMapper;
import db.mapper.UnderwritingMapper;
import enums.EvaluationGrade;
import model.partner.Partner;
import org.apache.ibatis.session.SqlSession;

// 실제 DB 연결 + 각 Mapper read 경로 + Partner CRUD 한 사이클 검증
public class DbConnectivityCheck {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Read 경로 검증 (각 Mapper.findAll 호출) ===");
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            check("PartnerMapper.findAll",                () -> s.getMapper(PartnerMapper.class).findAll().size());
            check("UnderwritingMapper.findAll",           () -> s.getMapper(UnderwritingMapper.class).findAll().size());
            check("InsuranceApplicationMapper.findAll",   () -> s.getMapper(InsuranceApplicationMapper.class).findAll().size());
            check("EndorsementMapper.findAll",            () -> s.getMapper(EndorsementMapper.class).findAll().size());
            check("ReinstatementMapper.findAll",          () -> s.getMapper(ReinstatementMapper.class).findAll().size());
            check("PaymentCollectionMapper.findAll",      () -> s.getMapper(PaymentCollectionMapper.class).findAll().size());
            check("MaturityNoticeMapper.findAll",         () -> s.getMapper(MaturityNoticeMapper.class).findAll().size());
            check("AccidentReportMapper.findAll",         () -> s.getMapper(AccidentReportMapper.class).findAll().size());
            check("DamageInvestigationMapper.findAll",    () -> s.getMapper(DamageInvestigationMapper.class).findAll().size());
            check("InsuranceMapper.findAll",              () -> s.getMapper(InsuranceMapper.class).findAll().size());
            check("AuthorizationMapper.findAll",          () -> s.getMapper(AuthorizationMapper.class).findAll().size());
            check("PayoutMapper.findAll",                 () -> s.getMapper(PayoutMapper.class).findAll().size());
            check("CompensationEvaluationMapper.findAll", () -> s.getMapper(CompensationEvaluationMapper.class).findAll().size());
        }

        System.out.println();
        System.out.println("=== Write 경로 검증 (Partner CRUD 한 사이클) ===");
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            PartnerMapper m = s.getMapper(PartnerMapper.class);
            String id = "P_TEST_" + System.currentTimeMillis();
            Partner p = new Partner(id, "테스트협력사", "손해사정", "010-1111-2222",
                    "차량 사고 조사", EvaluationGrade.GOOD);

            check("insert", () -> m.insert(p));
            check("findById (insert 후 조회)", () -> {
                Partner r = m.findById(id);
                if (r == null) throw new RuntimeException("inserted row not found");
                if (!"테스트협력사".equals(r.getPartnerName())) throw new RuntimeException("partnerName mismatch: " + r.getPartnerName());
                if (r.getEvaluationGrade() != EvaluationGrade.GOOD) throw new RuntimeException("evaluationGrade mismatch: " + r.getEvaluationGrade());
                return 1;
            });
            check("update", () -> {
                p.setPartnerName("수정된협력사");
                return m.update(p);
            });
            check("findById (update 후 재조회)", () -> {
                Partner r = m.findById(id);
                if (!"수정된협력사".equals(r.getPartnerName())) throw new RuntimeException("update not reflected: " + r.getPartnerName());
                return 1;
            });
            check("delete", () -> m.delete(id));
            check("findById (delete 후 null 확인)", () -> {
                Partner r = m.findById(id);
                if (r != null) throw new RuntimeException("row still exists after delete");
                return 0;
            });
        }

        System.out.println();
        System.out.println("=== 결과: passed=" + passed + ", failed=" + failed + " ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    @FunctionalInterface
    interface CheckOp { int run() throws Exception; }

    private static void check(String label, CheckOp op) {
        try {
            int n = op.run();
            System.out.println("  [OK] " + label + " (n=" + n + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("  [FAIL] " + label + " — " + e.getClass().getSimpleName() + ": " + e.getMessage());
            failed++;
        }
    }
}
