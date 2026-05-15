package repository;

import model.partner.Partner;

import java.util.List;
import java.util.Optional;

// 협력업체 저장소 인터페이스
public interface PartnerRepository {

    Optional<Partner> findById(String id);

    List<Partner> findAll();

    Partner save(Partner partner);
}
