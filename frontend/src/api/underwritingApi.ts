import { apiRequest } from './apiClient';
import type {
  UnderwritingApplicationCreateRequest,
  UnderwritingApplicationResponse,
  UnderwritingAutoScoreResponse,
  UnderwritingFinalizeRequest,
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
