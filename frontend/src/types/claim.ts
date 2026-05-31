export type AccidentType = 'VEHICLE' | 'INJURY' | 'PROPERTY' | 'FIRE' | 'ETC';

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
}

export interface FieldInvestigationMaterialResponse {
  accidentScenePhotoName: string;
  blackBoxVideoName: string;
  repairEstimateFileName: string;
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

export const ACCIDENT_TYPE_LABELS: Record<AccidentType, string> = {
  VEHICLE: '차량 사고',
  INJURY: '상해 사고',
  PROPERTY: '재물 손해',
  FIRE: '화재 사고',
  ETC: '기타'
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

export function getAccidentTypeLabel(type: AccidentType) {
  return ACCIDENT_TYPE_LABELS[type] ?? type;
}

export function getAccidentStatusLabel(status: AccidentStatus) {
  return ACCIDENT_STATUS_LABELS[status] ?? status;
}
