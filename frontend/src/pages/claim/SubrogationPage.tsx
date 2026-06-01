import { FormEvent, KeyboardEvent, useState } from 'react';
import { BadgeCheck, FileSearch, RotateCcw, Search } from 'lucide-react';
import { ApiError } from '../../api/apiClient';
import {
  completeSubrogation,
  createSubrogation,
  getSubrogation,
  getSubrogationEligibility
} from '../../api/claimApi';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  AccidentStatus,
  SubrogationCreateRequest,
  SubrogationEligibilityResponse,
  SubrogationResponse
} from '../../types/claim';
import { getAccidentStatusLabel } from '../../types/claim';

const PAYMENT_STATUS_LABELS: Record<string, string> = {
  APPROVAL_REQUESTED: '결재 요청',
  APPROVED: '승인',
  REJECTED: '반려',
  PAID: '지급 완료'
};

const SUBROGATION_STATUS_LABELS: Record<string, string> = {
  PENDING: '대기',
  IN_PROGRESS: '구상 요청',
  COMPLETED: '회수 완료',
  OBJECTED: '이의 제기',
  CLOSED: '종결'
};

type LoadingAction = 'lookup' | 'create' | 'complete' | null;

const initialCreateForm = {
  targetName: '',
  subrogationReason: '',
  subrogationAmount: '',
  employeeNo: ''
};

