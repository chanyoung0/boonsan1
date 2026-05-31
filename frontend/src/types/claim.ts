export type AccidentType = 'VEHICLE' | 'INJURY' | 'PROPERTY' | 'FIRE' | 'ETC';

export type AccidentStatus =
  | 'RECEIVED'
  | 'INVESTIGATING'
  | 'PAYMENT_REVIEW'
  | 'COMPLETED'
  | 'REJECTED';

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
  REJECTED: '반려'
};

export function getAccidentTypeLabel(type: AccidentType) {
  return ACCIDENT_TYPE_LABELS[type] ?? type;
}

export function getAccidentStatusLabel(status: AccidentStatus) {
  return ACCIDENT_STATUS_LABELS[status] ?? status;
}
