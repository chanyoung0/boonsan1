package db.mapper;

import model.partner.Partner;

import java.util.List;

// 협력업체 MyBatis Mapper — PartnerDBO가 위임하는 SQL 인터페이스
public interface PartnerMapper {

    Partner findById(String id);

    List<Partner> findAll();

    List<String> findAllIds();

    int insert(Partner partner);

    int update(Partner partner);

    int delete(String id);
}
