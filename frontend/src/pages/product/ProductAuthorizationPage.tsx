import { useState, type KeyboardEvent } from 'react';
import { FileSearch, Search } from 'lucide-react';
import { ApiError } from '../../api/apiClient';
import { getAuthorization, getAuthorizationEligibility } from '../../api/productApi';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/product/AlertMessage';
import { AuthorizationApprovalPanel } from '../../components/product/AuthorizationApprovalPanel';
import { AuthorizationEligibilityCard } from '../../components/product/AuthorizationEligibilityCard';
import { AuthorizationForm } from '../../components/product/AuthorizationForm';
import { AuthorizationStatusCard } from '../../components/product/AuthorizationStatusCard';
import { ProductWorkflowSteps } from '../../components/product/ProductWorkflowSteps';
import type {
  AuthorizationEligibilityResponse,
  AuthorizationResponse
} from '../../types/product';

export function ProductAuthorizationPage() {
  const [productCodeInput, setProductCodeInput] = useState('');
  const [eligibility, setEligibility] = useState<AuthorizationEligibilityResponse | null>(null);
  const [authorization, setAuthorization] = useState<AuthorizationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleLookup = async () => {
    const productCode = productCodeInput.trim();
    if (!productCode) {
      setError('상품 코드를 입력하세요.');
      setSuccess(null);
      setEligibility(null);
      setAuthorization(null);
      return;
    }

    setIsLoading(true);
    setError(null);
    setSuccess(null);
    setAuthorization(null);

    try {
      const eligibilityResponse = await getAuthorizationEligibility(productCode);
      setEligibility(eligibilityResponse);
      setProductCodeInput(eligibilityResponse.productCode);
      await loadExistingAuthorization(eligibilityResponse.productCode);
    } catch (caught) {
      setEligibility(null);
      setAuthorization(null);
      setError(caught instanceof Error ? caught.message : '상품 정보를 조회하지 못했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const loadExistingAuthorization = async (productCode: string) => {
    try {
      const existing = await getAuthorization(productCode);
      setAuthorization(existing);
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setAuthorization(null);
        return;
      }
      throw caught;
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleLookup();
    }
  };

  const handleAuthorizationCreated = (data: AuthorizationResponse) => {
    setAuthorization(data);
    setSuccess('인가 요청이 등록되었습니다.');
    if (eligibility) {
      setEligibility({
        ...eligibility,
        productStatus: data.productStatus,
        eligible: false,
        message: '인가 요청이 진행 중입니다.'
      });
    }
  };

  const handleAuthorizationUpdated = (data: AuthorizationResponse) => {
    setAuthorization(data);
    setSuccess('인가 상태가 갱신되었습니다.');
    if (eligibility) {
      setEligibility({ ...eligibility, productStatus: data.productStatus });
    }
  };

  return (
    <AppLayout activeMenuId="product-approval">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>상품 관리</span>
            <span aria-hidden="true">/</span>
            <strong>상품 인가 요청</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>상품 인가 요청</h1>
              <p>설계가 완료된 상품을 금융감독원에 인가 요청하고 결과를 반영합니다.</p>
            </div>
            <span className="page-kicker">상품 관리 · 상품개발자 · 금융감독원</span>
          </div>
        </header>

        <ProductWorkflowSteps currentStepId="authorization" />

        <section className="work-panel search-panel investigation-lookup-panel">
          <div className="panel-header compact">
            <div>
              <h2>상품 코드 조회</h2>
              <p>설계 완료 또는 보완 요청 상태인 상품만 인가 요청을 등록할 수 있습니다.</p>
            </div>
            <FileSearch aria-hidden="true" size={22} />
          </div>
          <div className="search-row">
            <input
              aria-label="상품 코드"
              value={productCodeInput}
              onChange={(event) => setProductCodeInput(event.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="PRD-AUTO-2026-000123"
              disabled={isLoading}
            />
            <button className="button primary" type="button" onClick={handleLookup} disabled={isLoading}>
              <Search aria-hidden="true" size={16} />
              {isLoading ? '조회 중...' : '조회'}
            </button>
          </div>
        </section>

        {error && <AlertMessage type="error" message={error} />}
        {success && <AlertMessage type="success" message={success} />}

        {eligibility ? (
          <>
            <AuthorizationEligibilityCard data={eligibility} />
            {eligibility.eligible && !authorization && (
              <AuthorizationForm productCode={eligibility.productCode} onSuccess={handleAuthorizationCreated} />
            )}
            {authorization && (
              <>
                <AuthorizationStatusCard data={authorization} />
                <AuthorizationApprovalPanel
                  productCode={eligibility.productCode}
                  data={authorization}
                  onUpdate={handleAuthorizationUpdated}
                />
              </>
            )}
          </>
        ) : (
          <aside className="work-panel empty-result">
            <strong>상품 코드 조회 대기</strong>
            <p>상품 코드를 입력하면 인가 가능 여부와 등록된 인가 요청이 여기에 표시됩니다.</p>
          </aside>
        )}
      </div>
    </AppLayout>
  );
}
