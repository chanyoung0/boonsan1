import { FormEvent, KeyboardEvent, useState } from 'react';
import { FileQuestion, Gavel, RotateCcw, Search, ShieldAlert } from 'lucide-react';
import { ApiError } from '../../api/apiClient';
import {
  completeObjection,
  createObjection,
  getObjection,
  getObjectionEligibility,
  markObjectionReinvestigationRequired,
  rejectObjection,
  transferObjectionToLegal
} from '../../api/claimApi';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  AccidentStatus,
  ObjectionCreateRequest,
  ObjectionEligibilityResponse,
  ObjectionResponse
} from '../../types/claim';
import { getAccidentStatusLabel } from '../../types/claim';

const PAYMENT_STATUS_LABELS: Record<string, string> = {
  DRAFT: '초안',
  OPINION_SAVED: '소견 저장',
  APPROVAL_REQUESTED: '결재 요청',
  APPROVED: '승인',
  REJECTED: '반려',
  PAID: '지급 완료'
};

const OBJECTION_STATUS_LABELS: Record<string, string> = {
  RECEIVED: '접수/검토 중',
  REINVESTIGATION_REQUIRED: '재조사 필요',
  REJECTED: '이의 기각',
  TRANSFERRED_TO_LEGAL: '법무팀 이관 필요',
  COMPLETED: '처리 완료'
};

type LoadingAction = 'lookup' | 'create' | 'reinvestigation' | 'reject' | 'legal' | 'complete' | null;

const initialForm = {
  claimantName: '',
  claimantPhone: '',
  objectionReason: '',
  requestedAction: '',
  employeeNo: ''
};

export function ObjectionPage() {
  const [accidentNumberInput, setAccidentNumberInput] = useState('');
  const [eligibility, setEligibility] = useState<ObjectionEligibilityResponse | null>(null);
  const [objection, setObjection] = useState<ObjectionResponse | null>(null);
  const [form, setForm] = useState(initialForm);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleLookup = async () => {
    const accidentNumber = accidentNumberInput.trim();
    if (!accidentNumber) {
      setError('사고번호를 입력하세요.');
      setSuccess(null);
      setEligibility(null);
      setObjection(null);
      return;
    }

    setLoadingAction('lookup');
    setError(null);
    setSuccess(null);
    setObjection(null);

    try {
      const eligibilityResponse = await getObjectionEligibility(accidentNumber);
      setEligibility(eligibilityResponse);
      setAccidentNumberInput(eligibilityResponse.accidentNumber);
      await loadExistingObjection(eligibilityResponse.accidentNumber);
    } catch (caught) {
      setEligibility(null);
      setObjection(null);
      setError(caught instanceof Error ? caught.message : '이의제기 가능 여부 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const loadExistingObjection = async (accidentNumber: string) => {
    try {
      const existing = await getObjection(accidentNumber);
      setObjection(existing);
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setObjection(null);
        return;
      }
      throw caught;
    }
  };

  const handleCreate = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!eligibility?.eligible) {
      setError('이의제기 가능 조건을 충족한 사고만 등록할 수 있습니다.');
      setSuccess(null);
      return;
    }

    const request: ObjectionCreateRequest = {
      claimantName: form.claimantName.trim(),
      claimantPhone: form.claimantPhone.trim(),
      objectionReason: form.objectionReason.trim(),
      requestedAction: form.requestedAction.trim(),
      employeeNo: form.employeeNo.trim()
    };

    setLoadingAction('create');
    setError(null);
    setSuccess(null);
    try {
      const response = await createObjection(eligibility.accidentNumber, request);
      setObjection(response);
      setForm(initialForm);
      setSuccess('이의제기가 등록되었습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '이의제기 등록에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleStatusAction = async (
    action: Exclude<LoadingAction, 'lookup' | 'create' | null>,
    request: (accidentNumber: string) => Promise<ObjectionResponse>,
    successMessage: string
  ) => {
    if (!objection) return;

    setLoadingAction(action);
    setError(null);
    setSuccess(null);
    try {
      const response = await request(objection.accidentNumber);
      setObjection(response);
      setSuccess(successMessage);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '이의제기 처리에 실패했습니다.');
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
    <AppLayout activeMenuId="claim-objection">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>보상 처리</span>
            <span aria-hidden="true">/</span>
            <strong>이의제기 처리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>이의제기 처리</h1>
              <p>지급 결과에 대한 이의제기를 접수하고 검토 상태를 관리합니다.</p>
            </div>
            <span className="page-kicker">보상 처리 · 이의제기</span>
          </div>
        </header>

        <section className="work-panel search-panel investigation-lookup-panel">
          <div className="panel-header compact">
            <div>
              <h2>사고번호 조회</h2>
              <p>지급 반려, 지급 완료, 종결 사고에 대해 이의제기 가능 여부를 확인합니다.</p>
            </div>
            <FileQuestion aria-hidden="true" size={22} />
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
            <ObjectionEligibilityCard eligibility={eligibility} />
            {eligibility.eligible && !objection && (
              <ObjectionCreateForm form={form} loading={loadingAction === 'create'} onChange={setForm} onSubmit={handleCreate} />
            )}
            {objection && (
              <>
                <ObjectionDetailCard objection={objection} />
                <ObjectionReviewPanel
                  objection={objection}
                  loadingAction={loadingAction}
                  onReinvestigation={() =>
                    handleStatusAction(
                      'reinvestigation',
                      markObjectionReinvestigationRequired,
                      '이의제기를 재조사 필요 상태로 처리했습니다.'
                    )
                  }
                  onReject={() =>
                    handleStatusAction('reject', rejectObjection, '이의제기를 기각 처리했습니다.')
                  }
                  onLegalTransfer={() =>
                    handleStatusAction(
                      'legal',
                      transferObjectionToLegal,
                      '법무팀 이관 필요 상태로 처리했습니다.'
                    )
                  }
                  onComplete={() =>
                    handleStatusAction('complete', completeObjection, '이의제기를 처리 완료했습니다.')
                  }
                />
              </>
            )}
          </>
        ) : (
          <aside className="work-panel empty-result">
            <strong>이의제기 조회 대기</strong>
            <p>사고번호를 입력하면 이의제기 가능 여부와 등록된 이의제기가 표시됩니다.</p>
          </aside>
        )}
      </div>
    </AppLayout>
  );
}

