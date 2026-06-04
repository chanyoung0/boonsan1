export type AccidentType = 'VEHICLE' | 'INJURY' | 'PROPERTY' | 'FIRE' | 'ETC' | 'PERSONAL_INJURY' | 'NATURAL_DISASTER';

export type AccidentStatus =
  | 'RECEIVED'
  | 'INVESTIGATING'
  | 'PAYMENT_REVIEW'
  | 'COMPLETED'
  | 'DOCUMENT_PENDING'
  | 'FIELD_INVESTIGATION_REQUIRED'
  | 'APPROVAL_REQUIRED'
  | 'REJECTED'
  | 'FRAUD_INVESTIGATION'
  | 'REINVESTIGATION_REQUIRED'
  | 'TRANSFERRED_TO_LEGAL'
  | 'OUTSOURCED_INVESTIGATION'
  | 'TEMP_SAVED';

export type ClaimAlternativeActionType =
  | 'INVESTIGATION_REJECTED'
  | 'FRAUD_INVESTIGATION_REQUESTED'
  | 'OUTSOURCE_REQUESTED'
  | 'OUTSOURCE_INVESTIGATION_REQUESTED'
  | 'OUTSOURCE_INVESTIGATION_COMPLETED';

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  message: string;
  errorCode: string | null;
}

export interface AccidentReportCreateRequest {
  policyNumber: string;
  accidentAt: string;
  accidentDescription: string;
  damageDetails: string;
  accidentType: AccidentType;
  accidentReportDocumentName: string | null;
  medicalCertificateFileName: string | null;
  claimDocumentName: string | null;
  submitDocumentsLater?: boolean;
}

export interface AccidentReportResponse {
  accidentNumber: string;
  policyNumber: string;
  accidentAt: string;
  accidentDescription: string;
  damageDetails: string;
  accidentType: AccidentType;
  accidentStatus: AccidentStatus;
  accidentReportDocumentName: string | null;
  medicalCertificateFileName: string | null;
  claimDocumentName: string | null;
  documentSubmissionDeadline?: string | null;
  createdAt?: string;
}

export interface DamageInvestigationStartResponse {
  accidentNumber: string;
  policyNumber: string;
  accidentAt: string;
  accidentDescription: string;
  damageDetails: string;
  accidentType: AccidentType;
  accidentStatus: AccidentStatus;
  accidentReportDocumentName: string | null;
  medicalCertificateFileName: string | null;
  claimDocumentName: string | null;
  documentSubmissionDeadline?: string | null;
}

export interface FieldInvestigationMaterialResponse {
  accidentScenePhotoName: string;
  blackBoxVideoName: string;
  repairEstimateFileName: string;
}

export interface DamageInvestigationRejectRequest {
  employeeNo: string;
  rejectionReason: string;
}

export interface FraudInvestigationRequest {
  employeeNo: string;
  confirmation: string;
}

export interface OutsourceInvestigationRequest {
  employeeNo: string;
  partnerName: string;
  materialChecklist: string;
  requestDetails: string;
}

export interface ClaimAlternativeFlowResponse {
  actionId: string;
  accidentNumber: string;
  actionType: ClaimAlternativeActionType;
  employeeNo: string;
  reason: string | null;
  partnerName: string | null;
  materialChecklist: string | null;
  resultMessage: string;
  createdAt: string;
  completedAt: string | null;
}

export interface DamageAssessmentRequest {
  accidentNumber: string;
  adjusterId: string;
  investigationAt: string;
  medicalExpense: number;
  lostIncome: number;
  repairCost: number;
  settlementAmount: number;
  faultRatio: number;
}

export interface PaymentApprovalDraftResponse {
  accidentNumber: string;
  totalDamageAmount: number;
  faultRatio: number;
  calculatedPaymentAmount: number;
  medicalExpense: number;
  lostIncome: number;
  repairCost: number;
  settlementAmount: number;
  draftMessage: string;
}

export interface AdjusterOpinionRequest {
  accidentNumber: string;
  faultRatioOpinion: string;
  adjusterOpinion: string;
}

export interface InvestigationApprovalRequest {
  accidentNumber: string;
  employeeNo: string;
}

export interface PaymentApprovalDocumentResponse {
  documentId: string;
  accidentNumber: string;
  investigationId: string;
  documentType: string;
  submissionStatus: string;
  totalDamageAmount: number;
  faultRatio: number;
  calculatedPaymentAmount: number;
  faultRatioOpinion: string | null;
  adjusterOpinion: string | null;
  employeeNo: string | null;
  accidentStatus: AccidentStatus;
  createdAt: string;
  submittedAt: string | null;
}

