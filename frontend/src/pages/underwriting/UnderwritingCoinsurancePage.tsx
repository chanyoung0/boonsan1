import { KeyboardEvent, useState } from 'react';
import { Building2, ClipboardList, FileCheck2, Search } from 'lucide-react';
import {
  createCoinsuranceProcess,
  getCoinsuranceEligibility,
  getCoinsuranceProcess,
  updateCoinsuranceResult
} from '../../api/underwritingApi';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  CoinsuranceProcessResponse,
  UnderwritingFollowUpEligibilityResponse
} from '../../types/underwriting';
import {
  FOLLOW_UP_STATUS_LABELS,
  UNDERWRITING_RESULT_LABELS
} from '../../types/underwriting';

type LoadingAction = 'lookup' | 'request' | 'result' | null;

const initialRequestForm = {
  coinsurerName: '공동인수사 Mock',
  retainedAmount: '50000000',
  shareRate: '30',
  manualSelected: false
};

const initialResultForm = {
  resultStatus: 'APPROVED' as 'APPROVED' | 'REJECTED',
  rejectionReason: ''
};

export function UnderwritingCoinsurancePage() {
  const [applicationIdInput, setApplicationIdInput] = useState('');
  const [eligibility, setEligibility] = useState<UnderwritingFollowUpEligibilityResponse | null>(null);
  const [process, setProcess] = useState<CoinsuranceProcessResponse | null>(null);
  const [requestForm, setRequestForm] = useState(initialRequestForm);
  const [resultForm, setResultForm] = useState(initialResultForm);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const applicationId = eligibility?.applicationId ?? applicationIdInput.trim();
  const finalAccepted = eligibility?.finalResult === 'APPROVED' || eligibility?.finalResult === 'SURCHARGE';
  const canRequest = Boolean(eligibility && !process && finalAccepted && (eligibility.eligible || requestForm.manualSelected));
  const canSaveResult = Boolean(process && process.resultStatus === 'PENDING_APPROVAL');

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
      const eligibilityResponse = await getCoinsuranceEligibility(trimmed);
      setEligibility(eligibilityResponse);
      setApplicationIdInput(eligibilityResponse.applicationId);
      await loadProcess(eligibilityResponse.applicationId);
      setSuccess('공동인수 가능 여부를 조회했습니다.');
    } catch (caught) {
      setEligibility(null);
      setProcess(null);
      setError(caught instanceof Error ? caught.message : '공동인수 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const loadProcess = async (targetApplicationId: string) => {
    try {
      setProcess(await getCoinsuranceProcess(targetApplicationId));
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
      const response = await createCoinsuranceProcess(applicationId, {
        coinsurerName: requestForm.coinsurerName,
        retainedAmount: toNumber(requestForm.retainedAmount),
        shareRate: toNumber(requestForm.shareRate),
        manualSelected: requestForm.manualSelected
      });
      setProcess(response);
      setSuccess('공동인수 요청을 등록했습니다.');
      setEligibility(await getCoinsuranceEligibility(applicationId));
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '공동인수 요청 등록에 실패했습니다.');
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
      const response = await updateCoinsuranceResult(applicationId, {
        resultStatus: resultForm.resultStatus,
        rejectionReason: resultForm.resultStatus === 'REJECTED' ? toNullable(resultForm.rejectionReason) : null
      });
      setProcess(response);
      setEligibility(await getCoinsuranceEligibility(applicationId));
      setSuccess('공동인수 결과를 저장했습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '공동인수 결과 저장에 실패했습니다.');
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
    <AppLayout activeMenuId="underwriting-coinsurance">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>청약 심사</span>
            <span aria-hidden="true">/</span>
            <strong>공동인수 처리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>공동인수 처리</h1>
              <p>공동인수 추천 또는 수동 선택 청약에 대해 참여 요청과 결과를 저장합니다.</p>
            </div>
            <span className="page-kicker">청약 심사 · 공동인수</span>
          </div>
        </header>

        <section className="work-panel search-panel">
          <div className="panel-header compact">
            <div>
              <h2>청약번호 조회</h2>
              <p>최종 심사 결과와 공동인수 추천 여부를 확인합니다.</p>
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

        {eligibility && <EligibilityCard title="공동인수 가능 여부" eligibility={eligibility} />}

        <section className="work-panel underwriting-form-panel">
          <div className="panel-header compact">
            <div>
              <h2>공동인수 요청</h2>
              <p>실제 공동인수사 API는 호출하지 않고, 요청 내용을 DB에 저장합니다.</p>
            </div>
            <Building2 aria-hidden="true" size={22} />
          </div>
          <div className="field-grid three">
            <TextField label="공동인수사명" value={requestForm.coinsurerName} onChange={(value) => setRequestForm({ ...requestForm, coinsurerName: value })} />
            <TextField label="보유액" type="number" value={requestForm.retainedAmount} onChange={(value) => setRequestForm({ ...requestForm, retainedAmount: value })} />
            <TextField label="분담률(%)" type="number" value={requestForm.shareRate} onChange={(value) => setRequestForm({ ...requestForm, shareRate: value })} />
          </div>
          <label className="credit-check-field followup-check">
            <input
              type="checkbox"
              checked={requestForm.manualSelected}
              onChange={(event) => setRequestForm({ ...requestForm, manualSelected: event.target.checked })}
            />
            <span>공동인수 추천 대상이 아니어도 업무 판단으로 수동 선택합니다.</span>
          </label>
          <p className="inline-note">외부 공동인수사 연동은 다음 단계 예정이며, 현재는 요청과 결과만 저장합니다.</p>
          <div className="form-actions">
            <button className="button primary" type="button" onClick={handleCreateRequest} disabled={!canRequest || loadingAction === 'request'}>
              {loadingAction === 'request' ? '요청 등록 중...' : process ? '공동인수 요청 등록 완료' : '공동인수 요청 등록'}
            </button>
          </div>
        </section>

        {process && <CoinsuranceProcessCard process={process} />}

        <section className="work-panel underwriting-form-panel">
          <div className="panel-header compact">
            <div>
              <h2>공동인수 결과 입력</h2>
              <p>외부 API 결과 대신 수동으로 공동인수 승인 또는 거절 결과를 저장합니다.</p>
            </div>
            <FileCheck2 aria-hidden="true" size={22} />
          </div>
          <div className="field-grid two">
            <label className="field">
              <span>공동인수 결과</span>
              <select value={resultForm.resultStatus} onChange={(event) => setResultForm({ ...resultForm, resultStatus: event.target.value as 'APPROVED' | 'REJECTED' })}>
                <option value="APPROVED">승인</option>
                <option value="REJECTED">거절</option>
              </select>
            </label>
            <TextField label="거절 사유" value={resultForm.rejectionReason} onChange={(value) => setResultForm({ ...resultForm, rejectionReason: value })} />
          </div>
          <div className="form-actions">
            <button className="button primary" type="button" onClick={handleSaveResult} disabled={!canSaveResult || loadingAction === 'result'}>
              {loadingAction === 'result' ? '결과 저장 중...' : canSaveResult ? '공동인수 결과 저장' : '결과 저장 불가'}
            </button>
          </div>
        </section>
      </div>
    </AppLayout>
  );
}

function EligibilityCard({ title, eligibility }: { title: string; eligibility: UnderwritingFollowUpEligibilityResponse }) {
  return (
    <section className={`work-panel detail-panel ${eligibility.eligible ? 'success-panel' : ''}`}>
      <div className="panel-header compact">
        <div>
          <h2>{title}</h2>
          <p>{eligibility.reason}</p>
        </div>
        <ClipboardList aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="청약번호" value={eligibility.applicationId} mono />
        <AmountItem label="최종 심사 결과" value={eligibility.finalResult ? getResultLabel(eligibility.finalResult) : '미완료'} strong />
        <AmountItem label="심사 점수" value={eligibility.totalScore !== null ? `${Math.round(eligibility.totalScore)}점` : '미산정'} />
        <AmountItem label="공동인수 추천" value={eligibility.coinsuranceRecommended ? '예' : '아니오'} strong={eligibility.coinsuranceRecommended} />
      </dl>
      {eligibility.nextStepMessage && <p className="inline-note">{eligibility.nextStepMessage}</p>}
    </section>
  );
}

function CoinsuranceProcessCard({ process }: { process: CoinsuranceProcessResponse }) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>공동인수 처리 결과</h2>
          <p>{process.processId}</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="처리번호" value={process.processId} mono />
        <AmountItem label="청약번호" value={process.applicationId} mono />
        <AmountItem label="공동인수사" value={process.coinsurerName} />
        <AmountItem label="요청 상태" value={getStatusLabel(process.requestStatus)} />
        <AmountItem label="결과 상태" value={getStatusLabel(process.resultStatus)} strong />
        <AmountItem label="보유액" value={process.retainedAmount} />
        <AmountItem label="분담률" value={`${process.shareRate}%`} />
        <AmountItem label="수동 선택" value={process.manualSelected ? '예' : '아니오'} />
      </dl>
      <div className="text-detail-blocks">
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
