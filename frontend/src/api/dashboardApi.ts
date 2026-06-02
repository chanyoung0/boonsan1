import { apiRequest } from './apiClient';
import type { DashboardSummaryResponse } from '../types/dashboard';

export function getDashboardSummary() {
  return apiRequest<DashboardSummaryResponse>('/api/dashboard/summary');
}