export interface DamageInvestigationResultResponse extends PaymentApprovalDocumentResponse {
  adjusterId: string;
  investigationAt: string;
  medicalExpense: number;
  lostIncome: number;
  repairCost: number;
  settlementAmount: number;
}

export type SubrogationStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'OBJECTED' | 'CLOSED';

export interface SubrogationEligibilityResponse {
  accidentNumber: string;
  documentId: string | null;
  investigationId: string | null;
  paidAmount: number | null;
  paymentStatus: string | null;
  accidentStatus: AccidentStatus | null;
  eligible: boolean;
  message: string;
}

export interface SubrogationCreateRequest {
  targetName: string;
  subrogationReason: string;
  subrogationAmount: number;
  employeeNo: string;
}

export interface SubrogationCompleteRequest {
  recoveredAmount: number;
}

export interface SubrogationResponse {
  subrogationId: string;
  accidentNumber: string;
  documentId: string;
  investigationId: string;
  targetName: string;
  subrogationReason: string;
  subrogationAmount: number;
  employeeNo: string;
  subrogationStatus: SubrogationStatus;
  paidAmount: number;
  paymentStatus: string;
  accidentStatus: AccidentStatus;
  recoveredAmount: number | null;
  recoveredAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export type ObjectionStatus =
  | 'RECEIVED'
  | 'REINVESTIGATION_REQUIRED'
  | 'REJECTED'
  | 'TRANSFERRED_TO_LEGAL'
  | 'COMPLETED';

export interface ObjectionEligibilityResponse {
  accidentNumber: string;
  accidentStatus: AccidentStatus;
  documentId: string | null;
  paymentStatus: string | null;
  finalPaymentAmount: number | null;
  eligible: boolean;
  unavailableReason: string | null;
}

export interface ObjectionCreateRequest {
  claimantName: string;
  claimantPhone: string;
  objectionReason: string;
  requestedAction: string;
  employeeNo: string;
}

export interface ObjectionResponse {
  objectionId: string;
  accidentNumber: string;
  accidentStatus: AccidentStatus;
  documentId: string | null;
  paymentStatus: string | null;
  finalPaymentAmount: number | null;
  claimantName: string;
  claimantPhone: string;
  objectionReason: string;
  requestedAction: string;
  employeeNo: string;
  objectionStatus: ObjectionStatus;
  createdAt: string;
  updatedAt: string;
  reviewedAt: string | null;
  completedAt: string | null;
}

export const ACCIDENT_TYPE_LABELS: Record<AccidentType, string> = {
  VEHICLE: '차량 사고',
  INJURY: '상해 사고',
  PROPERTY: '재물 손해',
  FIRE: '화재 사고',
  ETC: '기타',
  PERSONAL_INJURY: '인적 피해',
  NATURAL_DISASTER: '자연재해'
};

export const ACCIDENT_STATUS_LABELS: Record<AccidentStatus, string> = {
  RECEIVED: '접수 완료',
  INVESTIGATING: '손해조사 중',
  PAYMENT_REVIEW: '지급 심사 중',
  COMPLETED: '종결',
  DOCUMENT_PENDING: '서류 보완 필요',
  FIELD_INVESTIGATION_REQUIRED: '현장 조사 필요',
  APPROVAL_REQUIRED: '결재 필요',
  REJECTED: '반려',
  FRAUD_INVESTIGATION: '보험 사기 조사',
  REINVESTIGATION_REQUIRED: '재조사 필요',
  TRANSFERRED_TO_LEGAL: '법률과 이관',
  OUTSOURCED_INVESTIGATION: '위탁 조사',
  TEMP_SAVED: '임시저장'
};

export const CLAIM_ALTERNATIVE_ACTION_LABELS: Record<ClaimAlternativeActionType, string> = {
  INVESTIGATION_REJECTED: '보험 처리 반려',
  FRAUD_INVESTIGATION_REQUESTED: '보험사기 조사 요청',
  OUTSOURCE_REQUESTED: '손해조사 위탁 요청',
  OUTSOURCE_INVESTIGATION_REQUESTED: '손해조사 위탁 요청',
  OUTSOURCE_INVESTIGATION_COMPLETED: '손해조사 위탁 완료'
};

export function getAccidentTypeLabel(type: AccidentType) {
  return ACCIDENT_TYPE_LABELS[type] ?? type;
}

export function getAccidentStatusLabel(status: AccidentStatus) {
  return ACCIDENT_STATUS_LABELS[status] ?? status;
}

export function getClaimAlternativeActionLabel(actionType: ClaimAlternativeActionType) {
  return CLAIM_ALTERNATIVE_ACTION_LABELS[actionType] ?? actionType;
}
