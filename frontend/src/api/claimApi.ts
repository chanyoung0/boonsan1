import { apiRequest } from './apiClient';
import type {
  AccidentReportCreateRequest,
  AccidentReportResponse,
  AdjusterOpinionRequest,
  DamageAssessmentRequest,
  DamageInvestigationResultResponse,
  DamageInvestigationStartResponse,
  FieldInvestigationMaterialResponse,
  InvestigationApprovalRequest,
  PaymentApprovalDocumentResponse,
  PaymentApprovalDraftResponse
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
