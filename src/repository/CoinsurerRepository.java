package repository;

import model.underwriting.Coinsurer;

import java.util.List;
import java.util.Optional;

// 공동인수사 저장소 인터페이스
public interface CoinsurerRepository {

    Optional<Coinsurer> findByCompanyName(String companyName);

    List<Coinsurer> findAll();

    List<Coinsurer> findApprovableCandidates();

    Coinsurer save(Coinsurer coinsurer);
}
