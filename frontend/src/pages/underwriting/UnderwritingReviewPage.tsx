import { KeyboardEvent, useState } from 'react';
import { ClipboardList, FileCheck2, History, Search, ShieldCheck } from 'lucide-react';
import {
  calculateUnderwritingAutoScore,
  createUnderwritingApplication,
  finalizeUnderwritingReview,
  getUnderwritingApplication,
  getUnderwritingHistory
} from '../../api/underwritingApi';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  UnderwritingApplicationResponse,
  UnderwritingAutoScoreResponse,
  UnderwritingFinalizeRequest,
  UnderwritingHistoryResponse,
  UnderwritingReviewResponse,
  UnderwritingResultType
} from '../../types/underwriting';
import {
  APPLICATION_STATUS_LABELS,
  UNDERWRITING_RESULT_LABELS,
  UNDERWRITING_STATUS_LABELS
} from '../../types/underwriting';

type LoadingAction = 'lookup' | 'create' | 'auto-score' | 'finalize' | 'history' | null;

const initialApplicationForm = {
  productCode: 'AUTO-2026-001',
  insuredAmount: '100000000',
  premium: '150000',
  paymentCycle: '월납',
  termsVersion: '2026-표준약관 v1.0',
  specialContractList: '상해특약',
  appliedCondition: '',
  insuredPersonName: '홍길동',
  age: '35',
  gender: '남',
  occupation: '회사원',
  annualIncome: '50000000',
  pastMedicalHistory: '',
  medicated: false,
  surgeryHistory: '',
  familyHistory: '',
  smoker: false,
  alcoholConsumption: '주 1회',
  bmi: '22.4',
  vehicleModel: '현대 소나타',
  vehicleNumber: '12가3456',
  hasAccidentHistory: false,
  hasOtherContract: false
};

const initialFinalizeForm = {
  finalResult: 'APPROVED' as UnderwritingResultType,
  underwriterId: 'UW-EMP-001',
  underwriterName: '언더라이터',
  department: '청약심사팀',
  underwritingOpinion: '자동심사 보고서 기준으로 최종 판단합니다.',
  surchargeCondition: '보험료 15% 할증',
  rejectionReason: '위험도 기준 초과'
};

