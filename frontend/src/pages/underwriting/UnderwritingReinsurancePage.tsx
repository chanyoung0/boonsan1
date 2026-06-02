import { KeyboardEvent, useState } from 'react';
import { FileCheck2, Landmark, Search } from 'lucide-react';
import {
  createReinsuranceProcess,
  getReinsuranceEligibility,
  getReinsuranceProcess,
  updateReinsuranceResult
} from '../../api/underwritingApi';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  ReinsuranceProcessResponse,
  UnderwritingFollowUpEligibilityResponse
} from '../../types/underwriting';
import {
  FOLLOW_UP_STATUS_LABELS,
  UNDERWRITING_RESULT_LABELS
} from '../../types/underwriting';

type LoadingAction = 'lookup' | 'request' | 'result' | null;

const initialRequestForm = {
  reinsurerName: '재보험사 Mock',
  retentionAmount: '300000000',
  cessionRate: '40'
};

const initialResultForm = {
  resultStatus: 'ACCEPTED' as 'ACCEPTED' | 'REJECTED',
  rejectionReason: ''
};

export function UnderwritingReinsurancePage() {
  const [applicationIdInput, setApplicationIdInput] = useState('');
  const [eligibility, setEligibility] = useState<UnderwritingFollowUpEligibilityResponse | null>(null);
  const [process, setProcess] = useState<ReinsuranceProcessResponse | null>(null);
  const [requestForm, setRequestForm] = useState(initialRequestForm);
  const [resultForm, setResultForm] = useState(initialResultForm);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const applicationId = eligibility?.applicationId ?? applicationIdInput.trim();
  const canRequest = Boolean(eligibility?.eligible && !process);
  const canSaveResult = Boolean(process && process.resultStatus === 'REQUESTED');

  const handleLookup = async () => {
    const trimmed = applicationIdInput.trim();
    if (!trimmed) {
      setError('청약번호를 입력해주세요.');
      return;
    }
    setLoadingAction('lookup');
    setError(null);
    setSuccess(null);
    try {
      const eligibilityResponse = await getReinsuranceEligibility(trimmed);
      setEligibility(eligibilityResponse);
      setApplicationIdInput(eligibilityResponse.applicationId);
      await loadProcess(eligibilityResponse.applicationId);
      setSuccess('재보험 필요 여부를 조회했습니다.');
    } catch (caught) {
      setEligibility(null);
      setProcess(null);
      setError(caught instanceof Error ? caught.message : '재보험 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const loadProcess = async (targetApplicationId: string) => {
    try {
      setProcess(await getReinsuranceProcess(targetApplicationId));
    } catch {
      setProcess(null);
    }
  };

  const handleCreateRequest = async () => {
    if (!applicationId) return;
    setLoadingAction('request');
    setError(null);
    setSuccess(null);
    try {
      const response = await createReinsuranceProcess(applicationId, {
        reinsurerName: requestForm.reinsurerName,
        retentionAmount: toNumber(requestForm.retentionAmount),
        cessionRate: toNumber(requestForm.cessionRate)
      });
      setProcess(response);
      setEligibility(await getReinsuranceEligibility(applicationId));
      setSuccess('재보험 요청을 등록했습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '재보험 요청 등록에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleSaveResult = async () => {
    if (!applicationId) return;
    setLoadingAction('result');
    setError(null);
    setSuccess(null);
    try {
      const response = await updateReinsuranceResult(applicationId, {
        resultStatus: resultForm.resultStatus,
        rejectionReason: resultForm.resultStatus === 'REJECTED' ? toNullable(resultForm.rejectionReason) : null
      });
      setProcess(response);
      setEligibility(await getReinsuranceEligibility(applicationId));
      setSuccess('재보험 결과를 저장했습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '재보험 결과 저장에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleLookupKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleLookup();
    }
  };

  return (
    <AppLayout activeMenuId="underwriting-reinsurance">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>청약 심사</span>
            <span aria-hidden="true">/</span>
            <strong>재보험 처리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>재보험 처리</h1>
              <p>가입금액, 심사점수, 신용 위험등급을 기준으로 Mock 재보험 필요 여부와 결과를 관리합니다.</p>
            </div>
            <span className="page-kicker">청약 심사 · 재보험</span>
          </div>
        </header>

        <section className="work-panel search-panel">
          <div className="panel-header compact">
            <div>
              <h2>청약번호 조회</h2>
              <p>재보험 필요 여부를 확인하고 요청 이력을 조회합니다.</p>
            </div>
            <Search aria-hidden="true" size={22} />
          </div>
          <div className="search-row">
            <input
              aria-label="청약번호"
              value={applicationIdInput}
              onChange={(event) => setApplicationIdInput(event.target.value)}
              onKeyDown={handleLookupKeyDown}
              placeholder="APP-2026-000001"
              disabled={loadingAction === 'lookup'}
            />
            <button className="button primary" type="button" onClick={handleLookup} disabled={loadingAction === 'lookup'}>
              <Search aria-hidden="true" size={16} />
              {loadingAction === 'lookup' ? '조회 중...' : '조회'}
            </button>
          </div>
        </section>

        {error && <AlertMessage type="error" message={error} />}
        {success && <AlertMessage type="success" message={success} />}

        {eligibility && <EligibilityCard eligibility={eligibility} />}

        <section className="work-panel underwriting-form-panel">
          <div className="panel-header compact">
            <div>
              <h2>재보험 요청</h2>
              <p>실제 재보험사 연동 없이 출재 요청 정보를 저장합니다.</p>
            </div>
            <Landmark aria-hidden="true" size={22} />
          </div>
          <div className="field-grid three">
            <TextField label="재보험사명" value={requestForm.reinsurerName} onChange={(value) => setRequestForm({ ...requestForm, reinsurerName: value })} />
            <TextField label="보유금액" type="number" value={requestForm.retentionAmount} onChange={(value) => setRequestForm({ ...requestForm, retentionAmount: value })} />
            <TextField label="출재율(%)" type="number" value={requestForm.cessionRate} onChange={(value) => setRequestForm({ ...requestForm, cessionRate: value })} />
          </div>
          <p className="inline-note">Mock 기준상 재보험 필요 청약만 요청 등록할 수 있습니다.</p>
          <div className="form-actions">
            <button className="button primary" type="button" onClick={handleCreateRequest} disabled={!canRequest || loadingAction === 'request'}>
              {loadingAction === 'request' ? '요청 등록 중...' : process ? '재보험 요청 등록 완료' : '재보험 요청 등록'}
            </button>
          </div>
        </section>

        {process && <ReinsuranceProcessCard process={process} />}

        <section className="work-panel underwriting-form-panel">
          <div className="panel-header compact">
            <div>
              <h2>재보험 결과 입력</h2>
              <p>재보험사 수락 또는 거절 결과를 수동으로 저장합니다.</p>
            </div>
            <FileCheck2 aria-hidden="true" size={22} />
          </div>
          <div className="field-grid two">
            <label className="field">
              <span>재보험 결과</span>
              <select value={resultForm.resultStatus} onChange={(event) => setResultForm({ ...resultForm, resultStatus: event.target.value as 'ACCEPTED' | 'REJECTED' })}>
                <option value="ACCEPTED">수락</option>
                <option value="REJECTED">거절</option>
              </select>
            </label>
            <TextField label="거절 사유" value={resultForm.rejectionReason} onChange={(value) => setResultForm({ ...resultForm, rejectionReason: value })} />
          </div>
          <div className="form-actions">
            <button className="button primary" type="button" onClick={handleSaveResult} disabled={!canSaveResult || loadingAction === 'result'}>
              {loadingAction === 'result' ? '결과 저장 중...' : canSaveResult ? '재보험 결과 저장' : '결과 저장 불가'}
            </button>
          </div>
        </section>
      </div>
    </AppLayout>
  );
}

function EligibilityCard({ eligibility }: { eligibility: UnderwritingFollowUpEligibilityResponse }) {
  return (
    <section className={`work-panel detail-panel ${eligibility.eligible ? 'success-panel' : ''}`}>
      <div className="panel-header compact">
        <div>
          <h2>재보험 필요 여부</h2>
          <p>{eligibility.reason}</p>
        </div>
        <Landmark aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="청약번호" value={eligibility.applicationId} mono />
        <AmountItem label="최종 심사 결과" value={eligibility.finalResult ? getResultLabel(eligibility.finalResult) : '미완료'} strong />
        <AmountItem label="심사 점수" value={eligibility.totalScore !== null ? `${Math.round(eligibility.totalScore)}점` : '미산정'} />
        <AmountItem label="재보험 필요" value={eligibility.reinsuranceRequired ? '예' : '아니오'} strong={eligibility.reinsuranceRequired} />
      </dl>
      {eligibility.nextStepMessage && <p className="inline-note">{eligibility.nextStepMessage}</p>}
    </section>
  );
}

function ReinsuranceProcessCard({ process }: { process: ReinsuranceProcessResponse }) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>재보험 처리 결과</h2>
          <p>{process.processId}</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="처리번호" value={process.processId} mono />
        <AmountItem label="청약번호" value={process.applicationId} mono />
        <AmountItem label="재보험사" value={process.reinsurerName} />
        <AmountItem label="요청 상태" value={getStatusLabel(process.requestStatus)} />
        <AmountItem label="결과 상태" value={getStatusLabel(process.resultStatus)} strong />
        <AmountItem label="보유금액" value={process.retentionAmount} />
        <AmountItem label="출재율" value={`${process.cessionRate}%`} />
        <AmountItem label="재보험 필요" value={process.reinsuranceRequired ? '예' : '아니오'} />
      </dl>
      <div className="text-detail-blocks">
        <TextBlock title="필요 사유" value={process.reinsuranceReason} />
        <TextBlock title="외부 연동 예정 안내" value={process.externalSystemMessage} />
        {process.rejectionReason && <TextBlock title="거절 사유" value={process.rejectionReason} />}
      </div>
    </section>
  );
}

function TextField({ label, value, onChange, type = 'text' }: { label: string; value: string; onChange: (value: string) => void; type?: string }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function AmountItem({ label, value, strong = false, mono = false }: { label: string; value: number | string; strong?: boolean; mono?: boolean }) {
  const displayValue = typeof value === 'number' ? `${Math.round(value).toLocaleString('ko-KR')}원` : value;
  return (
    <div className={`amount-item ${strong ? 'strong' : ''}`}>
      <dt>{label}</dt>
      <dd className={mono ? 'mono' : ''} title={displayValue}>{displayValue}</dd>
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

function getResultLabel(value: string) {
  return UNDERWRITING_RESULT_LABELS[value] ?? value;
}

function getStatusLabel(value: string) {
  return FOLLOW_UP_STATUS_LABELS[value] ?? value;
}

function toNumber(value: string) {
  const numberValue = Number(value);
  return Number.isFinite(numberValue) ? numberValue : 0;
}

function toNullable(value: string) {
  const trimmed = value.trim();
  return trimmed ? trimmed : null;
}
