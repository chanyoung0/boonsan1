import { apiRequest } from './apiClient';
import type {
  ContractResponse,
  MaturityNoticeResponse,
  MaturityProcessResponse,
  MaturityRenewalResponse,
  MaturityTargetResponse,
  PaymentCollectionBatchRequest,
  PaymentCollectionBatchResponse,
  PaymentCollectionCreateRequest,
  PaymentCollectionResponse,
  PaymentCollectionTargetResponse,
  PaymentCollectionTransferRequest,
  PaymentCollectionTransferTargetResponse,
  PayoutApproveRequest,
  PayoutCreateRequest,
  PayoutResponse,
  EndorsementCreateRequest,
  EndorsementResponse,
  ReinstatementCreateRequest,
  ReinstatementResponse,
  ReinstatementUnpaidSummaryResponse,
  UnderwritingRequestCompleteRequest,
  UnderwritingRequestCreateRequest,
  UnderwritingRequestResponse,
  UnpaidNoticeResponse
} from '../types/contract';

export function getContract(policyNumber: string) {
  return apiRequest<ContractResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}`
  );
}

export function getMaturityNotice(policyNumber: string) {
  return apiRequest<MaturityNoticeResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/maturity/notice`
  );
}

export function processMaturity(policyNumber: string) {
  return apiRequest<MaturityProcessResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/maturity`,
    { method: 'POST' }
  );
}

export function listMaturityTargets() {
  return apiRequest<MaturityTargetResponse[]>('/api/contracts/maturity/targets');
}

export function sendMaturityNotice(policyNumber: string) {
  return apiRequest<MaturityNoticeResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/maturity/notice`,
    { method: 'POST' }
  );
}

export function listMaturityRenewalTargets() {
  return apiRequest<MaturityTargetResponse[]>('/api/contracts/maturity/renewal-targets');
}

export function recordMaturityRenewalIntention(policyNumber: string, renewalIntention: boolean) {
  return apiRequest<MaturityRenewalResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/maturity/renewal-intention`,
    {
      method: 'PATCH',
      body: JSON.stringify({ renewalIntention })
    }
  );
}

export function createPayout(policyNumber: string, request: PayoutCreateRequest) {
  return apiRequest<PayoutResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payouts`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function listPayouts(policyNumber: string) {
  return apiRequest<PayoutResponse[]>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payouts`
  );
}

export function getPayout(policyNumber: string, payoutId: string) {
  return apiRequest<PayoutResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payouts/${encodeURIComponent(payoutId)}`
  );
}

export function approvePayout(policyNumber: string, payoutId: string, request: PayoutApproveRequest) {
  return apiRequest<PayoutResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payouts/${encodeURIComponent(payoutId)}/approve`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function payPayout(policyNumber: string, payoutId: string) {
  return apiRequest<PayoutResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payouts/${encodeURIComponent(payoutId)}/pay`,
    { method: 'PATCH' }
  );
}

export function cancelPayout(policyNumber: string, payoutId: string) {
  return apiRequest<PayoutResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payouts/${encodeURIComponent(payoutId)}/cancel`,
    { method: 'PATCH' }
  );
}

export function createPaymentCollection(policyNumber: string, request: PaymentCollectionCreateRequest) {
  return apiRequest<PaymentCollectionResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payment-collections`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function listPaymentCollectionTargets() {
  return apiRequest<PaymentCollectionTargetResponse[]>('/api/contracts/payment-collections/targets');
}

export function processPaymentCollectionBatch(request: PaymentCollectionBatchRequest) {
  return apiRequest<PaymentCollectionBatchResponse>('/api/contracts/payment-collections/batch', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export function listPaymentCollectionTransferTargets() {
  return apiRequest<PaymentCollectionTransferTargetResponse[]>(
    '/api/contracts/payment-collections/transfer-targets'
  );
}

export function listPaymentCollections(policyNumber: string) {
  return apiRequest<PaymentCollectionResponse[]>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payment-collections`
  );
}

export function getPaymentCollection(policyNumber: string, collectionId: string) {
  return apiRequest<PaymentCollectionResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payment-collections/${encodeURIComponent(collectionId)}`
  );
}

export function getUnpaidNotice(policyNumber: string, collectionId: string) {
  return apiRequest<UnpaidNoticeResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payment-collections/${encodeURIComponent(collectionId)}/unpaid-notice`
  );
}

export function transferPaymentCollection(
  policyNumber: string,
  collectionId: string,
  request: PaymentCollectionTransferRequest
) {
  return apiRequest<PaymentCollectionResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/payment-collections/${encodeURIComponent(collectionId)}/transfer`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function applyReinstatement(policyNumber: string, request: ReinstatementCreateRequest) {
  return apiRequest<ReinstatementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function getReinstatementUnpaidSummary(policyNumber: string) {
  return apiRequest<ReinstatementUnpaidSummaryResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/unpaid-summary`
  );
}

export function getActiveReinstatement(policyNumber: string) {
  return apiRequest<ReinstatementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/current`
  );
}

export function listReinstatements(policyNumber: string) {
  return apiRequest<ReinstatementResponse[]>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements`
  );
}

export function settleReinstatementUnpaid(policyNumber: string) {
  return apiRequest<ReinstatementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/current/settle-unpaid`,
    { method: 'PATCH' }
  );
}

export function completeReinstatement(policyNumber: string) {
  return apiRequest<ReinstatementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/current/complete`,
    { method: 'PATCH' }
  );
}

export function cancelReinstatement(policyNumber: string) {
  return apiRequest<ReinstatementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/current/cancel`,
    { method: 'PATCH' }
  );
}

export function requestReinstatementUnderwriting(
  policyNumber: string,
  request: UnderwritingRequestCreateRequest
) {
  return apiRequest<UnderwritingRequestResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/current/underwriting-request`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function completeReinstatementUnderwriting(
  policyNumber: string,
  request: UnderwritingRequestCompleteRequest
) {
  return apiRequest<UnderwritingRequestResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/current/underwriting-request/complete`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function getReinstatementUnderwriting(policyNumber: string) {
  return apiRequest<UnderwritingRequestResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/reinstatements/current/underwriting-request`
  );
}

export function applyEndorsement(policyNumber: string, request: EndorsementCreateRequest) {
  return apiRequest<EndorsementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function getActiveEndorsement(policyNumber: string) {
  return apiRequest<EndorsementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements/current`
  );
}

export function listEndorsements(policyNumber: string) {
  return apiRequest<EndorsementResponse[]>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements`
  );
}

export function requestEndorsementUnderwriting(
  policyNumber: string,
  request: UnderwritingRequestCreateRequest
) {
  return apiRequest<UnderwritingRequestResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements/current/underwriting-request`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function completeEndorsementUnderwriting(
  policyNumber: string,
  request: UnderwritingRequestCompleteRequest
) {
  return apiRequest<UnderwritingRequestResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements/current/underwriting-request/complete`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function getEndorsementUnderwriting(policyNumber: string) {
  return apiRequest<UnderwritingRequestResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements/current/underwriting-request`
  );
}

export function approveEndorsement(policyNumber: string) {
  return apiRequest<EndorsementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements/current/approve`,
    { method: 'PATCH' }
  );
}

export function rejectEndorsement(policyNumber: string) {
  return apiRequest<EndorsementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements/current/reject`,
    { method: 'PATCH' }
  );
}

export function cancelEndorsement(policyNumber: string) {
  return apiRequest<EndorsementResponse>(
    `/api/contracts/${encodeURIComponent(policyNumber)}/endorsements/current/cancel`,
    { method: 'PATCH' }
  );
}
