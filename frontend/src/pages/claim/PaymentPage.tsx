import { KeyboardEvent, useState } from 'react';
import { CreditCard, FileCheck2, Search } from 'lucide-react';
import { getPaymentApprovalDocument } from '../../api/claimApi';
import { ApiError } from '../../api/apiClient';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type { PaymentApprovalDocumentResponse } from '../../types/claim';

const SUBMISSION_STATUS_LABELS: Record<string, string> = {
  DRAFT: '초안',
  OPINION_SAVED: '소견 저장',
  APPROVAL_REQUESTED: '결재 요청'
};

export function PaymentPage() {
  const [accidentNumberInput, setAccidentNumberInput] = useState('');
  const [document, setDocument] = useState<PaymentApprovalDocumentResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notFound, setNotFound] = useState<string | null>(null);

  const handleLookup = async () => {
    const accidentNumber = accidentNumberInput.trim();
    if (!accidentNumber) {
      setError('사고번호를 입력해주세요.');
      setNotFound(null);
      setDocument(null);
      return;
    }

    setIsLoading(true);
    setError(null);
    setNotFound(null);
    setDocument(null);
    try {
      const response = await getPaymentApprovalDocument(accidentNumber);
      setDocument(response);
      setAccidentNumberInput(response.accidentNumber);
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setNotFound('해당 사고번호의 지급품의서가 없습니다.');
        return;
      }
      setError(caught instanceof Error ? caught.message : '지급품의서 조회에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleLookup();
    }
  };

  return (
    <AppLayout activeMenuId="claim-payment">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>보상 처리</span>
            <span aria-hidden="true">/</span>
            <strong>보험금 지급</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>보험금 지급</h1>
              <p>사고번호로 결재 요청된 지급품의서를 조회하고, 실제 지급 전 확인 정보를 검토합니다.</p>
            </div>
            <span className="page-kicker">보상 처리 · 지급 대기</span>
          </div>
        </header>

        <section className="work-panel search-panel investigation-lookup-panel">
          <div className="panel-header compact">
            <div>
              <h2>지급품의서 조회</h2>
              <p>손해조사 완료 후 생성된 지급품의서를 사고번호 기준으로 조회합니다.</p>
            </div>
          </div>
          <div className="search-row">
            <input
              aria-label="사고번호"
              value={accidentNumberInput}
              onChange={(event) => setAccidentNumberInput(event.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ACC-2026-086881"
              disabled={isLoading}
            />
            <button className="button primary" type="button" onClick={handleLookup} disabled={isLoading}>
              <Search aria-hidden="true" size={16} />
              {isLoading ? '조회 중...' : '조회'}
            </button>
          </div>
        </section>

        {error && <AlertMessage type="error" message={error} />}

        {notFound && (
          <aside className="work-panel empty-result">
            <strong>{notFound}</strong>
            <p>손해조사 완료 및 결재 요청 이후 보험금 지급 화면에서 지급품의서를 확인할 수 있습니다.</p>
          </aside>
        )}

        {document ? (
          <>
            <PaymentApprovalLookupCard document={document} />
            <section className="work-panel detail-panel approval-card">
              <div className="panel-header compact">
                <div>
                  <h2>실제 보험금 지급</h2>
                  <p>결재 승인 및 실제 지급 처리는 다음 단계에서 구현 예정입니다.</p>
                </div>
                <CreditCard aria-hidden="true" size={22} />
              </div>
              <button className="button primary" type="button" disabled>
                실제 지급 처리 준비 중
              </button>
            </section>
          </>
        ) : (
          !notFound && (
            <aside className="work-panel empty-result">
              <strong>지급품의서 조회 대기</strong>
              <p>사고번호를 입력하면 결재 요청된 지급품의서 정보가 여기에 표시됩니다.</p>
            </aside>
          )
        )}
      </div>
    </AppLayout>
  );
}

function PaymentApprovalLookupCard({ document }: { document: PaymentApprovalDocumentResponse }) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>지급품의서 상세</h2>
          <p>{document.documentId}</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>

      <dl className="amount-grid">
        <AmountItem label="지급품의서 번호" value={document.documentId} mono />
        <AmountItem label="사고번호" value={document.accidentNumber} mono />
        <AmountItem label="손해조사번호" value={document.investigationId} mono />
        <AmountItem label="총 손해액" value={document.totalDamageAmount} strong />
        <AmountItem label="지급 인정 비율" value={`${document.faultRatio}%`} />
        <AmountItem label="최종 지급 예정 금액" value={document.calculatedPaymentAmount} strong />
        <AmountItem label="결재 상태" value={getSubmissionStatusLabel(document.submissionStatus)} strong />
        <AmountItem label="결재 요청자" value={document.employeeNo || '미입력'} />
      </dl>

      <div className="text-detail-blocks approval-opinions">
        <TextBlock title="손해사정인 소견" value={document.adjusterOpinion || '미입력'} />
        <TextBlock title="지급 인정 비율 소견" value={document.faultRatioOpinion || '미입력'} />
      </div>
    </section>
  );
}

function AmountItem({
  label,
  value,
  strong = false,
  mono = false
}: {
  label: string;
  value: number | string;
  strong?: boolean;
  mono?: boolean;
}) {
  const displayValue = typeof value === 'number' ? `${Math.round(value).toLocaleString('ko-KR')}원` : value;
  return (
    <div className={`amount-item ${strong ? 'strong' : ''}`}>
      <dt>{label}</dt>
      <dd className={mono ? 'mono' : ''} title={displayValue}>
        {displayValue}
      </dd>
    </div>
  );
}

function TextBlock({ title, value }: { title: string; value: string }) {
  return (
    <section className="text-box">
      <h3>{title}</h3>
      <p>{value}</p>
    </section>
  );
}

function getSubmissionStatusLabel(status: string) {
  return SUBMISSION_STATUS_LABELS[status] ?? status;
}
