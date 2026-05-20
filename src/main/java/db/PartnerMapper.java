package db;

import model.partner.Partner;

import java.util.List;

// 협력업체 MyBatis Mapper
public interface PartnerMapper {

    Partner findById(String id);

    List<Partner> findAll();

    List<String> findAllIds();

    int insert(Partner partner);

    int update(Partner partner);

    int delete(String id);
}
