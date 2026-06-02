import { KeyboardEvent, useState } from 'react';
import { ClipboardList, FileCheck2, History, Search, ShieldCheck } from 'lucide-react';
import {
  createCreditInformationInquiry,
  getCreditInformationInquiries,
  getCreditInformationInquiry,
  getUnderwritingApplication
} from '../../api/underwritingApi';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  CreditInformationInquiryCreateRequest,
  CreditInformationInquiryResponse,
  UnderwritingApplicationResponse
} from '../../types/underwriting';
import {
  APPLICATION_STATUS_LABELS,
  CREDIT_RISK_FLAG_LABELS,
  CREDIT_RISK_GRADE_LABELS
} from '../../types/underwriting';

type LoadingAction = 'lookup' | 'create' | 'history' | 'detail' | null;

const initialInquiryForm = {
  customerName: '',
  customerIdentifier: '',
  accidentHistoryExists: false,
  otherInsuranceContractExists: false,
  previousClaimExists: false
};

export function UnderwritingCreditPage() {
  const [applicationIdInput, setApplicationIdInput] = useState('');
  const [application, setApplication] = useState<UnderwritingApplicationResponse | null>(null);
  const [inquiryForm, setInquiryForm] = useState(initialInquiryForm);
  const [latestInquiry, setLatestInquiry] = useState<CreditInformationInquiryResponse | null>(null);
  const [selectedInquiry, setSelectedInquiry] = useState<CreditInformationInquiryResponse | null>(null);
  const [history, setHistory] = useState<CreditInformationInquiryResponse[]>([]);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleLookup = async () => {
    const applicationId = applicationIdInput.trim();
    if (!applicationId) {
      setError('청약번호를 입력해주세요.');
      return;
    }

    setLoadingAction('lookup');
    setError(null);
    setSuccess(null);
    setLatestInquiry(null);
    setSelectedInquiry(null);
    try {
      const applicationResponse = await getUnderwritingApplication(applicationId);
      setApplication(applicationResponse);
      setApplicationIdInput(applicationResponse.applicationId);
      setInquiryForm((current) => ({
        ...current,
        customerName: current.customerName.trim() || applicationResponse.insuredPersonName
      }));
      const historyResponse = await getCreditInformationInquiries(applicationResponse.applicationId);
      setHistory(historyResponse);
      setSuccess('청약 정보를 확인했습니다.');
    } catch (caught) {
      setApplication(null);
      setHistory([]);
      setError(caught instanceof Error ? caught.message : '청약 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleCreateInquiry = async () => {
    if (!application) {
      setError('먼저 청약번호를 조회해주세요.');
      return;
    }

    setLoadingAction('create');
    setError(null);
    setSuccess(null);
    try {
      const request: CreditInformationInquiryCreateRequest = {
        customerName: toNullable(inquiryForm.customerName) ?? application.insuredPersonName,
        customerIdentifier: toNullable(inquiryForm.customerIdentifier),
        accidentHistoryExists: inquiryForm.accidentHistoryExists,
        otherInsuranceContractExists: inquiryForm.otherInsuranceContractExists,
        previousClaimExists: inquiryForm.previousClaimExists
      };
      const response = await createCreditInformationInquiry(application.applicationId, request);
      setLatestInquiry(response);
      setSelectedInquiry(response);
      const historyResponse = await getCreditInformationInquiries(application.applicationId);
      setHistory(historyResponse);
      setSuccess('Mock 신용정보 조회 결과를 저장했습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '신용정보 조회에 실패했습니다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleLoadDetail = async (inquiryId: string) => {
    setLoadingAction('detail');
    setError(null);
    try {
      const response = await getCreditInformationInquiry(inquiryId);
      setSelectedInquiry(response);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '신용정보 상세 조회에 실패했습니다.');
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
    <AppLayout activeMenuId="underwriting-credit">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>청약 심사</span>
            <span aria-hidden="true">/</span>
            <strong>신용정보 조회</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>신용정보 조회</h1>
              <p>청약번호 기준으로 Mock 신용정보를 조회하고 사고이력, 타사계약, 지급이력 위험 플래그를 관리합니다.</p>
            </div>
            <span className="page-kicker">청약 심사 · Mock 조회</span>
          </div>
        </header>

        <section className="work-panel search-panel">
          <div className="panel-header compact">
            <div>
              <h2>청약번호 조회</h2>
              <p>등록된 청약번호를 확인한 뒤 신용정보 조회 이력을 불러옵니다.</p>
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

        {application && <ApplicationSummaryCard application={application} />}

        <section className="work-panel underwriting-form-panel">
          <div className="panel-header compact">
            <div>
              <h2>Mock 신용정보 조회 입력</h2>
              <p>실제 외부 API를 호출하지 않고, 입력한 플래그를 기준으로 신용 위험등급을 산정합니다.</p>
            </div>
            <ClipboardList aria-hidden="true" size={22} />
          </div>

          <div className="field-grid two">
            <TextField
              label="고객명"
              value={inquiryForm.customerName}
              onChange={(value) => setInquiryForm({ ...inquiryForm, customerName: value })}
              placeholder={application?.insuredPersonName ?? '청약 조회 시 자동 입력'}
            />
            <TextField
              label="고객 식별자"
              value={inquiryForm.customerIdentifier}
              onChange={(value) => setInquiryForm({ ...inquiryForm, customerIdentifier: value })}
              placeholder="900101-1234567"
            />
          </div>

          <div className="credit-flag-grid">
            <CheckboxField
              label="사고이력 있음"
              checked={inquiryForm.accidentHistoryExists}
              onChange={(value) => setInquiryForm({ ...inquiryForm, accidentHistoryExists: value })}
            />
            <CheckboxField
              label="타사계약 있음"
              checked={inquiryForm.otherInsuranceContractExists}
              onChange={(value) => setInquiryForm({ ...inquiryForm, otherInsuranceContractExists: value })}
            />
            <CheckboxField
              label="이전 보험금 지급이력 있음"
              checked={inquiryForm.previousClaimExists}
              onChange={(value) => setInquiryForm({ ...inquiryForm, previousClaimExists: value })}
            />
          </div>

          <p className="inline-note">
            주민등록번호 등 전체 개인정보는 저장하지 않습니다. 입력한 식별자는 백엔드에서 마스킹된 값만 저장합니다.
          </p>

          <div className="form-actions">
            <button className="button primary" type="button" onClick={handleCreateInquiry} disabled={!application || loadingAction === 'create'}>
              {loadingAction === 'create' ? '조회 저장 중...' : 'Mock 신용정보 조회'}
            </button>
          </div>
        </section>

        {latestInquiry && <CreditInquiryResultCard inquiry={latestInquiry} title="최근 조회 결과" />}

        <CreditInquiryHistoryPanel
          history={history}
          loading={loadingAction === 'detail'}
          selectedInquiryId={selectedInquiry?.inquiryId ?? null}
          onLoadDetail={handleLoadDetail}
        />

        {selectedInquiry && selectedInquiry.inquiryId !== latestInquiry?.inquiryId && (
          <CreditInquiryResultCard inquiry={selectedInquiry} title="조회 이력 상세" />
        )}
      </div>
    </AppLayout>
  );
}

function ApplicationSummaryCard({ application }: { application: UnderwritingApplicationResponse }) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>청약 확인 정보</h2>
          <p>{application.applicationId}</p>
        </div>
        <ShieldCheck aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="청약번호" value={application.applicationId} mono />
        <AmountItem label="청약 상태" value={APPLICATION_STATUS_LABELS[application.applicationStatus] ?? application.applicationStatus} strong />
        <AmountItem label="상품코드" value={application.productCode} />
        <AmountItem label="피보험자" value={application.insuredPersonName} />
        <AmountItem label="사고이력 입력값" value={application.hasAccidentHistory ? '있음' : '없음'} />
        <AmountItem label="타사계약 입력값" value={application.hasOtherContract ? '있음' : '없음'} />
      </dl>
    </section>
  );
}

function CreditInquiryResultCard({
  inquiry,
  title
}: {
  inquiry: CreditInformationInquiryResponse;
  title: string;
}) {
  const flagLabels = splitRiskFlags(inquiry.riskFlags).map((flag) => CREDIT_RISK_FLAG_LABELS[flag] ?? flag);
  const isHighRisk = inquiry.creditRiskGrade === 'HIGH';

  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>{title}</h2>
          <p>{inquiry.inquiryId}</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="조회번호" value={inquiry.inquiryId} mono />
        <AmountItem label="청약번호" value={inquiry.applicationId} mono />
        <AmountItem label="고객명" value={inquiry.customerName} />
        <AmountItem label="마스킹 식별자" value={inquiry.customerIdentifierMasked ?? '미입력'} />
        <AmountItem label="사고이력" value={inquiry.accidentHistoryExists ? '있음' : '없음'} strong={inquiry.accidentHistoryExists} />
        <AmountItem label="타사계약" value={inquiry.otherInsuranceContractExists ? '있음' : '없음'} strong={inquiry.otherInsuranceContractExists} />
        <AmountItem label="이전 지급이력" value={inquiry.previousClaimExists ? '있음' : '없음'} strong={inquiry.previousClaimExists} />
        <AmountItem label="위험등급" value={CREDIT_RISK_GRADE_LABELS[inquiry.creditRiskGrade]} strong={inquiry.creditRiskGrade !== 'LOW'} />
      </dl>

      <div className="credit-flag-list">
        {flagLabels.map((flagLabel) => (
          <span className={`risk-grade-badge ${inquiry.creditRiskGrade.toLowerCase()}`} key={flagLabel}>
            {flagLabel}
          </span>
        ))}
      </div>

      <div className="text-detail-blocks">
        <TextBlock title="외부 연동 예정 안내" value={inquiry.externalSystemMessage} />
        {isHighRisk && <TextBlock title="심사 연결 안내" value="위험등급이 높음이므로 향후 청약 심사 점수 계산에서 감점될 수 있습니다." />}
      </div>
    </section>
  );
}

function CreditInquiryHistoryPanel({
  history,
  loading,
  selectedInquiryId,
  onLoadDetail
}: {
  history: CreditInformationInquiryResponse[];
  loading: boolean;
  selectedInquiryId: string | null;
  onLoadDetail: (inquiryId: string) => void;
}) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header compact">
        <div>
          <h2>신용정보 조회 이력</h2>
          <p>동일 청약번호의 Mock 신용정보 조회 결과를 이력으로 확인합니다.</p>
        </div>
        <History aria-hidden="true" size={22} />
      </div>
      {history.length > 0 ? (
        <div className="history-list">
          {history.map((item) => (
            <div className="history-item credit-history-item" key={item.inquiryId}>
              <strong>{item.inquiryId}</strong>
              <span>{formatDateTime(item.createdAt)}</span>
              <p>
                위험등급 {CREDIT_RISK_GRADE_LABELS[item.creditRiskGrade]} · {splitRiskFlags(item.riskFlags).map((flag) => CREDIT_RISK_FLAG_LABELS[flag] ?? flag).join(', ')}
              </p>
              <button
                className="button secondary compact-button"
                type="button"
                onClick={() => onLoadDetail(item.inquiryId)}
                disabled={loading || selectedInquiryId === item.inquiryId}
              >
                {selectedInquiryId === item.inquiryId ? '선택됨' : '상세 조회'}
              </button>
            </div>
          ))}
        </div>
      ) : (
        <p className="empty-value">청약번호 조회 후 Mock 신용정보 조회를 실행하면 이력이 표시됩니다.</p>
      )}
    </section>
  );
}

function TextField({
  label,
  value,
  onChange,
  placeholder
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}) {
  return (
    <label className="field">
      <span>{label}</span>
      <input value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder} />
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
    <label className="credit-check-field">
      <input type="checkbox" checked={checked} onChange={(event) => onChange(event.target.checked)} />
      <span>{label}</span>
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

function splitRiskFlags(value: string) {
  return value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
}

function toNullable(value: string) {
  const trimmed = value.trim();
  return trimmed ? trimmed : null;
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