function ObjectionEligibilityCard({ eligibility }: { eligibility: ObjectionEligibilityResponse }) {
  return (
    <section className={`work-panel detail-panel approval-card ${eligibility.eligible ? 'next-step-panel' : ''}`}>
      <div className="panel-header compact">
        <div>
          <h2>이의제기 가능 여부</h2>
          <p>
            {eligibility.eligible
              ? '이 사고는 이의제기를 접수할 수 있습니다.'
              : eligibility.unavailableReason || '이의제기 가능 조건을 충족하지 않습니다.'}
          </p>
        </div>
        <ShieldAlert aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="사고번호" value={eligibility.accidentNumber} mono />
        <AmountItem label="사고 상태" value={getAccidentStatusDisplay(eligibility.accidentStatus)} />
        <AmountItem label="지급품의서 번호" value={eligibility.documentId || '미생성'} mono />
        <AmountItem label="지급품의서 상태" value={getPaymentStatusLabel(eligibility.paymentStatus)} strong={eligibility.eligible} />
        <AmountItem label="최종 지급 금액" value={eligibility.finalPaymentAmount ?? '미확정'} strong={eligibility.eligible} />
        <AmountItem label="이의제기 가능 여부" value={eligibility.eligible ? '가능' : '불가'} strong={eligibility.eligible} />
      </dl>
    </section>
  );
}

