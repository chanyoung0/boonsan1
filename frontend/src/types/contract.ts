export type ContractStatus = 'ACTIVE' | 'TERMINATED' | 'SUSPENDED' | 'MATURED' | 'EXPIRED' | 'PENDING';

export type PaymentCycle = 'MONTHLY' | 'QUARTERLY' | 'SEMI_ANNUALLY' | 'ANNUALLY';

export interface ContractResponse {
  policyNumber: string;
  productCode: string;
  contractStatus: ContractStatus;
  paymentCycle: PaymentCycle;
  premiumAmount: number;
  installmentCount: number;
  hasUnpaidPremium: boolean;
  contractStartDate: string;
  contractEndDate: string;
  insuredName: string;
  insuredRrn: string;
  insuredContact: string;
  accountNumber: string | null;
  accountBank: string | null;
  insuredAmount: number | null;
  specialContractList: string | null;
  maturityRefundAmount: number;
  createdAt: string;
}

export const CONTRACT_STATUS_LABELS: Record<ContractStatus, string> = {
  ACTIVE: '유효',
  TERMINATED: '해지',
  SUSPENDED: '실효',
  MATURED: '만기',
  EXPIRED: '만기종료',
  PENDING: '대기'
};

export const PAYMENT_CYCLE_LABELS: Record<PaymentCycle, string> = {
  MONTHLY: '월납',
  QUARTERLY: '분기납',
  SEMI_ANNUALLY: '반기납',
  ANNUALLY: '연납'
};

export function getContractStatusLabel(status: ContractStatus) {
  return CONTRACT_STATUS_LABELS[status] ?? status;
}

export function getPaymentCycleLabel(cycle: PaymentCycle) {
  return PAYMENT_CYCLE_LABELS[cycle] ?? cycle;
}

export interface MaturityNoticeResponse {
  policyNumber: string;
  insuredName: string;
  insuredContact: string;
  contractEndDate: string;
  contractStatus: ContractStatus;
  daysUntilMaturity: number;
  noticeMessage: string;
  deliveryMethod: string;
  maturityRefundAmount: number;
  sentAt: string | null;
  renewalIntention: boolean | null;
  renewalCheckedAt: string | null;
}

export interface MaturityProcessResponse {
  policyNumber: string;
  previousStatus: ContractStatus;
  contractStatus: ContractStatus;
  contractEndDate: string;
  processedAt: string;
  message: string;
}

export interface MaturityTargetResponse {
  policyNumber: string;
  insuredName: string;
  insuredContact: string;
  contractStartDate: string;
  contractEndDate: string;
  contractStatus: ContractStatus;
  insuredAmount: number | null;
  maturityRefundAmount: number;
  daysUntilMaturity: number;
  maturityTiming: 'DUE' | 'UPCOMING';
  noticeSentAt: string | null;
  renewalIntention: boolean | null;
  renewalCheckedAt: string | null;
}

export interface MaturityRenewalResponse {
  policyNumber: string;
  renewalIntention: boolean;
  contractStatus: ContractStatus;
  checkedAt: string;
  message: string;
}

export type CalculationBasis = 'MATURITY_REFUND' | 'MID_SURRENDER' | 'SURRENDER' | 'DIVIDEND';

export type PaymentType = 'LUMP_SUM' | 'INSTALLMENT' | 'LOAN_SETTLEMENT';

export type PayoutStatus = 'CALCULATED' | 'APPROVED' | 'PAID' | 'CANCELLED';

export interface PayoutCreateRequest {
  calculationBasis: CalculationBasis;
  paymentType: PaymentType;
  paidPremiumAmount: number;
  deductionItem: string | null;
  deductionAmount: number;
}

export interface PayoutApproveRequest {
  processor: string;
}

export interface PayoutResponse {
  payoutId: string;
  policyNumber: string;
  calculationBasis: CalculationBasis;
  paymentType: PaymentType;
  paidPremiumAmount: number;
  refundRate: number;
  calculatedAmount: number;
  deductionItem: string | null;
  deductionAmount: number;
  finalPaymentAmount: number;
  processor: string | null;
  payoutStatus: PayoutStatus;
  createdAt: string;
  approvedAt: string | null;
  paidAt: string | null;
  cancelledAt: string | null;
}

