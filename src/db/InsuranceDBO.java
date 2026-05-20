package db;

import model.insurance.AutoInsurance;
import model.insurance.FireInsurance;
import model.insurance.Insurance;
import model.insurance.MarineInsurance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// Insurance entity database operator for insurance CRUD.
public class InsuranceDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "product_code, product_type, insurance_period, insured_amount, premium, maturity_refund, "
                    + "driver_age, vehicle_type, building_type, location, vessel_type, shipping_route";

    public Insurance findByProductCode(String productCode) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insurance WHERE product_code = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapInsurance(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험상품 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Insurance> findAll() {
        List<Insurance> productList = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insurance ORDER BY product_code";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Insurance insurance = mapInsurance(resultSet);
                if (insurance != null) {
                    productList.add(insurance);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험상품 목록 조회 실패: " + e.getMessage());
        }
        return productList;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT product_code FROM insurance ORDER BY product_code";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("product_code"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험상품 코드 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public boolean save(Insurance insurance) {
        String sql = "INSERT INTO insurance "
                + "(product_code, product_type, insurance_period, insured_amount, premium, maturity_refund, "
                + "driver_age, vehicle_type, building_type, location, vessel_type, shipping_route) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setInsuranceParams(statement, insurance);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험상품 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Insurance insurance) {
        String sql = "UPDATE insurance SET "
                + "product_type = ?, insurance_period = ?, insured_amount = ?, premium = ?, maturity_refund = ?, "
                + "driver_age = ?, vehicle_type = ?, building_type = ?, location = ?, vessel_type = ?, shipping_route = ? "
                + "WHERE product_code = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveProductType(insurance));
            statement.setString(2, insurance.getInsurancePeriod());
            statement.setBigDecimal(3, insurance.getInsuredAmount());
            statement.setBigDecimal(4, insurance.getPremium());
            statement.setBigDecimal(5, insurance.getMaturityRefund());
            setSubtypeParams(statement, insurance, 6);
            statement.setString(12, insurance.getProductCode());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험상품 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String productCode) {
        String sql = "DELETE FROM insurance WHERE product_code = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productCode);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험상품 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private Insurance mapInsurance(ResultSet resultSet) throws SQLException {
        String productCode = resultSet.getString("product_code");
        String insurancePeriod = resultSet.getString("insurance_period");
        String productType = resultSet.getString("product_type");

        if ("AUTO".equals(productType)) {
            return new AutoInsurance(
                    productCode,
                    insurancePeriod,
                    resultSet.getBigDecimal("insured_amount"),
                    resultSet.getBigDecimal("premium"),
                    resultSet.getBigDecimal("maturity_refund"),
                    resultSet.getInt("driver_age"),
                    resultSet.getString("vehicle_type")
            );
        }
        if ("FIRE".equals(productType)) {
            return new FireInsurance(
                    productCode,
                    insurancePeriod,
                    resultSet.getBigDecimal("insured_amount"),
                    resultSet.getBigDecimal("premium"),
                    resultSet.getBigDecimal("maturity_refund"),
                    resultSet.getString("building_type"),
                    resultSet.getString("location")
            );
        }
        if ("MARINE".equals(productType)) {
            return new MarineInsurance(
                    productCode,
                    insurancePeriod,
                    resultSet.getBigDecimal("insured_amount"),
                    resultSet.getBigDecimal("premium"),
                    resultSet.getBigDecimal("maturity_refund"),
                    resultSet.getString("vessel_type"),
                    resultSet.getString("shipping_route")
            );
        }
        return null;
    }

    private void setInsuranceParams(PreparedStatement statement, Insurance insurance) throws SQLException {
        statement.setString(1, insurance.getProductCode());
        statement.setString(2, resolveProductType(insurance));
        statement.setString(3, insurance.getInsurancePeriod());
        statement.setBigDecimal(4, insurance.getInsuredAmount());
        statement.setBigDecimal(5, insurance.getPremium());
        statement.setBigDecimal(6, insurance.getMaturityRefund());
        setSubtypeParams(statement, insurance, 7);
    }

    private void setSubtypeParams(PreparedStatement statement, Insurance insurance,
                                  int startIndex) throws SQLException {
        if (insurance instanceof AutoInsurance) {
            AutoInsurance autoInsurance = (AutoInsurance) insurance;
            statement.setInt(startIndex, autoInsurance.getDriverAge());
            statement.setString(startIndex + 1, autoInsurance.getVehicleType());
            statement.setNull(startIndex + 2, Types.VARCHAR);
            statement.setNull(startIndex + 3, Types.VARCHAR);
            statement.setNull(startIndex + 4, Types.VARCHAR);
            statement.setNull(startIndex + 5, Types.VARCHAR);
            return;
        }
        if (insurance instanceof FireInsurance) {
            FireInsurance fireInsurance = (FireInsurance) insurance;
            statement.setNull(startIndex, Types.INTEGER);
            statement.setNull(startIndex + 1, Types.VARCHAR);
            statement.setString(startIndex + 2, fireInsurance.getBuildingType());
            statement.setString(startIndex + 3, fireInsurance.getLocation());
            statement.setNull(startIndex + 4, Types.VARCHAR);
            statement.setNull(startIndex + 5, Types.VARCHAR);
            return;
        }
        if (insurance instanceof MarineInsurance) {
            MarineInsurance marineInsurance = (MarineInsurance) insurance;
            statement.setNull(startIndex, Types.INTEGER);
            statement.setNull(startIndex + 1, Types.VARCHAR);
            statement.setNull(startIndex + 2, Types.VARCHAR);
            statement.setNull(startIndex + 3, Types.VARCHAR);
            statement.setString(startIndex + 4, marineInsurance.getVesselType());
            statement.setString(startIndex + 5, marineInsurance.getShippingRoute());
            return;
        }
        statement.setNull(startIndex, Types.INTEGER);
        statement.setNull(startIndex + 1, Types.VARCHAR);
        statement.setNull(startIndex + 2, Types.VARCHAR);
        statement.setNull(startIndex + 3, Types.VARCHAR);
        statement.setNull(startIndex + 4, Types.VARCHAR);
        statement.setNull(startIndex + 5, Types.VARCHAR);
    }

    private String resolveProductType(Insurance insurance) {
        if (insurance instanceof AutoInsurance) {
            return "AUTO";
        }
        if (insurance instanceof FireInsurance) {
            return "FIRE";
        }
        if (insurance instanceof MarineInsurance) {
            return "MARINE";
        }
        return null;
    }
}
