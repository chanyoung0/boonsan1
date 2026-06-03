export type InsuranceTypeCode = 'AUTO' | 'FIRE' | 'MARINE';

export type ProductStatus =
  | 'DESIGN_COMPLETED'
  | 'AUTHORIZATION_REQUESTED'
  | 'AUTHORIZED'
  | 'AUTHORIZATION_REJECTED'
  | 'REVISION_REQUESTED'
  | 'TEMP_SAVED';

export type AuthorizationStatus =
  | 'REQUESTED'
  | 'APPROVED'
  | 'REJECTED'
  | 'REVISION_REQUIRED'
  | 'CANCELLED';

export interface ProductDesignRequest {
  productName: string;
  insuranceTypeCode: InsuranceTypeCode;
  targetCustomer: string | null;
  salesChannel: string | null;
  insurancePeriod: string | null;
  paymentPeriod: string | null;
  insuredAmount: number;
  premium: number | null;
  maturityRefund: number | null;
  mainCoverage: string | null;
  subscriptionConditions: string | null;
  rateInformation: string | null;
  baseRate: number | null;
  riskRate: number | null;
  expectedInterestRate: number | null;
  operatingExpenseRatio: number | null;
  discountSurchargeRate: number | null;
  appliedRate: number | null;
  profitLossEstimate: number | null;
  specialContractInfo: string | null;
  driverAge: number | null;
  vehicleType: string | null;
  buildingType: string | null;
  location: string | null;
  shippingRoute: string | null;
  vesselType: string | null;
}

export interface ProductResponse {
  productCode: string;
  productName: string;
  insuranceTypeCode: InsuranceTypeCode;
  targetCustomer: string | null;
  salesChannel: string | null;
  insurancePeriod: string | null;
  paymentPeriod: string | null;
  insuredAmount: number;
  premium: number | null;
  maturityRefund: number | null;
  mainCoverage: string | null;
  subscriptionConditions: string | null;
  rateInformation: string | null;
  baseRate: number | null;
  riskRate: number | null;
  expectedInterestRate: number | null;
  operatingExpenseRatio: number | null;
  discountSurchargeRate: number | null;
  appliedRate: number | null;
  profitLossEstimate: number | null;
  specialContractInfo: string | null;
  productStatus: ProductStatus;
  driverAge: number | null;
  vehicleType: string | null;
  buildingType: string | null;
  location: string | null;
  shippingRoute: string | null;
  vesselType: string | null;
  createdAt: string;
}

export interface PremiumEstimateRequest {
  insuredAmount: number;
  baseRate: number;
  riskRate: number;
  expectedInterestRate: number;
  operatingExpenseRatio: number;
  discountSurchargeRate: number;
}

export interface PremiumEstimateResponse {
  baseRate: number;
  appliedRate: number;
  estimatedPremium: number;
  profitLossEstimate: number;
}

export interface AuthorizationEligibilityResponse {
  productCode: string;
  productName: string | null;
  insuranceTypeCode: InsuranceTypeCode | null;
  productStatus: ProductStatus | null;
  eligible: boolean;
  message: string;
}

export interface AuthorizationCreateRequest {
  requestReason: string;
  submissionAgencyName: string;
  productDescriptionFileName: string | null;
  termsAndConditionsFileName: string | null;
  rateScheduleFileName: string | null;
  productEvidenceFileName: string | null;
}

export interface AuthorizationRevisionRequest {
  revisionRequest: string;
}

export interface AuthorizationResponse {
  requestId: string;
  productCode: string;
  requestedAt: string;
  approvedAt: string | null;
  isApproved: boolean;
  requestReason: string;
  submissionAgencyName: string;
  authorizationStatus: AuthorizationStatus;
  productStatus: ProductStatus;
  productDescriptionFileName: string | null;
  termsAndConditionsFileName: string | null;
  rateScheduleFileName: string | null;
  productEvidenceFileName: string | null;
  revisionRequest: string | null;
  updatedAt: string;
}

export const INSURANCE_TYPE_LABELS: Record<InsuranceTypeCode, string> = {
  AUTO: '자동차보험',
  FIRE: '화재보험',
  MARINE: '해상보험'
};

export const PRODUCT_STATUS_LABELS: Record<ProductStatus, string> = {
  DESIGN_COMPLETED: '설계 완료',
  AUTHORIZATION_REQUESTED: '인가 요청 중',
  AUTHORIZED: '인가 완료',
  AUTHORIZATION_REJECTED: '인가 불허',
  REVISION_REQUESTED: '보완 요청',
  TEMP_SAVED: '임시 저장'
};

export const AUTHORIZATION_STATUS_LABELS: Record<AuthorizationStatus, string> = {
  REQUESTED: '인가 요청',
  APPROVED: '승인',
  REJECTED: '불허',
  REVISION_REQUIRED: '보완 요청',
  CANCELLED: '취소'
};

export function getInsuranceTypeLabel(type: InsuranceTypeCode) {
  return INSURANCE_TYPE_LABELS[type] ?? type;
}

export function getProductStatusLabel(status: ProductStatus) {
  return PRODUCT_STATUS_LABELS[status] ?? status;
}

export function getAuthorizationStatusLabel(status: AuthorizationStatus) {
  return AUTHORIZATION_STATUS_LABELS[status] ?? status;
}
