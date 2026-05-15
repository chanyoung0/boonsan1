package repository;

import model.underwriting.InsuranceApplication;

import java.util.List;
import java.util.Optional;

// 청약 저장소 인터페이스
public interface InsuranceApplicationRepository {

    Optional<InsuranceApplication> findByApplicationId(String applicationId);

    List<InsuranceApplication> findAll();

    InsuranceApplication save(InsuranceApplication application);
}