export const CALCULATION_BASIS_LABELS: Record<CalculationBasis, string> = {
  MATURITY_REFUND: '만기환급금',
  MID_SURRENDER: '중도해지환급금',
  SURRENDER: '해지환급금',
  DIVIDEND: '배당금'
};

export const PAYMENT_TYPE_LABELS: Record<PaymentType, string> = {
  LUMP_SUM: '일시지급',
  INSTALLMENT: '분할지급',
  LOAN_SETTLEMENT: '대출 상계'
};

export const PAYOUT_STATUS_LABELS: Record<PayoutStatus, string> = {
  CALCULATED: '산정 완료',
  APPROVED: '승인',
  PAID: '지급 완료',
  CANCELLED: '취소'
};

export function getCalculationBasisLabel(basis: CalculationBasis) {
  return CALCULATION_BASIS_LABELS[basis] ?? basis;
}

export function getPaymentTypeLabel(type: PaymentType) {
  return PAYMENT_TYPE_LABELS[type] ?? type;
}

export function getPayoutStatusLabel(status: PayoutStatus) {
  return PAYOUT_STATUS_LABELS[status] ?? status;
}

export type PaymentMethod = 'AUTO_TRANSFER' | 'BANK_TRANSFER' | 'CREDIT_CARD' | 'VISIT_COLLECTION';

export type ProcessingResult = 'SUCCESS' | 'FAILED' | 'PENDING' | 'PARTIAL';

export type TransferType = 'VISIT_COLLECTION' | 'CANCELLATION' | 'DEPARTMENT_CHANGE';

export interface PaymentCollectionCreateRequest {
  installmentNo: number;
  dueDate: string;
  plannedAmount: number;
  collectedAmount: number;
  paymentMethod: PaymentMethod;
}

export interface PaymentCollectionTransferRequest {
  transferType: TransferType;
}

export interface PaymentCollectionResponse {
  collectionId: string;
  policyNumber: string;
  installmentNo: number;
  dueDate: string;
  plannedAmount: number;
  collectedAmount: number;
  unpaidAmount: number;
  lateFee: number;
  paymentMethod: PaymentMethod;
  processingResult: ProcessingResult;
  collectedAt: string;
  transferType: TransferType | null;
  transferredAt: string | null;
  createdAt: string;
}

export interface PaymentCollectionTargetResponse {
  policyNumber: string;
  insuredName: string;
  installmentNo: number;
  dueDate: string;
  plannedAmount: number;
  accountNumber: string | null;
  accountBank: string | null;
}

export interface PaymentCollectionBatchRequest {
  policyNumbers: string[];
}

export interface PaymentCollectionBatchResponse {
  targetCount: number;
  successCount: number;
  failureCount: number;
  totalCollectedAmount: number;
  results: PaymentCollectionResponse[];
}

export interface PaymentCollectionTransferTargetResponse {
  policyNumber: string;
  insuredName: string;
  unpaidInstallmentCount: number;
  unpaidAmount: number;
}

export interface UnpaidNoticeResponse {
  collectionId: string;
  policyNumber: string;
  insuredName: string;
  insuredContact: string;
  installmentNo: number;
  dueDate: string;
  daysOverdue: number;
  unpaidAmount: number;
  lateFee: number;
  totalAmountDue: number;
  paymentMethod: PaymentMethod;
  processingResult: ProcessingResult;
  noticeMessage: string;
  deliveryMethod: string;
}

export const PAYMENT_METHOD_LABELS: Record<PaymentMethod, string> = {
  AUTO_TRANSFER: '자동이체',
  BANK_TRANSFER: '계좌이체',
  CREDIT_CARD: '카드',
  VISIT_COLLECTION: '방문수금'
};

export const PROCESSING_RESULT_LABELS: Record<ProcessingResult, string> = {
  SUCCESS: '수금 완료',
  FAILED: '미수금',
  PENDING: '대기',
  PARTIAL: '부분 수금'
};

export const TRANSFER_TYPE_LABELS: Record<TransferType, string> = {
  VISIT_COLLECTION: '방문수금 이관',
  CANCELLATION: '해지 이관',
  DEPARTMENT_CHANGE: '부서 이관'
};

export function getPaymentMethodLabel(method: PaymentMethod) {
  return PAYMENT_METHOD_LABELS[method] ?? method;
}

