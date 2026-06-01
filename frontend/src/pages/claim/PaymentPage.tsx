import { KeyboardEvent, useState } from 'react';
import { CheckCircle2, CreditCard, FileCheck2, Search, XCircle } from 'lucide-react';
import {
  approvePaymentApprovalDocument,
  getPaymentApprovalDocument,
  payPaymentApprovalDocument,
  rejectPaymentApprovalDocument
} from '../../api/claimApi';
import { ApiError } from '../../api/apiClient';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type { PaymentApprovalDocumentResponse } from '../../types/claim';

const SUBMISSION_STATUS_LABELS: Record<string, string> = {
  DRAFT: '초안',
  OPINION_SAVED: '소견 저장',
  APPROVAL_REQUESTED: '결재 요청',
  APPROVED: '승인',
  REJECTED: '반려',
  PAID: '지급 완료'
};

type PaymentAction = 'lookup' | 'approve' | 'reject' | 'pay' | null;

export function PaymentPage() {
  const [accidentNumberInput, setAccidentNumberInput] = useState('');
  const [document, setDocument] = useState<PaymentApprovalDocumentResponse | null>(null);
  const [loadingAction, setLoadingAction] = useState<PaymentAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [notFound, setNotFound] = useState<string | null>(null);

  const handleLookup = async () => {
    const accidentNumber = accidentNumberInput.trim();
    if (!accidentNumber) {
      setError('사고번호를 입력해주세요.');
      setSuccess(null);
      setNotFound(null);
      setDocument(null);
      return;
    }

    await loadPaymentApprovalDocument(accidentNumber, 'lookup');
  };

  const loadPaymentApprovalDocument = async (
    accidentNumber: string,
    action: PaymentAction = null,
    successMessage?: string
  ) => {
    setLoadingAction(action);
    setError(null);
    setSuccess(null);
    setNotFound(null);
    if (action === 'lookup') {
      setDocument(null);
    }

    try {
      const response = await getPaymentApprovalDocument(accidentNumber);
      setDocument(response);
      setAccidentNumberInput(response.accidentNumber);
      if (successMessage) {
        setSuccess(successMessage);
      }
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setDocument(null);
        setNotFound('해당 사고번호의 지급품의서가 없습니다.');
        return;
      }
      setError(caught instanceof Error ? caught.message : '지급품의서 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handlePaymentAction = async (
    action: Exclude<PaymentAction, 'lookup' | null>,
    request: (accidentNumber: string) => Promise<PaymentApprovalDocumentResponse>,
    successMessage: string
  ) => {
    if (!document) return;

    setLoadingAction(action);
    setError(null);
    setSuccess(null);
    try {
      const response = await request(document.accidentNumber);
      setDocument(response);
      setSuccess(successMessage);
      await loadPaymentApprovalDocument(response.accidentNumber, null, successMessage);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '지급품의서 처리에 실패했습니다.');
      setLoadingAction(null);
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
              disabled={loadingAction === 'lookup'}
            />
            <button
              className="button primary"
              type="button"
              onClick={handleLookup}
              disabled={loadingAction === 'lookup'}
            >
              <Search aria-hidden="true" size={16} />
              {loadingAction === 'lookup' ? '조회 중...' : '조회'}
            </button>
          </div>
        </section>

        {error && <AlertMessage type="error" message={error} />}
        {success && <AlertMessage type="success" message={success} />}

        {notFound && (
          <aside className="work-panel empty-result">
            <strong>{notFound}</strong>
            <p>손해조사 완료 및 결재 요청 이후 보험금 지급 화면에서 지급품의서를 확인할 수 있습니다.</p>
          </aside>
        )}

        {document ? (
          <>
            <PaymentApprovalLookupCard document={document} />
            <PaymentApprovalDecisionPanel
              document={document}
              loadingAction={loadingAction}
              onApprove={() =>
                handlePaymentAction('approve', approvePaymentApprovalDocument, '지급품의서가 승인되었습니다.')
              }
              onReject={() =>
                handlePaymentAction('reject', rejectPaymentApprovalDocument, '지급품의서가 반려되었습니다.')
              }
            />
            <PaymentExecutionPanel
              document={document}
              loadingAction={loadingAction}
              onPay={() => handlePaymentAction('pay', payPaymentApprovalDocument, '보험금 지급 처리가 완료되었습니다.')}
            />
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

function PaymentApprovalDecisionPanel({
  document,
  loadingAction,
  onApprove,
  onReject
}: {
  document: PaymentApprovalDocumentResponse;
  loadingAction: PaymentAction;
  onApprove: () => void;
  onReject: () => void;
}) {
  const isApprovalRequested = document.submissionStatus === 'APPROVAL_REQUESTED';
  const isProcessing = loadingAction === 'approve' || loadingAction === 'reject';

  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>결재 승인/반려</h2>
          <p>
            {isApprovalRequested
              ? '결재 요청된 지급품의서를 승인하거나 반려합니다.'
              : `현재 상태는 ${getSubmissionStatusLabel(document.submissionStatus)}입니다.`}
          </p>
        </div>
        <CheckCircle2 aria-hidden="true" size={22} />
      </div>
      <div className="form-actions">
        <button className="button primary" type="button" onClick={onApprove} disabled={!isApprovalRequested || isProcessing}>
          <CheckCircle2 aria-hidden="true" size={16} />
          {loadingAction === 'approve' ? '승인 처리 중...' : '승인'}
        </button>
        <button className="button secondary" type="button" onClick={onReject} disabled={!isApprovalRequested || isProcessing}>
          <XCircle aria-hidden="true" size={16} />
          {loadingAction === 'reject' ? '반려 처리 중...' : '반려'}
        </button>
      </div>
      {!isApprovalRequested && (
        <p className="empty-value">반려 사유 입력 기능은 다음 단계에서 구현 예정입니다.</p>
      )}
    </section>
  );
}

function PaymentExecutionPanel({
  document,
  loadingAction,
  onPay
}: {
  document: PaymentApprovalDocumentResponse;
  loadingAction: PaymentAction;
  onPay: () => void;
}) {
  const canPay = document.submissionStatus === 'APPROVED';
  const isPaid = document.submissionStatus === 'PAID';
  const isRejected = document.submissionStatus === 'REJECTED';

  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>실제 보험금 지급</h2>
          <p>{getPaymentExecutionMessage(document.submissionStatus)}</p>
        </div>
        <CreditCard aria-hidden="true" size={22} />
      </div>
      <button className="button primary" type="button" onClick={onPay} disabled={!canPay || loadingAction === 'pay'}>
        <CreditCard aria-hidden="true" size={16} />
        {loadingAction === 'pay' ? '지급 처리 중...' : isPaid ? '지급 완료' : '실제 지급 처리'}
      </button>
      {isRejected && <p className="empty-value">반려 상태에서는 실제 보험금 지급 처리를 진행할 수 없습니다.</p>}
      {isPaid && <p className="empty-value">외부 지급 시스템 연동은 다음 단계에서 구현 예정입니다.</p>}
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

function getPaymentExecutionMessage(status: string) {
  if (status === 'APPROVED') {
    return '승인된 지급품의서입니다. 실제 지급 처리를 진행할 수 있습니다.';
  }
  if (status === 'PAID') {
    return '지급 처리 결과를 DB에 반영했습니다. 외부 지급 시스템 연동은 다음 단계에서 구현 예정입니다.';
  }
  if (status === 'REJECTED') {
    return '반려된 지급품의서는 실제 지급 처리할 수 없습니다.';
  }
  return '결재 승인 이후 실제 지급 처리가 가능합니다.';
}
