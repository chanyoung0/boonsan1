import type { ApiResponse } from '../types/claim';

const DEFAULT_API_BASE_URL = 'http://localhost:8080';

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE_URL).replace(/\/$/, '');

export class ApiError extends Error {
  readonly status: number;
  readonly errorCode: string | null;

  constructor(message: string, status: number, errorCode: string | null = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errorCode = errorCode;
  }
}

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${apiBaseUrl}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {})
    },
    ...init
  });

  const body = (await response.json().catch(() => null)) as ApiResponse<T> | null;

  if (!response.ok || !body?.success) {
    throw new ApiError(
      body?.message || `API 요청에 실패했습니다. (${response.status})`,
      response.status,
      body?.errorCode ?? null
    );
  }

  if (body.data === null) {
    throw new ApiError('응답 데이터가 비어 있습니다.', response.status, body.errorCode);
  }

  return body.data;
}

export { apiBaseUrl };