export function UnderwritingReviewPage() {
  const [applicationForm, setApplicationForm] = useState(initialApplicationForm);
  const [finalizeForm, setFinalizeForm] = useState(initialFinalizeForm);
  const [lookupApplicationId, setLookupApplicationId] = useState('');
  const [application, setApplication] = useState<UnderwritingApplicationResponse | null>(null);
  const [autoScore, setAutoScore] = useState<UnderwritingAutoScoreResponse | null>(null);
  const [finalReview, setFinalReview] = useState<UnderwritingReviewResponse | null>(null);
  const [history, setHistory] = useState<UnderwritingHistoryResponse[]>([]);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleLookup = async () => {
    const applicationId = lookupApplicationId.trim();
    if (!applicationId) {
      setError('청약번호를 입력해주세요.');
      return;
    }

    setLoadingAction('lookup');
    setError(null);
    setSuccess(null);
    setAutoScore(null);
    setFinalReview(null);
    try {
      const response = await getUnderwritingApplication(applicationId);
      setApplication(response);
      setLookupApplicationId(response.applicationId);
      await loadHistory(response.applicationId, false);
      setSuccess('청약 정보를 조회했습니다.');
    } catch (caught) {
      setApplication(null);
      setHistory([]);
      setError(caught instanceof Error ? caught.message : '청약 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleCreateApplication = async () => {
    setLoadingAction('create');
    setError(null);
    setSuccess(null);
    setAutoScore(null);
    setFinalReview(null);
    setHistory([]);

    try {
      const response = await createUnderwritingApplication({
        productCode: applicationForm.productCode,
        insuredAmount: toNumber(applicationForm.insuredAmount),
        premium: toNumber(applicationForm.premium),
        paymentCycle: applicationForm.paymentCycle,
        termsVersion: applicationForm.termsVersion,
        specialContractList: toNullable(applicationForm.specialContractList),
        appliedCondition: toNullable(applicationForm.appliedCondition),
        insuredPersonName: applicationForm.insuredPersonName,
        age: Math.trunc(toNumber(applicationForm.age)),
        gender: applicationForm.gender,
        occupation: applicationForm.occupation,
        annualIncome: toNumber(applicationForm.annualIncome),
        pastMedicalHistory: toNullable(applicationForm.pastMedicalHistory),
        medicated: applicationForm.medicated,
        surgeryHistory: toNullable(applicationForm.surgeryHistory),
        familyHistory: toNullable(applicationForm.familyHistory),
        smoker: applicationForm.smoker,
        alcoholConsumption: toNullable(applicationForm.alcoholConsumption),
        bmi: toNumber(applicationForm.bmi),
        vehicleModel: toNullable(applicationForm.vehicleModel),
        vehicleNumber: toNullable(applicationForm.vehicleNumber),
        hasAccidentHistory: applicationForm.hasAccidentHistory,
        hasOtherContract: applicationForm.hasOtherContract
      });
      setApplication(response);
      setLookupApplicationId(response.applicationId);
      await loadHistory(response.applicationId, false);
      setSuccess(`청약이 등록되었습니다. 청약번호: ${response.applicationId}`);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '청약 등록에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleAutoScore = async () => {
    if (!application) return;

    setLoadingAction('auto-score');
    setError(null);
    setSuccess(null);
    setFinalReview(null);
    try {
      const response = await calculateUnderwritingAutoScore(application.applicationId);
      setAutoScore(response);
      await loadHistory(application.applicationId, false);
      setSuccess('자동심사 보고서가 생성되었습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '자동심사 실행에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleFinalize = async () => {
    if (!application) return;

    setLoadingAction('finalize');
    setError(null);
    setSuccess(null);
    try {
      const request: UnderwritingFinalizeRequest = {
        finalResult: finalizeForm.finalResult,
        underwriterId: finalizeForm.underwriterId,
        underwriterName: finalizeForm.underwriterName,
        department: finalizeForm.department,
        underwritingOpinion: toNullable(finalizeForm.underwritingOpinion),
        surchargeCondition: finalizeForm.finalResult === 'SURCHARGE' ? toNullable(finalizeForm.surchargeCondition) : null,
        rejectionReason: finalizeForm.finalResult === 'REJECTED' ? toNullable(finalizeForm.rejectionReason) : null
      };
      const response = await finalizeUnderwritingReview(application.applicationId, request);
      setFinalReview(response);
      const updatedApplication = await getUnderwritingApplication(application.applicationId);
      setApplication(updatedApplication);
      await loadHistory(application.applicationId, false);
      setSuccess('최종 심사 결과가 저장되었습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '최종 심사 저장에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const loadHistory = async (applicationId: string, showLoading = true) => {
    if (showLoading) {
      setLoadingAction('history');
      setError(null);
    }
    try {
      const response = await getUnderwritingHistory(applicationId);
      setHistory(response);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '심사 이력 조회에 실패했습니다.');
    } finally {
      if (showLoading) {
        setLoadingAction(null);
      }
    }
  };

  const handleLookupKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleLookup();
    }
  };

  const finalAlreadySaved = Boolean(finalReview?.finalResult) || application?.applicationStatus === 'APPROVED' || application?.applicationStatus === 'REJECTED';

  return (
    <AppLayout activeMenuId="underwriting-review">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>청약 심사</span>
            <span aria-hidden="true">/</span>
            <strong>보험청약 심사</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>보험청약 심사</h1>
              <p>청약 정보를 등록하고 자동심사 점수, 수동심사 판단, 최종 심사 결과를 관리합니다.</p>
            </div>
            <span className="page-kicker">청약 심사 · 1단계</span>
          </div>
        </header>

        <section className="work-panel search-panel">
          <div className="panel-header compact">
            <div>
              <h2>청약번호 조회</h2>
              <p>등록된 청약번호로 청약 정보와 심사 이력을 조회합니다.</p>
            </div>
            <Search aria-hidden="true" size={22} />
          </div>
          <div className="search-row">
            <input
              aria-label="청약번호"
              value={lookupApplicationId}
              onChange={(event) => setLookupApplicationId(event.target.value)}
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

        <ApplicationForm
          form={applicationForm}
          loading={loadingAction === 'create'}
          onChange={setApplicationForm}
          onSubmit={handleCreateApplication}
        />

        {application && <ApplicationResultCard application={application} />}

        <AutoScorePanel
          application={application}
          autoScore={autoScore}
          loading={loadingAction === 'auto-score'}
          onRun={handleAutoScore}
        />

        {autoScore && <AutoScoreReportCard autoScore={autoScore} />}

        <FinalizePanel
          form={finalizeForm}
          application={application}
          autoScore={autoScore}
          finalAlreadySaved={finalAlreadySaved}
          loading={loadingAction === 'finalize'}
          onChange={setFinalizeForm}
          onSubmit={handleFinalize}
        />

        {finalReview && <FinalReviewCard review={finalReview} />}

        <HistoryPanel
          application={application}
          history={history}
          loading={loadingAction === 'history'}
          onReload={() => application && loadHistory(application.applicationId)}
        />
      </div>
    </AppLayout>
  );
}

function ApplicationForm({
  form,
  loading,
  onChange,
  onSubmit
}: {
  form: typeof initialApplicationForm;
  loading: boolean;
  onChange: (form: typeof initialApplicationForm) => void;
  onSubmit: () => void;
}) {
  return (
    <section className="work-panel">
      <div className="panel-header compact">
        <div>
          <h2>청약 정보 입력</h2>
          <p>보험청약 기본 정보와 Mock 신용정보 판단값을 입력합니다.</p>
        </div>
        <ClipboardList aria-hidden="true" size={22} />
      </div>

      <div className="field-grid three">
        <TextField label="상품코드" value={form.productCode} onChange={(value) => onChange({ ...form, productCode: value })} />
        <TextField label="보험가입금액" type="number" value={form.insuredAmount} onChange={(value) => onChange({ ...form, insuredAmount: value })} />
        <TextField label="보험료" type="number" value={form.premium} onChange={(value) => onChange({ ...form, premium: value })} />
        <TextField label="납입주기" value={form.paymentCycle} onChange={(value) => onChange({ ...form, paymentCycle: value })} />
        <TextField label="약관 버전" value={form.termsVersion} onChange={(value) => onChange({ ...form, termsVersion: value })} />
        <TextField label="특약 목록" value={form.specialContractList} onChange={(value) => onChange({ ...form, specialContractList: value })} />
      </div>

      <div className="section-title">
        <h3>피보험자 정보</h3>
      </div>
      <div className="field-grid three">
        <TextField label="이름" value={form.insuredPersonName} onChange={(value) => onChange({ ...form, insuredPersonName: value })} />
        <TextField label="나이" type="number" value={form.age} onChange={(value) => onChange({ ...form, age: value })} />
        <TextField label="성별" value={form.gender} onChange={(value) => onChange({ ...form, gender: value })} />
        <TextField label="직업" value={form.occupation} onChange={(value) => onChange({ ...form, occupation: value })} />
        <TextField label="연소득" type="number" value={form.annualIncome} onChange={(value) => onChange({ ...form, annualIncome: value })} />
        <TextField label="BMI" type="number" value={form.bmi} onChange={(value) => onChange({ ...form, bmi: value })} />
        <TextField label="과거질병이력" value={form.pastMedicalHistory} onChange={(value) => onChange({ ...form, pastMedicalHistory: value })} />
        <TextField label="수술이력" value={form.surgeryHistory} onChange={(value) => onChange({ ...form, surgeryHistory: value })} />
        <TextField label="가족력" value={form.familyHistory} onChange={(value) => onChange({ ...form, familyHistory: value })} />
        <TextField label="음주량" value={form.alcoholConsumption} onChange={(value) => onChange({ ...form, alcoholConsumption: value })} />
        <TextField label="차량기종" value={form.vehicleModel} onChange={(value) => onChange({ ...form, vehicleModel: value })} />
        <TextField label="차량번호" value={form.vehicleNumber} onChange={(value) => onChange({ ...form, vehicleNumber: value })} />
      </div>

      <div className="field-grid three">
        <CheckboxField label="투약여부" checked={form.medicated} onChange={(value) => onChange({ ...form, medicated: value })} />
        <CheckboxField label="흡연여부" checked={form.smoker} onChange={(value) => onChange({ ...form, smoker: value })} />
        <CheckboxField label="사고이력 있음" checked={form.hasAccidentHistory} onChange={(value) => onChange({ ...form, hasAccidentHistory: value })} />
        <CheckboxField label="타사계약 있음" checked={form.hasOtherContract} onChange={(value) => onChange({ ...form, hasOtherContract: value })} />
      </div>

      <p className="inline-note">외부 신용정보 API는 실제 연동하지 않고, 현재 단계에서는 입력값 기반 Mock 판단으로 처리합니다.</p>

      <div className="form-actions">
        <button className="button primary" type="button" onClick={onSubmit} disabled={loading}>
          {loading ? '등록 중...' : '청약 등록'}
        </button>
      </div>
    </section>
  );
}

function ApplicationResultCard({ application }: { application: UnderwritingApplicationResponse }) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>청약 등록 결과</h2>
          <p>{application.applicationId}</p>
        </div>
        <ShieldCheck aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="청약번호" value={application.applicationId} mono />
        <AmountItem label="청약 상태" value={getApplicationStatusLabel(application.applicationStatus)} strong />
        <AmountItem label="상품코드" value={application.productCode} />
        <AmountItem label="피보험자" value={application.insuredPersonInfo} />
        <AmountItem label="보험가입금액" value={application.insuredAmount} strong />
        <AmountItem label="보험료" value={application.premium} />
      </dl>
      {application.nextStepMessage && <p className="empty-value">{application.nextStepMessage}</p>}
    </section>
  );
}

function AutoScorePanel({
  application,
  autoScore,
  loading,
  onRun
}: {
  application: UnderwritingApplicationResponse | null;
  autoScore: UnderwritingAutoScoreResponse | null;
  loading: boolean;
  onRun: () => void;
}) {
  return (
    <section className="work-panel">
      <div className="panel-header compact">
        <div>
          <h2>자동심사 실행</h2>
          <p>기존 콘솔 감점 기준을 기반으로 자동심사 점수와 보고서를 생성합니다.</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>
      <button className="button primary" type="button" onClick={onRun} disabled={!application || loading || Boolean(autoScore)}>
        {loading ? '자동심사 중...' : autoScore ? '자동심사 완료' : '자동심사 점수 계산'}
      </button>
      {!application && <p className="empty-value">청약 등록 또는 청약번호 조회 후 자동심사를 실행할 수 있습니다.</p>}
    </section>
  );
}

function AutoScoreReportCard({ autoScore }: { autoScore: UnderwritingAutoScoreResponse }) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>자동심사 보고서</h2>
          <p>{autoScore.reviewId}</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="총점" value={`${Math.round(autoScore.totalScore)}점`} strong />
        <AmountItem label="총 감점" value={`${autoScore.totalDeduction}점`} />
        <AmountItem label="추천 결과" value={getResultLabel(autoScore.recommendedResult)} strong />
        <AmountItem label="자동심사 가능 여부" value={autoScore.autoReviewAvailable ? '가능' : '불가'} />
        <AmountItem label="수동심사 필요" value={autoScore.manualReviewRequired ? '필요' : '불필요'} />
        <AmountItem label="공동인수 추천" value={autoScore.coinsuranceRecommended ? '예' : '아니오'} strong={autoScore.coinsuranceRecommended} />
      </dl>
      <div className="text-detail-blocks">
        <TextBlock title="보고서 요약" value={autoScore.reportSummary} />
        <TextBlock title="공동인수 안내" value={autoScore.coinsuranceMessage} />
        <TextBlock title="재보험 안내" value={autoScore.reinsuranceMessage} />
        <TextBlock title="증권 발행 안내" value={autoScore.policyIssueMessage} />
      </div>
      <div className="history-list">
        {autoScore.deductionItems.map((item) => (
          <div className="history-item" key={item.itemName}>
            <strong>{item.itemName}</strong>
            <span>{item.itemValue}</span>
            <span>{item.deduction}점</span>
            <p>{item.reason}</p>
          </div>
        ))}
      </div>
    </section>
  );
}

function FinalizePanel({
  form,
  application,
  autoScore,
  finalAlreadySaved,
  loading,
  onChange,
  onSubmit
}: {
  form: typeof initialFinalizeForm;
  application: UnderwritingApplicationResponse | null;
  autoScore: UnderwritingAutoScoreResponse | null;
  finalAlreadySaved: boolean;
  loading: boolean;
  onChange: (form: typeof initialFinalizeForm) => void;
  onSubmit: () => void;
}) {
  return (
    <section className="work-panel">
      <div className="panel-header compact">
        <div>
          <h2>수동심사/최종심사 입력</h2>
          <p>자동심사 보고서를 바탕으로 언더라이터 최종 판단을 저장합니다.</p>
        </div>
      </div>
      <div className="field-grid three">
        <label className="field">
          <span>최종 심사 결과</span>
          <select value={form.finalResult} onChange={(event) => onChange({ ...form, finalResult: event.target.value as UnderwritingResultType })}>
            <option value="APPROVED">승인</option>
            <option value="SURCHARGE">할증</option>
            <option value="REJECTED">거절</option>
          </select>
        </label>
        <TextField label="사원번호" value={form.underwriterId} onChange={(value) => onChange({ ...form, underwriterId: value })} />
        <TextField label="심사자 이름" value={form.underwriterName} onChange={(value) => onChange({ ...form, underwriterName: value })} />
        <TextField label="부서" value={form.department} onChange={(value) => onChange({ ...form, department: value })} />
        <TextField label="할증조건" value={form.surchargeCondition} onChange={(value) => onChange({ ...form, surchargeCondition: value })} />
        <TextField label="거절사유" value={form.rejectionReason} onChange={(value) => onChange({ ...form, rejectionReason: value })} />
      </div>
      <label className="field">
        <span>심사 의견</span>
        <textarea value={form.underwritingOpinion} onChange={(event) => onChange({ ...form, underwritingOpinion: event.target.value })} />
      </label>
      <div className="form-actions">
        <button className="button primary" type="button" onClick={onSubmit} disabled={!application || !autoScore || finalAlreadySaved || loading}>
          {loading ? '저장 중...' : finalAlreadySaved ? '최종심사 저장 완료' : '최종 심사 결과 저장'}
        </button>
      </div>
      {!autoScore && <p className="empty-value">자동심사 보고서 생성 후 최종심사를 저장할 수 있습니다.</p>}
      {finalAlreadySaved && <p className="empty-value">이미 최종 심사가 완료된 청약은 중복 저장할 수 없습니다.</p>}
    </section>
  );
}

function FinalReviewCard({ review }: { review: UnderwritingReviewResponse }) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>최종 심사 결과</h2>
          <p>{review.reviewId}</p>
        </div>
        <ShieldCheck aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="청약번호" value={review.applicationId} mono />
        <AmountItem label="심사 상태" value={getUnderwritingStatusLabel(review.underwritingStatus)} strong />
        <AmountItem label="자동심사 추천" value={getResultLabel(review.recommendedResult)} />
        <AmountItem label="최종 결과" value={review.finalResult ? getResultLabel(review.finalResult) : '미저장'} strong />
        <AmountItem label="총점" value={`${Math.round(review.totalScore)}점`} />
        <AmountItem label="공동인수 추천" value={review.coinsuranceRecommended ? '예' : '아니오'} />
        <AmountItem label="심사자" value={review.underwriterName || '미입력'} />
        <AmountItem label="부서" value={review.department || '미입력'} />
      </dl>
      <div className="text-detail-blocks">
        <TextBlock title="심사 의견" value={review.underwritingOpinion || '미입력'} />
        <TextBlock title="다음 단계 안내" value={review.nextStepMessage || '공동인수/재보험/증권발행 실제 처리는 다음 단계 예정입니다.'} />
      </div>
    </section>
  );
}

