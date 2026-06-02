package product.dto;

import enums.ProductStatus;

public class AuthorizationEligibilityResponse {

    private String productCode;
    private String productName;
    private String insuranceTypeCode;
    private ProductStatus productStatus;
    private boolean eligible;
    private String message;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getInsuranceTypeCode() { return insuranceTypeCode; }
    public void setInsuranceTypeCode(String insuranceTypeCode) { this.insuranceTypeCode = insuranceTypeCode; }

    public ProductStatus getProductStatus() { return productStatus; }
    public void setProductStatus(ProductStatus productStatus) { this.productStatus = productStatus; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
