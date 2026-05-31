import { apiRequest } from './apiClient';
import type { AccidentReportCreateRequest, AccidentReportResponse } from '../types/claim';

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