function ObjectionCreateForm({
  form,
  loading,
  onChange,
  onSubmit
}: {
  form: typeof initialForm;
  loading: boolean;
  onChange: (form: typeof initialForm) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
}) {
  return (
    <form className="work-panel form-panel" onSubmit={onSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>이의제기 등록</h2>
          <p>지급 결과에 대한 이의 사유와 요청 내용을 기록합니다.</p>
        </div>
      </div>

      <div className="field-grid two">
        <label className="field">
          <span>이의제기자 이름</span>
          <input
            value={form.claimantName}
            onChange={(event) => onChange({ ...form, claimantName: event.target.value })}
            placeholder="홍길동"
            required
          />
        </label>
        <label className="field">
          <span>연락처</span>
          <input
            value={form.claimantPhone}
            onChange={(event) => onChange({ ...form, claimantPhone: event.target.value })}
            placeholder="010-0000-0000"
            required
          />
        </label>
        <label className="field">
          <span>담당 사원번호</span>
          <input
            value={form.employeeNo}
            onChange={(event) => onChange({ ...form, employeeNo: event.target.value })}
            placeholder="EMP-OBJ-001"
            required
          />
        </label>
      </div>

      <div className="field-grid two form-section">
        <label className="field">
          <span>이의제기 사유</span>
          <textarea
            value={form.objectionReason}
            onChange={(event) => onChange({ ...form, objectionReason: event.target.value })}
            placeholder="지급 금액 또는 반려 결과에 대한 이의 사유를 입력하세요."
            required
          />
        </label>
        <label className="field">
          <span>요청 내용</span>
          <textarea
            value={form.requestedAction}
            onChange={(event) => onChange({ ...form, requestedAction: event.target.value })}
            placeholder="재검토 요청, 추가 서류 반영 요청 등을 입력하세요."
            required
          />
        </label>
      </div>

      <div className="form-actions">
        <button className="button primary" type="submit" disabled={loading}>
          {loading ? '등록 중...' : '이의제기 등록'}
        </button>
      </div>
    </form>
  );
}

function ObjectionDetailCard({ objection }: { objection: ObjectionResponse }) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>이의제기 상세</h2>
          <p>{objection.objectionId}</p>
        </div>
        <Gavel aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="이의제기번호" value={objection.objectionId} mono />
        <AmountItem label="사고번호" value={objection.accidentNumber} mono />
        <AmountItem label="이의제기자" value={objection.claimantName} />
        <AmountItem label="연락처" value={objection.claimantPhone} />
        <AmountItem label="지급품의서 상태" value={getPaymentStatusLabel(objection.paymentStatus)} />
        <AmountItem label="최종 지급 금액" value={objection.finalPaymentAmount ?? '미확정'} strong />
        <AmountItem label="이의제기 상태" value={getObjectionStatusLabel(objection.objectionStatus)} strong />
        <AmountItem label="담당 사원번호" value={objection.employeeNo} mono />
      </dl>
      <div className="text-detail-blocks approval-opinions">
        <section className="text-box">
          <h3>이의제기 사유</h3>
          <p>{objection.objectionReason}</p>
        </section>
        <section className="text-box">
          <h3>요청 내용</h3>
          <p>{objection.requestedAction}</p>
        </section>
      </div>
      {objection.objectionStatus === 'TRANSFERRED_TO_LEGAL' && (
        <p className="empty-value">법무팀 이관 예정 상태입니다. 실제 소송 기능은 이번 범위에 포함하지 않았습니다.</p>
      )}
    </section>
  );
}

function ObjectionReviewPanel({
  objection,
  loadingAction,
  onReinvestigation,
  onReject,
  onLegalTransfer,
  onComplete
}: {
  objection: ObjectionResponse;
  loadingAction: LoadingAction;
  onReinvestigation: () => void;
  onReject: () => void;
  onLegalTransfer: () => void;
  onComplete: () => void;
}) {
  const canReview = objection.objectionStatus === 'RECEIVED';
  const processing = loadingAction !== null;

  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>이의제기 검토 처리</h2>
          <p>
            {canReview
              ? '접수/검토 중 상태에서만 검토 처리를 진행할 수 있습니다.'
              : '최종 상태에서는 추가 검토 처리를 할 수 없습니다.'}
          </p>
        </div>
        <RotateCcw aria-hidden="true" size={22} />
      </div>
      <div className="form-actions">
        <button className="button secondary" type="button" onClick={onReinvestigation} disabled={!canReview || processing}>
          {loadingAction === 'reinvestigation' ? '처리 중...' : '재조사 필요'}
        </button>
        <button className="button secondary" type="button" onClick={onReject} disabled={!canReview || processing}>
          {loadingAction === 'reject' ? '처리 중...' : '기각'}
        </button>
        <button className="button secondary" type="button" onClick={onLegalTransfer} disabled={!canReview || processing}>
          {loadingAction === 'legal' ? '처리 중...' : '법무팀 이관 필요'}
        </button>
        <button className="button primary" type="button" onClick={onComplete} disabled={!canReview || processing}>
          {loadingAction === 'complete' ? '처리 중...' : '처리 완료'}
        </button>
      </div>
      <p className="empty-value">법무팀 이관은 안내 상태만 기록하며 Litigation/Lawsuit 기능은 생성하지 않습니다.</p>
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

function getPaymentStatusLabel(status: string | null) {
  return status ? PAYMENT_STATUS_LABELS[status] ?? status : '미생성';
}

function getObjectionStatusLabel(status: string) {
  return OBJECTION_STATUS_LABELS[status] ?? status;
}

function getAccidentStatusDisplay(status: AccidentStatus | null) {
  return status ? getAccidentStatusLabel(status) : '미확인';
}
