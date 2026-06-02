import { apiRequest } from './apiClient';
import type {
  CoinsuranceCreateRequest,
  CoinsuranceProcessResponse,
  CoinsuranceResultRequest,
  CreditInformationInquiryCreateRequest,
  CreditInformationInquiryResponse,
  PolicyIssueResponse,
  ReinsuranceCreateRequest,
  ReinsuranceProcessResponse,
  ReinsuranceResultRequest,
  UnderwritingApplicationCreateRequest,
  UnderwritingApplicationResponse,
  UnderwritingAutoScoreResponse,
  UnderwritingFinalizeRequest,
  UnderwritingFollowUpEligibilityResponse,
  UnderwritingHistoryResponse,
  UnderwritingReviewResponse
} from '../types/underwriting';

export function createUnderwritingApplication(request: UnderwritingApplicationCreateRequest) {
  return apiRequest<UnderwritingApplicationResponse>('/api/underwriting/applications', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export function getUnderwritingApplication(applicationId: string) {
  return apiRequest<UnderwritingApplicationResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}`
  );
}

export function calculateUnderwritingAutoScore(applicationId: string) {
  return apiRequest<UnderwritingAutoScoreResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/reviews/auto-score`,
    {
      method: 'POST'
    }
  );
}

export function finalizeUnderwritingReview(applicationId: string, request: UnderwritingFinalizeRequest) {
  return apiRequest<UnderwritingReviewResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/reviews/finalize`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function getUnderwritingHistory(applicationId: string) {
  return apiRequest<UnderwritingHistoryResponse[]>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/history`
  );
}

export function createCreditInformationInquiry(
  applicationId: string,
  request: CreditInformationInquiryCreateRequest
) {
  return apiRequest<CreditInformationInquiryResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/credit-inquiries`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function getCreditInformationInquiries(applicationId: string) {
  return apiRequest<CreditInformationInquiryResponse[]>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/credit-inquiries`
  );
}

export function getCreditInformationInquiry(inquiryId: string) {
  return apiRequest<CreditInformationInquiryResponse>(
    `/api/underwriting/credit-inquiries/${encodeURIComponent(inquiryId)}`
  );
}

export function getCoinsuranceEligibility(applicationId: string) {
  return apiRequest<UnderwritingFollowUpEligibilityResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/coinsurance/eligibility`
  );
}

export function createCoinsuranceProcess(applicationId: string, request: CoinsuranceCreateRequest) {
  return apiRequest<CoinsuranceProcessResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/coinsurance`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function getCoinsuranceProcess(applicationId: string) {
  return apiRequest<CoinsuranceProcessResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/coinsurance`
  );
}

export function updateCoinsuranceResult(applicationId: string, request: CoinsuranceResultRequest) {
  return apiRequest<CoinsuranceProcessResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/coinsurance/result`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function getReinsuranceEligibility(applicationId: string) {
  return apiRequest<UnderwritingFollowUpEligibilityResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/reinsurance/eligibility`
  );
}

export function createReinsuranceProcess(applicationId: string, request: ReinsuranceCreateRequest) {
  return apiRequest<ReinsuranceProcessResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/reinsurance`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function getReinsuranceProcess(applicationId: string) {
  return apiRequest<ReinsuranceProcessResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/reinsurance`
  );
}

export function updateReinsuranceResult(applicationId: string, request: ReinsuranceResultRequest) {
  return apiRequest<ReinsuranceProcessResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/reinsurance/result`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function getPolicyIssueEligibility(applicationId: string) {
  return apiRequest<UnderwritingFollowUpEligibilityResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/policy-issue/eligibility`
  );
}

export function issuePolicy(applicationId: string) {
  return apiRequest<PolicyIssueResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/policy-issue`,
    {
      method: 'POST'
    }
  );
}

export function getPolicyIssue(applicationId: string) {
  return apiRequest<PolicyIssueResponse>(
    `/api/underwriting/applications/${encodeURIComponent(applicationId)}/policy-issue`
  );
}
