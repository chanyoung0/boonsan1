package db.mapper;

import model.insurance.Authorization;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 상품 인가 MyBatis Mapper — AuthorizationDBO가 위임하는 SQL 인터페이스
public interface AuthorizationMapper {

    Authorization findById(String requestId);

    List<Authorization> findAll();

    int insert(@Param("auth") Authorization authorization,
               @Param("productCode") String productCode);

    int update(@Param("auth") Authorization authorization,
               @Param("productCode") String productCode,
               @Param("includeProductCode") boolean includeProductCode);

    int delete(String requestId);
}
