package db;

import model.insurance.Insurance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 보험 상품 MyBatis Mapper
public interface InsuranceMapper {

    Insurance findByProductCode(String productCode);

    List<Insurance> findAll();

    List<String> findAllIds();

    int insert(@Param("ins") Insurance insurance,
               @Param("productType") String productType,
               @Param("driverAge") Integer driverAge,
               @Param("vehicleType") String vehicleType,
               @Param("buildingType") String buildingType,
               @Param("location") String location,
               @Param("vesselType") String vesselType,
               @Param("shippingRoute") String shippingRoute);

    int update(@Param("ins") Insurance insurance,
               @Param("productType") String productType,
               @Param("driverAge") Integer driverAge,
               @Param("vehicleType") String vehicleType,
               @Param("buildingType") String buildingType,
               @Param("location") String location,
               @Param("vesselType") String vesselType,
               @Param("shippingRoute") String shippingRoute);

    int delete(String productCode);
}
