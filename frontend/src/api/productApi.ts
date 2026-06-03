import { apiRequest } from './apiClient';
import type {
  AuthorizationCreateRequest,
  AuthorizationEligibilityResponse,
  AuthorizationResponse,
  AuthorizationRevisionRequest,
  PremiumEstimateRequest,
  PremiumEstimateResponse,
  ProductDesignRequest,
  ProductResponse
} from '../types/product';

export function createProduct(request: ProductDesignRequest) {
  return apiRequest<ProductResponse>('/api/products', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export function estimatePremium(request: PremiumEstimateRequest) {
  return apiRequest<PremiumEstimateResponse>('/api/products/premium-estimate', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export function getProducts() {
  return apiRequest<ProductResponse[]>('/api/products');
}

export function getProduct(productCode: string) {
  return apiRequest<ProductResponse>(`/api/products/${encodeURIComponent(productCode)}`);
}

export function getAuthorizationEligibility(productCode: string) {
  return apiRequest<AuthorizationEligibilityResponse>(
    `/api/products/${encodeURIComponent(productCode)}/authorization/eligibility`
  );
}

export function getAuthorization(productCode: string) {
  return apiRequest<AuthorizationResponse>(
    `/api/products/${encodeURIComponent(productCode)}/authorization`
  );
}

export function createAuthorization(productCode: string, request: AuthorizationCreateRequest) {
  return apiRequest<AuthorizationResponse>(
    `/api/products/${encodeURIComponent(productCode)}/authorization`,
    {
      method: 'POST',
      body: JSON.stringify(request)
    }
  );
}

export function approveAuthorization(productCode: string) {
  return apiRequest<AuthorizationResponse>(
    `/api/products/${encodeURIComponent(productCode)}/authorization/approve`,
    { method: 'PATCH' }
  );
}

export function rejectAuthorization(productCode: string) {
  return apiRequest<AuthorizationResponse>(
    `/api/products/${encodeURIComponent(productCode)}/authorization/reject`,
    { method: 'PATCH' }
  );
}

export function requestAuthorizationRevision(productCode: string, request: AuthorizationRevisionRequest) {
  return apiRequest<AuthorizationResponse>(
    `/api/products/${encodeURIComponent(productCode)}/authorization/revision`,
    {
      method: 'PATCH',
      body: JSON.stringify(request)
    }
  );
}

export function cancelAuthorization(productCode: string) {
  return apiRequest<AuthorizationResponse>(
    `/api/products/${encodeURIComponent(productCode)}/authorization/cancel`,
    { method: 'PATCH' }
  );
}