export function getProcessingResultLabel(result: ProcessingResult) {
  return PROCESSING_RESULT_LABELS[result] ?? result;
}

export function getTransferTypeLabel(type: TransferType) {
  return TRANSFER_TYPE_LABELS[type] ?? type;
}

export type ReinstatementReason =
  | 'FINANCIAL_DIFFICULTY'
  | 'OVERSEAS_ABSENCE'
  | 'MEDICAL_EMERGENCY'
  | 'OTHER';

export type ReinstatementStatus = 'APPLIED' | 'UNPAID_SETTLED' | 'COMPLETED' | 'CANCELLED';

export interface ReinstatementCreateRequest {
  reinstatementReason: ReinstatementReason;
  desiredDate: string;
  hasHealthChanged: boolean;
  lastPaidDate: string | null;
  unpaidInstallmentCount?: number;
  premiumPerInstallment?: number;
}

export interface ReinstatementUnpaidSummaryResponse {
  policyNumber: string;
  unpaidInstallmentCount: number;
  premiumPerInstallment: number;
  unpaidPremium: number;
  lastPaidDate: string | null;
}

export interface ReinstatementResponse {
  reinstatementId: string;
  policyNumber: string;
  reinstatementReason: ReinstatementReason;
  desiredDate: string;
  hasHealthChanged: boolean;
  lastPaidDate: string | null;
  unpaidInstallmentCount: number;
  premiumPerInstallment: number;
  unpaidPremium: number;
  reinstatementStatus: ReinstatementStatus;
  underwritingRequestId: string | null;
  appliedAt: string;
  unpaidSettledAt: string | null;
  completedAt: string | null;
  cancelledAt: string | null;
}

export const REINSTATEMENT_REASON_LABELS: Record<ReinstatementReason, string> = {
  FINANCIAL_DIFFICULTY: '경제적 어려움',
  OVERSEAS_ABSENCE: '해외 부재',
  MEDICAL_EMERGENCY: '의료 사유',
  OTHER: '기타'
};

export const REINSTATEMENT_STATUS_LABELS: Record<ReinstatementStatus, string> = {
  APPLIED: '신청 접수',
  UNPAID_SETTLED: '미납 납부 완료',
  COMPLETED: '부활 완료',
  CANCELLED: '취소'
};

export function getReinstatementReasonLabel(reason: ReinstatementReason) {
  return REINSTATEMENT_REASON_LABELS[reason] ?? reason;
}

export function getReinstatementStatusLabel(status: ReinstatementStatus) {
  return REINSTATEMENT_STATUS_LABELS[status] ?? status;
}

export type EndorsementType =
  | 'COVERAGE_CHANGE'
  | 'BENEFICIARY_CHANGE'
  | 'PREMIUM_CHANGE'
  | 'SPECIAL_CONTRACT_CHANGE';

export type ChangeReason =
  | 'INSURED_AMOUNT_CHANGE'
  | 'PAYMENT_CYCLE_CHANGE'
  | 'SPECIAL_CONTRACT_ADD'
  | 'SPECIAL_CONTRACT_REMOVE'
  | 'BENEFICIARY_CHANGE';

export type EndorsementStatus = 'APPLIED' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

export type RequestReason = 'ENDORSEMENT' | 'REINSTATEMENT' | 'RENEWAL' | 'NEW_APPLICATION';

export type RequestStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export type UnderwritingType = 'AUTO' | 'DIAGNOSIS' | 'SPECIAL' | 'GENERAL' | 'IMAGE' | 'FITNESS';

export type UnderwritingResultType = 'APPROVED' | 'SURCHARGE' | 'REJECTED' | 'PENDING';

export type RejectionReason = 'HIGH_RISK' | 'INCOMPLETE_DOCUMENTS' | 'FRAUD_SUSPICION' | 'POLICY_LIMIT';

export type SurchargeCondition =
  | 'NONE'
  | 'HIGH_RISK_OCCUPATION'
  | 'POOR_HEALTH'
  | 'HAZARDOUS_ACTIVITY';

export interface EndorsementCreateRequest {
  endorsementType: EndorsementType;
  changeReason: ChangeReason;
  previousContent: string;
  newContent: string;
}

