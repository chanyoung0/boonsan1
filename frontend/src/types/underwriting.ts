export type UnderwritingResultType = 'APPROVED' | 'SURCHARGE' | 'REJECTED' | 'PENDING';

export interface UnderwritingApplicationCreateRequest {
  productCode: string;
  insuredAmount: number;
  premium: number;
  paymentCycle: string;
  termsVersion: string;
  specialContractList: string | null;
  appliedCondition: string | null;
  insuredPersonName: string;
  age: number;
  gender: string;
  occupation: string;
  annualIncome: number;
  pastMedicalHistory: string | null;
  medicated: boolean;
  surgeryHistory: string | null;
  familyHistory: string | null;
  smoker: boolean;
  alcoholConsumption: string | null;
  bmi: number;
  vehicleModel: string | null;
  vehicleNumber: string | null;
  hasAccidentHistory: boolean;
  hasOtherContract: boolean;
}

export interface UnderwritingApplicationResponse extends UnderwritingApplicationCreateRequest {
  applicationId: string;
  applicationStatus: string;
  appliedAt: string;
  insuredPersonInfo: string;
  nextStepMessage: string | null;
}

export interface UnderwritingDeductionItemResponse {
  itemName: string;
  itemValue: string;
  deduction: number;
  reason: string;
}

export interface UnderwritingAutoScoreResponse {
  applicationId: string;
  reviewId: string;
  totalScore: number;
  totalDeduction: number;
  recommendedResult: UnderwritingResultType;
  autoReviewAvailable: boolean;
  manualReviewRequired: boolean;
  coinsuranceRecommended: boolean;
  deductionItems: UnderwritingDeductionItemResponse[];
  reportSummary: string;
  coinsuranceMessage: string;
  reinsuranceMessage: string;
  policyIssueMessage: string;
  createdAt: string;
}

export interface UnderwritingFinalizeRequest {
  finalResult: UnderwritingResultType;
  underwriterId: string;
  underwriterName: string;
  department: string;
  underwritingOpinion: string | null;
  surchargeCondition: string | null;
  rejectionReason: string | null;
}

export interface UnderwritingReviewResponse {
  applicationId: string;
  reviewId: string;
  underwritingStatus: string;
  underwritingType: string;
  totalScore: number;
  totalDeduction: number;
  recommendedResult: UnderwritingResultType;
  finalResult: UnderwritingResultType | null;
  autoReviewAvailable: boolean;
  coinsuranceRecommended: boolean;
  itemizedScores: string;
  underwriterId: string | null;
  underwriterName: string | null;
  department: string | null;
  underwritingOpinion: string | null;
  surchargeCondition: string | null;
  rejectionReason: string | null;
  createdAt: string;
  finalizedAt: string | null;
  nextStepMessage: string | null;
}

export interface UnderwritingHistoryResponse {
  historyId: string;
  applicationId: string;
  reviewId: string | null;
  eventType: string;
  eventMessage: string;
  score: number | null;
  result: string | null;
  createdAt: string;
}

export const UNDERWRITING_RESULT_LABELS: Record<string, string> = {
  APPROVED: '승인',
  SURCHARGE: '할증',
  REJECTED: '거절',
  PENDING: '대기'
};

export const APPLICATION_STATUS_LABELS: Record<string, string> = {
  PENDING: '심사 대기',
  APPROVED: '승인',
  REJECTED: '거절',
  CANCELLED: '취소'
};

export const UNDERWRITING_STATUS_LABELS: Record<string, string> = {
  PENDING: '대기',
  IN_PROGRESS: '심사 진행',
  COMPLETED: '심사 완료',
  APPROVED: '승인',
  REJECTED: '거절'
};
