package product.mapper;

import model.insurance.Insurance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    void insertProduct(Insurance insurance);

    Insurance findByProductCode(@Param("productCode") String productCode);

    List<Insurance> findAll();

    int updateProductStatus(@Param("productCode") String productCode,
                            @Param("productStatus") String productStatus);
}
