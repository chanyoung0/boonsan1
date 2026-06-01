import { useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { ProductDesignForm } from '../../components/product/ProductDesignForm';
import { ProductDesignResultCard } from '../../components/product/ProductDesignResultCard';
import { ProductDetailCard } from '../../components/product/ProductDetailCard';
import { ProductSearchBox } from '../../components/product/ProductSearchBox';
import { ProductWorkflowSteps } from '../../components/product/ProductWorkflowSteps';
import type { ProductResponse } from '../../types/product';

export function ProductDesignPage() {
  const [registrationResult, setRegistrationResult] = useState<ProductResponse | null>(null);

  return (
    <AppLayout activeMenuId="product-design">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>상품 관리</span>
            <span aria-hidden="true">/</span>
            <strong>상품 설계</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>상품 설계</h1>
              <p>보험 상품의 기본정보·담보·가입조건·요율·특약 정보를 입력하여 설계 완료 상태로 등록합니다.</p>
            </div>
            <span className="page-kicker">상품 관리 · 상품개발자</span>
          </div>
        </header>

        <ProductWorkflowSteps currentStepId="design" />

        <section className="content-grid">
          <ProductDesignForm onSuccess={setRegistrationResult} />
          <div className="side-stack">
            {registrationResult ? (
              <ProductDesignResultCard data={registrationResult} />
            ) : (
              <aside className="work-panel empty-result">
                <strong>설계 결과 대기</strong>
                <p>상품 설계를 저장하면 백엔드가 발급한 상품 코드가 여기에 표시됩니다.</p>
              </aside>
            )}
            {registrationResult && <ProductDetailCard data={registrationResult} />}
          </div>
        </section>

        <ProductSearchBox />
      </div>
    </AppLayout>
  );
}
