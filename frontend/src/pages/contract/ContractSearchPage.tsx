import { AppLayout } from '../../components/layout/AppLayout';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';

export function ContractSearchPage() {
  return (
    <AppLayout activeMenuId="contract-search">
      <div className="page-stack contract-page">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>계약 조회</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>계약 조회</h1>
              <p>증권번호로 계약 상태, 납입 정보, 피보험자 정보를 확인합니다.</p>
            </div>
            <span className="page-kicker">계약 관리 · 계약 정보</span>
          </div>
        </header>

        <ContractLookupCard
          title="계약 조회"
          description="조회할 계약의 증권번호를 입력하세요."
          placeholder="POL-2024-000001"
        />
      </div>
    </AppLayout>
  );
}