function HistoryPanel({
  application,
  history,
  loading,
  onReload
}: {
  application: UnderwritingApplicationResponse | null;
  history: UnderwritingHistoryResponse[];
  loading: boolean;
  onReload: () => void;
}) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>심사 이력 조회</h2>
          <p>청약 접수, 자동심사, 최종심사 저장 이력을 확인합니다.</p>
        </div>
        <History aria-hidden="true" size={22} />
      </div>
      <button className="button secondary" type="button" onClick={onReload} disabled={!application || loading}>
        {loading ? '조회 중...' : '이력 새로고침'}
      </button>
      {history.length > 0 ? (
        <div className="history-list">
          {history.map((item) => (
            <div className="history-item" key={item.historyId}>
              <strong>{item.eventType}</strong>
              <span>{formatDateTime(item.createdAt)}</span>
              <p>{item.eventMessage}</p>
              <p>
                {item.score !== null ? `${Math.round(item.score)}점` : '점수 없음'}
                {item.result ? ` · ${getResultLabel(item.result)}` : ''}
              </p>
            </div>
          ))}
        </div>
      ) : (
        <p className="empty-value">청약을 등록하거나 조회하면 심사 이력이 표시됩니다.</p>
      )}
    </section>
  );
}

function TextField({
  label,
  value,
  onChange,
  type = 'text'
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
}) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function CheckboxField({
  label,
  checked,
  onChange
}: {
  label: string;
  checked: boolean;
  onChange: (value: boolean) => void;
}) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type="checkbox" checked={checked} onChange={(event) => onChange(event.target.checked)} />
    </label>
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

function toNumber(value: string) {
  const numberValue = Number(value);
  return Number.isFinite(numberValue) ? numberValue : 0;
}

function toNullable(value: string) {
  const trimmed = value.trim();
  return trimmed ? trimmed : null;
}

function getResultLabel(result: string) {
  return UNDERWRITING_RESULT_LABELS[result] ?? result;
}

function getApplicationStatusLabel(status: string) {
  return APPLICATION_STATUS_LABELS[status] ?? status;
}

function getUnderwritingStatusLabel(status: string) {
  return UNDERWRITING_STATUS_LABELS[status] ?? status;
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
