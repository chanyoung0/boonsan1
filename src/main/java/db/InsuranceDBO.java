package db;

import db.mapper.InsuranceMapper;
import db.mybatis.MyBatisSessionFactory;
import model.insurance.AutoInsurance;
import model.insurance.FireInsurance;
import model.insurance.Insurance;
import model.insurance.MarineInsurance;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

// Insurance 엔티티 DB 매핑 — insurance 테이블 CRUD 담당 (MyBatis 위임)
public class InsuranceDBO extends DBA {

    public Insurance findByProductCode(String productCode) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceMapper.class).findByProductCode(productCode);
        } catch (Exception e) {
            System.out.println("[DB 오류] 보험상품 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public List<Insurance> findAll() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 보험상품 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> findAllIds() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceMapper.class).findAllIds();
        } catch (Exception e) {
            System.out.println("[DB 오류] 보험상품 코드 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean save(Insurance insurance) {
        if (insurance == null) {
            return false;
        }
        SubtypeParams params = resolveSubtypeParams(insurance);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceMapper.class)
                    .insert(insurance, params.productType,
                            params.driverAge, params.vehicleType,
                            params.buildingType, params.location,
                            params.vesselType, params.shippingRoute) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보험상품 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Insurance insurance) {
        if (insurance == null) {
            return false;
        }
        SubtypeParams params = resolveSubtypeParams(insurance);
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceMapper.class)
                    .update(insurance, params.productType,
                            params.driverAge, params.vehicleType,
                            params.buildingType, params.location,
                            params.vesselType, params.shippingRoute) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보험상품 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String productCode) {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(InsuranceMapper.class).delete(productCode) > 0;
        } catch (Exception e) {
            System.out.println("[DB 오류] 보험상품 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private SubtypeParams resolveSubtypeParams(Insurance insurance) {
        SubtypeParams params = new SubtypeParams();
        if (insurance instanceof AutoInsurance) {
            AutoInsurance auto = (AutoInsurance) insurance;
            params.productType = "AUTO";
            params.driverAge = auto.getDriverAge();
            params.vehicleType = auto.getVehicleType();
            return params;
        }
        if (insurance instanceof FireInsurance) {
            FireInsurance fire = (FireInsurance) insurance;
            params.productType = "FIRE";
            params.buildingType = fire.getBuildingType();
            params.location = fire.getLocation();
            return params;
        }
        if (insurance instanceof MarineInsurance) {
            MarineInsurance marine = (MarineInsurance) insurance;
            params.productType = "MARINE";
            params.vesselType = marine.getVesselType();
            params.shippingRoute = marine.getShippingRoute();
            return params;
        }
        return params;
    }

    private static final class SubtypeParams {
        String productType;
        Integer driverAge;
        String vehicleType;
        String buildingType;
        String location;
        String vesselType;
        String shippingRoute;
    }
}
