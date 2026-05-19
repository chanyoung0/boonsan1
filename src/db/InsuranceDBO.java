package db;

import model.insurance.AutoInsurance;
import model.insurance.FireInsurance;
import model.insurance.Insurance;
import model.insurance.MarineInsurance;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// Insurance 엔티티 DB 매핑 — insurance 테이블 CRUD 담당 (단일 테이블, insurance_type으로 AUTO/FIRE/MARINE 구분)
public class InsuranceDBO extends DBA {

    private static final String SELECT_COLS =
            "SELECT product_code, insurance_type, insurance_period, insured_amount, premium, maturity_refund, "
            + "driver_age, vehicle_type, building_type, location, vessel_type, shipping_route "
            + "FROM insurance";

    public Insurance findByProductCode(String productCode) {
        String sql = SELECT_COLS + " WHERE product_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapInsurance(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 상품 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Insurance> findAll() {
        List<Insurance> list = new ArrayList<>();
        String sql = SELECT_COLS + " ORDER BY product_code";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Insurance ins = mapInsurance(rs);
                if (ins != null) {
                    list.add(ins);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 상품 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public boolean save(Insurance insurance) {
        String sql = "INSERT INTO insurance "
                + "(product_code, insurance_type, insurance_period, insured_amount, premium, maturity_refund, "
                + "driver_age, vehicle_type, building_type, location, vessel_type, shipping_route) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, insurance.getProductCode());
            stmt.setString(2, resolveType(insurance));
            stmt.setString(3, insurance.getInsurancePeriod());
            stmt.setBigDecimal(4, insurance.getInsuredAmount());
            stmt.setBigDecimal(5, insurance.getPremium());
            stmt.setBigDecimal(6, insurance.getMaturityRefund());
            setTypeParams(stmt, insurance, 7);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 상품 등록 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Insurance insurance) {
        String sql = "UPDATE insurance "
                + "SET insurance_period=?, insured_amount=?, premium=?, maturity_refund=?, "
                + "driver_age=?, vehicle_type=?, building_type=?, location=?, vessel_type=?, shipping_route=? "
                + "WHERE product_code=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, insurance.getInsurancePeriod());
            stmt.setBigDecimal(2, insurance.getInsuredAmount());
            stmt.setBigDecimal(3, insurance.getPremium());
            stmt.setBigDecimal(4, insurance.getMaturityRefund());
            setTypeParams(stmt, insurance, 5);
            stmt.setString(11, insurance.getProductCode());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 상품 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String productCode) {
        String sql = "DELETE FROM insurance WHERE product_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험 상품 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Insurance mapInsurance(ResultSet rs) throws SQLException {
        String type = rs.getString("insurance_type");
        String productCode = rs.getString("product_code");
        String period = rs.getString("insurance_period");
        BigDecimal insuredAmount = rs.getBigDecimal("insured_amount");
        BigDecimal premium = rs.getBigDecimal("premium");
        BigDecimal maturityRefund = rs.getBigDecimal("maturity_refund");

        switch (type) {
            case "AUTO":
                return new AutoInsurance(productCode, period, insuredAmount, premium, maturityRefund,
                        rs.getInt("driver_age"), rs.getString("vehicle_type"));
            case "FIRE":
                return new FireInsurance(productCode, period, insuredAmount, premium, maturityRefund,
                        rs.getString("building_type"), rs.getString("location"));
            case "MARINE":
                return new MarineInsurance(productCode, period, insuredAmount, premium, maturityRefund,
                        rs.getString("vessel_type"), rs.getString("shipping_route"));
            default:
                return null;
        }
    }

    // start: INSERT=7, UPDATE=5
    private void setTypeParams(PreparedStatement stmt, Insurance ins, int start) throws SQLException {
        if (ins instanceof AutoInsurance) {
            AutoInsurance a = (AutoInsurance) ins;
            stmt.setInt(start,       a.getDriverAge());
            stmt.setString(start + 1, a.getVehicleType());
            stmt.setNull(start + 2, Types.VARCHAR);
            stmt.setNull(start + 3, Types.VARCHAR);
            stmt.setNull(start + 4, Types.VARCHAR);
            stmt.setNull(start + 5, Types.VARCHAR);
        } else if (ins instanceof FireInsurance) {
            FireInsurance f = (FireInsurance) ins;
            stmt.setNull(start,     Types.INTEGER);
            stmt.setNull(start + 1, Types.VARCHAR);
            stmt.setString(start + 2, f.getBuildingType());
            stmt.setString(start + 3, f.getLocation());
            stmt.setNull(start + 4, Types.VARCHAR);
            stmt.setNull(start + 5, Types.VARCHAR);
        } else {
            MarineInsurance m = (MarineInsurance) ins;
            stmt.setNull(start,     Types.INTEGER);
            stmt.setNull(start + 1, Types.VARCHAR);
            stmt.setNull(start + 2, Types.VARCHAR);
            stmt.setNull(start + 3, Types.VARCHAR);
            stmt.setString(start + 4, m.getVesselType());
            stmt.setString(start + 5, m.getShippingRoute());
        }
    }

    private String resolveType(Insurance ins) {
        if (ins instanceof AutoInsurance) return "AUTO";
        if (ins instanceof FireInsurance)  return "FIRE";
        return "MARINE";
    }
}
