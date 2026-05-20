package db;

import db.mapper.PayoutMapper;
import db.mybatis.MyBatisSessionFactory;
import enums.CalculationBasis;
import enums.PaymentType;
import model.contract.Payout;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// Payout 엔티티 DB 매핑 — payout 테이블 CRUD 담당 (MyBatis 위임)
public class PayoutDBO extends DBA {

    public Payout findById(String payoutId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class).findById(payoutId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<Payout> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 번호 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String findPolicyNumberById(String payoutId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class).findPolicyNumberById(payoutId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 증권번호 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public String findStatusById(String payoutId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            String value = session.getMapper(PayoutMapper.class).findStatusById(payoutId);
            return resolvePayoutStatus(value);
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 상태 조회 실패: " + e.getMessage());
            return "NOT_FOUND";
        }
    }

    public String findIdByPayout(Payout payout) {
        if (payout == null) {
            return null;
        }
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class).findIdByPayout(
                    payout.getProcessor(),
                    resolvePaymentTypeName(payout),
                    resolveCalculationBasisName(payout),
                    payout.getCalculatedAmount(),
                    payout.getDeductionItem(),
                    payout.getFinalPaymentAmount());
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 번호 역조회 실패: " + e.getMessage());
            return null;
        }
    }

    public boolean save(Payout payout) {
        throw new UnsupportedOperationException("payoutId, policyNumber, payoutStatus 파라미터가 필요합니다.");
    }

    public boolean save(Payout payout, String payoutId, String policyNumber, String payoutStatus) {
        if (payout == null || payoutId == null || policyNumber == null) {
            return false;
        }
        String paymentType = resolvePaymentTypeName(payout);
        String calculationBasis = resolveCalculationBasisName(payout);
        String statusName = resolvePayoutStatus(payoutStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class)
                    .insert(payout, payoutId, policyNumber, paymentType, calculationBasis, statusName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Payout payout) {
        throw new UnsupportedOperationException("payoutId, policyNumber, payoutStatus 파라미터가 필요합니다.");
    }

    public boolean update(Payout payout, String payoutId, String policyNumber, String payoutStatus) {
        if (payout == null || payoutId == null || policyNumber == null) {
            return false;
        }
        String paymentType = resolvePaymentTypeName(payout);
        String calculationBasis = resolveCalculationBasisName(payout);
        String statusName = resolvePayoutStatus(payoutStatus);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class)
                    .update(payout, payoutId, policyNumber, paymentType, calculationBasis, statusName) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String payoutId) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(PayoutMapper.class).delete(payoutId) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 제지급금 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private String resolvePaymentTypeName(Payout payout) {
        if (payout == null || payout.getPaymentType() == null) {
            return PaymentType.LUMP_SUM.name();
        }
        return payout.getPaymentType().name();
    }

    private String resolveCalculationBasisName(Payout payout) {
        if (payout == null || payout.getCalculationBasis() == null) {
            return CalculationBasis.MATURITY_REFUND.name();
        }
        return payout.getCalculationBasis().name();
    }

    private String resolvePayoutStatus(String value) {
        if ("APPROVED".equals(value) || "PAID".equals(value) || "CANCELLED".equals(value)) {
            return value;
        }
        return "REGISTERED";
    }
}
