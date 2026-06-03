import { apiRequest } from './apiClient';
import type {
  AccidentReportCreateRequest,
  AccidentReportResponse,
  AdjusterOpinionRequest,
  ClaimAlternativeFlowResponse,
  DamageAssessmentRequest,
  DamageInvestigationRejectRequest,
  DamageInvestigationResultResponse,
  DamageInvestigationStartResponse,
  FieldInvestigationMaterialResponse,
  FraudInvestigationRequest,
  InvestigationApprovalRequest,
  OutsourceInvestigationRequest,
  PaymentApprovalDocumentResponse,
  PaymentApprovalDraftResponse,
  ObjectionCreateRequest,
  ObjectionEligibilityResponse,
  ObjectionResponse,
  SubrogationCompleteRequest,
  SubrogationCreateRequest,
  SubrogationEligibilityResponse,
  SubrogationResponse
} from '../types/claim';

export function createAccidentReport(request: AccidentReportCreateRequest) {
  return apiRequest<AccidentReportResponse>('/api/claims/accident-reports', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export function getAccidentReport(accidentNumber: string) {
  return apiRequest<AccidentReportResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}`
  );
}

export function getAccidentReportForInvestigation(accidentNumber: string) {
  return apiRequest<DamageInvestigationStartResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/investigation`
  );
}

export function getFieldInvestigationMaterials(accidentNumber: string) {
  return apiRequest<FieldInvestigationMaterialResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/field-materials`
  );
}

export function getDamageInvestigationResult(accidentNumber: string) {
  return apiRequest<DamageInvestigationResultResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/damage-investigations/result`
  );
}

export function getPaymentApprovalDocument(accidentNumber: string) {
  return apiRequest<PaymentApprovalDocumentResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/payment-approval-document`
  );
}

export function approvePaymentApprovalDocument(accidentNumber: string) {
  return apiRequest<PaymentApprovalDocumentResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/payment-approval-document/approve`,
    {
      method: 'PATCH'
    }
  );
}

export function rejectPaymentApprovalDocument(accidentNumber: string) {
  return apiRequest<PaymentApprovalDocumentResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/payment-approval-document/reject`,
    {
      method: 'PATCH'
    }
  );
}

export function payPaymentApprovalDocument(accidentNumber: string) {
  return apiRequest<PaymentApprovalDocumentResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/payment-approval-document/pay`,
    {
      method: 'PATCH'
    }
  );
}

export function rejectInsuranceProcessing(accidentNumber: string, request: DamageInvestigationRejectRequest) {
  return apiRequest<ClaimAlternativeFlowResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/investigation/reject`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function requestFraudInvestigation(accidentNumber: string, request: FraudInvestigationRequest) {
  return apiRequest<ClaimAlternativeFlowResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/investigation/fraud-request`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function requestOutsourceInvestigation(accidentNumber: string, request: OutsourceInvestigationRequest) {
  return apiRequest<ClaimAlternativeFlowResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/investigation/outsource`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function completeOutsourceInvestigation(accidentNumber: string) {
  return apiRequest<ClaimAlternativeFlowResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/investigation/outsource/complete`,
    {
      method: 'PATCH'
    }
  );
}

export function getClaimAlternativeFlowHistory(accidentNumber: string) {
  return apiRequest<ClaimAlternativeFlowResponse[]>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/investigation/actions`
  );
}

export function createPaymentApprovalDraft(accidentNumber: string, request: DamageAssessmentRequest) {
  return apiRequest<PaymentApprovalDraftResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/damage-investigations/draft`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function saveAdjusterOpinion(accidentNumber: string, request: AdjusterOpinionRequest) {
  return apiRequest<PaymentApprovalDocumentResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/damage-investigations/opinion`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function requestInvestigationApproval(accidentNumber: string, request: InvestigationApprovalRequest) {
  return apiRequest<PaymentApprovalDocumentResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/damage-investigations/approval-request`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function getSubrogationEligibility(accidentNumber: string) {
  return apiRequest<SubrogationEligibilityResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/subrogation/eligibility`
  );
}

export function getSubrogation(accidentNumber: string) {
  return apiRequest<SubrogationResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/subrogation`
  );
}

export function createSubrogation(accidentNumber: string, request: SubrogationCreateRequest) {
  return apiRequest<SubrogationResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/subrogation`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function completeSubrogation(accidentNumber: string, request: SubrogationCompleteRequest) {
  return apiRequest<SubrogationResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/subrogation/complete`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function getObjectionEligibility(accidentNumber: string) {
  return apiRequest<ObjectionEligibilityResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/objection/eligibility`
  );
}

export function getObjection(accidentNumber: string) {
  return apiRequest<ObjectionResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/objection`
  );
}

export function createObjection(accidentNumber: string, request: ObjectionCreateRequest) {
  return apiRequest<ObjectionResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/objection`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function markObjectionReinvestigationRequired(accidentNumber: string) {
  return apiRequest<ObjectionResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/objection/reinvestigation`,
    {
      method: 'PATCH'
    }
  );
}

export function rejectObjection(accidentNumber: string) {
  return apiRequest<ObjectionResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/objection/reject`,
    {
      method: 'PATCH'
    }
  );
}

export function transferObjectionToLegal(accidentNumber: string) {
  return apiRequest<ObjectionResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/objection/legal-transfer`,
    {
      method: 'PATCH'
    }
  );
}

export function completeObjection(accidentNumber: string) {
  return apiRequest<ObjectionResponse>(
    `/api/claims/accident-reports/${encodeURIComponent(accidentNumber)}/objection/complete`,
    {
      method: 'PATCH'
    }
  );
}