export function SubrogationPage() {
  const [accidentNumberInput, setAccidentNumberInput] = useState('');
  const [eligibility, setEligibility] = useState<SubrogationEligibilityResponse | null>(null);
  const [subrogation, setSubrogation] = useState<SubrogationResponse | null>(null);
  const [createForm, setCreateForm] = useState(initialCreateForm);
  const [recoveredAmount, setRecoveredAmount] = useState('');
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleLookup = async () => {
    const accidentNumber = accidentNumberInput.trim();
    if (!accidentNumber) {
      setError('사고번호를 입력하세요.');
      setSuccess(null);
      setEligibility(null);
      setSubrogation(null);
      return;
    }

    setLoadingAction('lookup');
    setError(null);
    setSuccess(null);
    setSubrogation(null);

    try {
      const eligibilityResponse = await getSubrogationEligibility(accidentNumber);
      setEligibility(eligibilityResponse);
      setAccidentNumberInput(eligibilityResponse.accidentNumber);
      await loadExistingSubrogation(eligibilityResponse.accidentNumber);
    } catch (caught) {
      setEligibility(null);
      setSubrogation(null);
      setError(caught instanceof Error ? caught.message : '구상 처리 가능 여부 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const loadExistingSubrogation = async (accidentNumber: string) => {
    try {
      const existing = await getSubrogation(accidentNumber);
      setSubrogation(existing);
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setSubrogation(null);
        return;
      }
      throw caught;
    }
  };

  const handleCreate = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!eligibility?.eligible) {
      setError('지급 완료된 사고만 구상 요청을 등록할 수 있습니다.');
      setSuccess(null);
      return;
    }

    const subrogationAmount = Number(createForm.subrogationAmount);
    if (!Number.isFinite(subrogationAmount) || subrogationAmount <= 0) {
      setError('구상 청구 금액을 0원보다 크게 입력하세요.');
      setSuccess(null);
      return;
    }
    if (eligibility.paidAmount !== null && subrogationAmount > eligibility.paidAmount) {
      setError('구상 청구 금액은 지급 완료 금액을 초과할 수 없습니다.');
      setSuccess(null);
      return;
    }

    const request: SubrogationCreateRequest = {
      targetName: createForm.targetName.trim(),
      subrogationReason: createForm.subrogationReason.trim(),
      subrogationAmount,
      employeeNo: createForm.employeeNo.trim()
    };

    setLoadingAction('create');
    setError(null);
    setSuccess(null);
    try {
      const response = await createSubrogation(eligibility.accidentNumber, request);
      setSubrogation(response);
      setCreateForm(initialCreateForm);
      setSuccess('구상 요청이 등록되었습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '구상 요청 등록에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleComplete = async () => {
    if (!subrogation) return;

    const amount = Number(recoveredAmount);
    if (!Number.isFinite(amount) || amount <= 0) {
      setError('회수 완료 금액을 0원보다 크게 입력하세요.');
      setSuccess(null);
      return;
    }
    if (amount > subrogation.subrogationAmount) {
      setError('회수 완료 금액은 구상 청구 금액을 초과할 수 없습니다.');
      setSuccess(null);
      return;
    }

    setLoadingAction('complete');
    setError(null);
    setSuccess(null);
    try {
      const response = await completeSubrogation(subrogation.accidentNumber, { recoveredAmount: amount });
      setSubrogation(response);
      setRecoveredAmount('');
      setSuccess('구상 회수 완료 처리되었습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '구상 회수 완료 처리에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleLookup();
    }
  };

  return (
    <AppLayout activeMenuId="claim-subrogation">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>보상 처리</span>
            <span aria-hidden="true">/</span>
            <strong>구상 처리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>구상 처리</h1>
              <p>지급 완료된 사고에 대해 구상 요청을 등록하고 회수 완료 상태를 관리합니다.</p>
            </div>
            <span className="page-kicker">보상 처리 · 구상 대기</span>
          </div>
        </header>

        <section className="work-panel search-panel investigation-lookup-panel">
          <div className="panel-header compact">
            <div>
              <h2>사고번호 조회</h2>
              <p>보험금 지급 완료 여부를 확인한 뒤 구상 처리를 진행합니다.</p>
            </div>
            <FileSearch aria-hidden="true" size={22} />
          </div>
          <div className="search-row">
            <input
              aria-label="사고번호"
              value={accidentNumberInput}
              onChange={(event) => setAccidentNumberInput(event.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ACC-2026-443688"
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

        {eligibility ? (
          <>
            <SubrogationEligibilityCard eligibility={eligibility} />
            {eligibility.eligible && (
              <PaymentCompletedCard eligibility={eligibility} subrogation={subrogation} />
            )}
            {eligibility.eligible && !subrogation && (
              <SubrogationCreateForm
                form={createForm}
                paidAmount={eligibility.paidAmount ?? 0}
                loading={loadingAction === 'create'}
                onChange={setCreateForm}
                onSubmit={handleCreate}
              />
            )}
            {subrogation && (
              <>
                <SubrogationDetailCard subrogation={subrogation} />
                <SubrogationCompletePanel
                  subrogation={subrogation}
                  recoveredAmount={recoveredAmount}
                  loading={loadingAction === 'complete'}
                  onRecoveredAmountChange={setRecoveredAmount}
                  onComplete={handleComplete}
                />
              </>
            )}
          </>
        ) : (
          <aside className="work-panel empty-result">
            <strong>구상 처리 조회 대기</strong>
            <p>사고번호를 입력하면 지급 완료 여부와 등록된 구상 요청이 여기에 표시됩니다.</p>
          </aside>
        )}
      </div>
    </AppLayout>
  );
}

function SubrogationEligibilityCard({ eligibility }: { eligibility: SubrogationEligibilityResponse }) {
  return (
    <section className={`work-panel detail-panel approval-card ${eligibility.eligible ? 'next-step-panel' : ''}`}>
      <div className="panel-header compact">
        <div>
          <h2>구상 가능 여부</h2>
          <p>{getEligibilityMessage(eligibility)}</p>
        </div>
        <BadgeCheck aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="사고번호" value={eligibility.accidentNumber} mono />
        <AmountItem label="지급품의서 번호" value={eligibility.documentId || '미생성'} mono />
        <AmountItem label="지급 상태" value={getPaymentStatusLabel(eligibility.paymentStatus)} strong={eligibility.paymentStatus === 'PAID'} />
        <AmountItem label="사고 상태" value={getAccidentStatusDisplay(eligibility.accidentStatus)} strong={eligibility.accidentStatus === 'COMPLETED'} />
      </dl>
    </section>
  );
}

function PaymentCompletedCard({
  eligibility,
  subrogation
}: {
  eligibility: SubrogationEligibilityResponse;
  subrogation: SubrogationResponse | null;
}) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>지급 완료 정보</h2>
          <p>구상 처리는 실제 지급이 완료된 사고에 대해서만 등록할 수 있습니다.</p>
        </div>
      </div>
      <dl className="amount-grid">
        <AmountItem label="사고번호" value={eligibility.accidentNumber} mono />
        <AmountItem label="지급품의서 번호" value={eligibility.documentId || '미생성'} mono />
        <AmountItem label="손해조사번호" value={eligibility.investigationId || '미생성'} mono />
        <AmountItem label="지급 완료 금액" value={eligibility.paidAmount ?? 0} strong />
        <AmountItem label="지급 상태" value={getPaymentStatusLabel(eligibility.paymentStatus)} />
        <AmountItem label="사고 상태" value={getAccidentStatusDisplay(eligibility.accidentStatus)} />
        <AmountItem label="구상 요청 상태" value={subrogation ? getSubrogationStatusLabel(subrogation.subrogationStatus) : '미등록'} />
      </dl>
    </section>
  );
}

function SubrogationCreateForm({
  form,
  paidAmount,
  loading,
  onChange,
  onSubmit
}: {
  form: typeof initialCreateForm;
  paidAmount: number;
  loading: boolean;
  onChange: (form: typeof initialCreateForm) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
}) {
  const amount = Number(form.subrogationAmount);
  const exceedsPaidAmount = Number.isFinite(amount) && amount > paidAmount;

  return (
    <form className="work-panel form-panel" onSubmit={onSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>구상 요청 등록</h2>
          <p>외부 구상/회수 시스템 연동은 다음 단계에서 구현 예정입니다.</p>
        </div>
      </div>

      <div className="field-grid two">
        <label className="field">
          <span>구상 대상자 또는 기관</span>
          <input
            value={form.targetName}
            onChange={(event) => onChange({ ...form, targetName: event.target.value })}
            placeholder="상대 운전자 또는 기관명"
            required
          />
        </label>
        <label className="field">
          <span>담당 사원번호</span>
          <input
            value={form.employeeNo}
            onChange={(event) => onChange({ ...form, employeeNo: event.target.value })}
            placeholder="EMP-SUB-001"
            required
          />
        </label>
        <label className="field">
          <span>구상 청구 금액</span>
          <input
            type="number"
            min="1"
            max={paidAmount}
            value={form.subrogationAmount}
            onChange={(event) => onChange({ ...form, subrogationAmount: event.target.value })}
            placeholder={String(Math.round(paidAmount))}
            required
          />
        </label>
        <label className="field">
          <span>지급 완료 금액 한도</span>
          <input value={`${formatAmount(paidAmount)}원`} disabled readOnly />
        </label>
      </div>

      <label className="field form-section">
        <span>구상 사유</span>
        <textarea
          value={form.subrogationReason}
          onChange={(event) => onChange({ ...form, subrogationReason: event.target.value })}
          placeholder="제3자 과실, 계약상 회수 사유 등을 입력하세요."
          required
        />
      </label>

      {exceedsPaidAmount && <p className="empty-value">구상 청구 금액은 지급 완료 금액을 초과할 수 없습니다.</p>}

      <div className="form-actions">
        <button className="button primary" type="submit" disabled={loading || exceedsPaidAmount}>
          {loading ? '등록 중...' : '구상 요청 등록'}
        </button>
      </div>
    </form>
  );
}

function SubrogationDetailCard({ subrogation }: { subrogation: SubrogationResponse }) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>구상 요청 상세</h2>
          <p>{subrogation.subrogationId}</p>
        </div>
        <RotateCcw aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="구상번호" value={subrogation.subrogationId} mono />
        <AmountItem label="사고번호" value={subrogation.accidentNumber} mono />
        <AmountItem label="구상 대상" value={subrogation.targetName} />
        <AmountItem label="구상 상태" value={getSubrogationStatusLabel(subrogation.subrogationStatus)} strong={subrogation.subrogationStatus === 'COMPLETED'} />
        <AmountItem label="구상 청구 금액" value={subrogation.subrogationAmount} strong />
        <AmountItem label="회수 완료 금액" value={subrogation.recoveredAmount ?? '미처리'} strong={subrogation.subrogationStatus === 'COMPLETED'} />
        <AmountItem label="담당 사원번호" value={subrogation.employeeNo} mono />
        <AmountItem label="회수 완료 일시" value={formatDateTime(subrogation.recoveredAt)} />
      </dl>
      <div className="text-detail-blocks approval-opinions">
        <section className="text-box">
          <h3>구상 사유</h3>
          <p>{subrogation.subrogationReason}</p>
        </section>
      </div>
    </section>
  );
}

function SubrogationCompletePanel({
  subrogation,
  recoveredAmount,
  loading,
  onRecoveredAmountChange,
  onComplete
}: {
  subrogation: SubrogationResponse;
  recoveredAmount: string;
  loading: boolean;
  onRecoveredAmountChange: (value: string) => void;
  onComplete: () => void;
}) {
  const canComplete = subrogation.subrogationStatus === 'IN_PROGRESS';

  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>구상 회수 완료</h2>
          <p>
            {canComplete
              ? '구상 요청 상태에서만 회수 완료 처리를 할 수 있습니다.'
              : '회수 완료 또는 종결 상태에서는 추가 완료 처리를 할 수 없습니다.'}
          </p>
        </div>
      </div>
      <div className="field-grid two">
        <label className="field">
          <span>회수 완료 금액</span>
          <input
            type="number"
            min="1"
            max={subrogation.subrogationAmount}
            value={recoveredAmount}
            onChange={(event) => onRecoveredAmountChange(event.target.value)}
            placeholder={String(Math.round(subrogation.subrogationAmount))}
            disabled={!canComplete}
          />
        </label>
        <label className="field">
          <span>외부 연동 안내</span>
          <input value="외부 구상/회수 시스템 연동 예정" disabled readOnly />
        </label>
      </div>
      <div className="form-actions">
        <button className="button primary" type="button" onClick={onComplete} disabled={!canComplete || loading}>
          {loading ? '완료 처리 중...' : subrogation.subrogationStatus === 'COMPLETED' ? '회수 완료' : '회수 완료 처리'}
        </button>
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
  const displayValue = typeof value === 'number' ? `${formatAmount(value)}원` : value;
  return (
    <div className={`amount-item ${strong ? 'strong' : ''}`}>
      <dt>{label}</dt>
      <dd className={mono ? 'mono' : ''} title={displayValue}>
        {displayValue}
      </dd>
    </div>
  );
}

function getPaymentStatusLabel(status: string | null) {
  return status ? PAYMENT_STATUS_LABELS[status] ?? status : '미생성';
}

function getSubrogationStatusLabel(status: string) {
  return SUBROGATION_STATUS_LABELS[status] ?? status;
}

function getAccidentStatusDisplay(status: AccidentStatus | null) {
  return status ? getAccidentStatusLabel(status) : '미확인';
}

function getEligibilityMessage(eligibility: SubrogationEligibilityResponse) {
  if (eligibility.eligible) {
    return '지급 완료된 사고로 구상 요청을 등록할 수 있습니다.';
  }
  if (!eligibility.documentId) {
    return '해당 사고번호의 지급품의서가 없습니다.';
  }
  return '보험금 지급 완료 후 구상 처리를 진행할 수 있습니다.';
}

function formatAmount(value: number) {
  return Math.round(value).toLocaleString('ko-KR');
}

function formatDateTime(value: string | null) {
  if (!value) return '미처리';
  return new Intl.DateTimeFormat('ko-KR', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value));
}
