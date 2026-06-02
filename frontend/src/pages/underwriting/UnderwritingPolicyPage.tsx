import { KeyboardEvent, useState } from 'react';
import { FileBadge2, FileCheck2, Search } from 'lucide-react';
import {
  getPolicyIssue,
  getPolicyIssueEligibility,
  issuePolicy
} from '../../api/underwritingApi';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  PolicyIssueResponse,
  UnderwritingFollowUpEligibilityResponse
} from '../../types/underwriting';
import {
  FOLLOW_UP_STATUS_LABELS,
  UNDERWRITING_RESULT_LABELS
} from '../../types/underwriting';

type LoadingAction = 'lookup' | 'issue' | null;

export function UnderwritingPolicyPage() {
  const [applicationIdInput, setApplicationIdInput] = useState('');
  const [eligibility, setEligibility] = useState<UnderwritingFollowUpEligibilityResponse | null>(null);
  const [policyIssue, setPolicyIssue] = useState<PolicyIssueResponse | null>(null);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const applicationId = eligibility?.applicationId ?? applicationIdInput.trim();

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
      const eligibilityResponse = await getPolicyIssueEligibility(trimmed);
      setEligibility(eligibilityResponse);
      setApplicationIdInput(eligibilityResponse.applicationId);
      await loadPolicyIssue(eligibilityResponse.applicationId);
      setSuccess('증권 발행 가능 여부를 조회했습니다.');
    } catch (caught) {
      setEligibility(null);
      setPolicyIssue(null);
      setError(caught instanceof Error ? caught.message : '증권 발행 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const loadPolicyIssue = async (targetApplicationId: string) => {
    try {
      setPolicyIssue(await getPolicyIssue(targetApplicationId));
    } catch {
      setPolicyIssue(null);
    }
  };

  const handleIssuePolicy = async () => {
    if (!applicationId) return;
    setLoadingAction('issue');
    setError(null);
    setSuccess(null);
    try {
      const response = await issuePolicy(applicationId);
      setPolicyIssue(response);
      setEligibility(await getPolicyIssueEligibility(applicationId));
      setSuccess(`증권이 발행되었습니다. 증권번호: ${response.policyNumber}`);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '증권 발행에 실패했습니다.');
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
    <AppLayout activeMenuId="underwriting-policy">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>청약 심사</span>
            <span aria-hidden="true">/</span>
            <strong>증권 발행</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>증권 발행</h1>
              <p>최종 심사와 필요한 공동인수/재보험 절차가 완료된 청약에 대해 증권 발행 결과를 저장합니다.</p>
            </div>
            <span className="page-kicker">청약 심사 · 증권 발행</span>
          </div>
        </header>

        <section className="work-panel search-panel">
          <div className="panel-header compact">
            <div>
              <h2>청약번호 조회</h2>
              <p>증권 발행 가능 여부와 기존 발행 결과를 확인합니다.</p>
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
              <h2>증권 발행 처리</h2>
              <p>실제 PDF 생성이나 계약 생성 없이 증권 발행 결과만 저장합니다.</p>
            </div>
            <FileBadge2 aria-hidden="true" size={22} />
          </div>
          <p className="inline-note">계약 관리 연동, 전자문서 발송, PDF 생성은 다음 단계 예정입니다.</p>
          <div className="form-actions">
            <button className="button primary" type="button" onClick={handleIssuePolicy} disabled={!eligibility?.eligible || Boolean(policyIssue) || loadingAction === 'issue'}>
              {loadingAction === 'issue' ? '증권 발행 중...' : policyIssue ? '증권 발행 완료' : '증권 발행'}
            </button>
          </div>
        </section>

        {policyIssue && <PolicyIssueCard policyIssue={policyIssue} />}
      </div>
    </AppLayout>
  );
}

function EligibilityCard({ eligibility }: { eligibility: UnderwritingFollowUpEligibilityResponse }) {
  return (
    <section className={`work-panel detail-panel ${eligibility.eligible ? 'success-panel' : ''}`}>
      <div className="panel-header compact">
        <div>
          <h2>증권 발행 가능 여부</h2>
          <p>{eligibility.reason}</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="청약번호" value={eligibility.applicationId} mono />
        <AmountItem label="최종 심사 결과" value={eligibility.finalResult ? getResultLabel(eligibility.finalResult) : '미완료'} strong />
        <AmountItem label="공동인수 추천" value={eligibility.coinsuranceRecommended ? '예' : '아니오'} />
        <AmountItem label="재보험 필요" value={eligibility.reinsuranceRequired ? '예' : '아니오'} />
        <AmountItem label="발행 증권번호" value={eligibility.policyNumber ?? '미발행'} mono />
        <AmountItem label="상태" value={eligibility.resultStatus ? getStatusLabel(eligibility.resultStatus) : eligibility.eligible ? '발행 가능' : '발행 불가'} strong />
      </dl>
      {eligibility.nextStepMessage && <p className="inline-note">{eligibility.nextStepMessage}</p>}
    </section>
  );
}

function PolicyIssueCard({ policyIssue }: { policyIssue: PolicyIssueResponse }) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>발행된 증권 정보</h2>
          <p>{policyIssue.policyNumber}</p>
        </div>
        <FileBadge2 aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="발행번호" value={policyIssue.issueId} mono />
        <AmountItem label="청약번호" value={policyIssue.applicationId} mono />
        <AmountItem label="증권번호" value={policyIssue.policyNumber} mono strong />
        <AmountItem label="발행 상태" value={getStatusLabel(policyIssue.issueStatus)} />
        <AmountItem label="최종 심사 결과" value={getResultLabel(policyIssue.finalResult)} />
        <AmountItem label="발행일시" value={formatDateTime(policyIssue.issuedAt)} />
      </dl>
      <div className="text-detail-blocks">
        <TextBlock title="적용조건" value={policyIssue.appliedCondition || '표준 조건'} />
        <TextBlock title="계약 관리 연동 예정 안내" value={policyIssue.externalSystemMessage} />
      </div>
    </section>
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

function formatDateTime(value: string | null) {
  if (!value) return '미입력';
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value));
}
