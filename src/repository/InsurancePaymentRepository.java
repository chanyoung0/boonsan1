package repository;

import model.accident.InsurancePayment;

import java.util.List;
import java.util.Optional;

// 보험금 지급 저장소 인터페이스
public interface InsurancePaymentRepository {

    Optional<InsurancePayment> findByPaymentId(String paymentId);

    List<InsurancePayment> findAll();

    InsurancePayment save(InsurancePayment payment);
}