export interface EndorsementResponse {
  endorsementId: string;
  policyNumber: string;
  endorsementType: EndorsementType;
  changeReason: ChangeReason;
  previousContent: string;
  newContent: string;
  endorsementStatus: EndorsementStatus;
  underwritingRequestId: string | null;
  appliedAt: string;
  approvedAt: string | null;
  rejectedAt: string | null;
  cancelledAt: string | null;
}

export interface UnderwritingRequestCreateRequest {
  underwritingType: UnderwritingType | null;
}

export interface UnderwritingRequestCompleteRequest {
  underwritingResult: UnderwritingResultType;
  surchargeCondition: SurchargeCondition | null;
  rejectionReason: RejectionReason | null;
}

export interface UnderwritingRequestResponse {
  requestId: string;
  policyNumber: string;
  requestReason: RequestReason;
  sourceId: string;
  underwritingType: UnderwritingType | null;
  requestStatus: RequestStatus;
  underwritingResult: UnderwritingResultType | null;
  rejectionReason: RejectionReason | null;
  surchargeCondition: SurchargeCondition | null;
  requestedAt: string;
  completedAt: string | null;
  cancelledAt: string | null;
}

export const ENDORSEMENT_TYPE_LABELS: Record<EndorsementType, string> = {
  COVERAGE_CHANGE: '보장 변경',
  BENEFICIARY_CHANGE: '수익자 변경',
  PREMIUM_CHANGE: '보험료 변경',
  SPECIAL_CONTRACT_CHANGE: '특약 변경'
};

export const CHANGE_REASON_LABELS: Record<ChangeReason, string> = {
  INSURED_AMOUNT_CHANGE: '가입금액 변경',
  PAYMENT_CYCLE_CHANGE: '납입주기 변경',
  SPECIAL_CONTRACT_ADD: '특약 추가',
  SPECIAL_CONTRACT_REMOVE: '특약 삭제',
  BENEFICIARY_CHANGE: '수익자 변경'
};

export const ENDORSEMENT_STATUS_LABELS: Record<EndorsementStatus, string> = {
  APPLIED: '신청 접수',
  APPROVED: '승인',
  REJECTED: '반려',
  CANCELLED: '취소'
};

export const REQUEST_STATUS_LABELS: Record<RequestStatus, string> = {
  PENDING: '심사 중',
  IN_PROGRESS: '진행 중',
  COMPLETED: '심사 완료',
  CANCELLED: '취소'
};

export const UNDERWRITING_TYPE_LABELS: Record<UnderwritingType, string> = {
  AUTO: '자동 심사',
  DIAGNOSIS: '진단 심사',
  SPECIAL: '특별 심사',
  GENERAL: '일반 심사',
  IMAGE: '영상 심사',
  FITNESS: '건강 심사'
};

export const UNDERWRITING_RESULT_LABELS: Record<UnderwritingResultType, string> = {
  APPROVED: '승인',
  SURCHARGE: '할증 인수',
  REJECTED: '거절',
  PENDING: '대기'
};

export const REJECTION_REASON_LABELS: Record<RejectionReason, string> = {
  HIGH_RISK: '고위험',
  INCOMPLETE_DOCUMENTS: '서류 미비',
  FRAUD_SUSPICION: '사기 의심',
  POLICY_LIMIT: '한도 초과'
};

export const SURCHARGE_CONDITION_LABELS: Record<SurchargeCondition, string> = {
  NONE: '해당 없음',
  HIGH_RISK_OCCUPATION: '위험 직업',
  POOR_HEALTH: '건강 이슈',
  HAZARDOUS_ACTIVITY: '위험 활동'
};

export function getEndorsementTypeLabel(type: EndorsementType) {
  return ENDORSEMENT_TYPE_LABELS[type] ?? type;
}

export function getChangeReasonLabel(reason: ChangeReason) {
  return CHANGE_REASON_LABELS[reason] ?? reason;
}

export function getEndorsementStatusLabel(status: EndorsementStatus) {
  return ENDORSEMENT_STATUS_LABELS[status] ?? status;
}

export function getRequestStatusLabel(status: RequestStatus) {
  return REQUEST_STATUS_LABELS[status] ?? status;
}

export function getUnderwritingTypeLabel(type: UnderwritingType) {
  return UNDERWRITING_TYPE_LABELS[type] ?? type;
}

export function getUnderwritingResultLabel(result: UnderwritingResultType) {
  return UNDERWRITING_RESULT_LABELS[result] ?? result;
}
