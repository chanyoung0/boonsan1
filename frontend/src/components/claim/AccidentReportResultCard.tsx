import { CalendarDays, Check, CheckCircle2, ClipboardList, Copy, FileText } from 'lucide-react';
import { useState, type ReactNode } from 'react';
import type { AccidentReportResponse } from '../../types/claim';
import { getAccidentTypeLabel } from '../../types/claim';
import { StatusBadge } from './StatusBadge';

interface AccidentReportResultCardProps {
  data: AccidentReportResponse;
}

export function AccidentReportResultCard({ data }: AccidentReportResultCardProps) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    await navigator.clipboard.writeText(data.accidentNumber);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1600);
  };

  return (
    <aside className="work-panel result-panel">
      <div className="result-heading">
        <CheckCircle2 aria-hidden="true" size={22} />
        <div>
          <h2>접수 완료</h2>
          <p>사고 접수가 정상적으로 등록되었습니다.</p>
        </div>
      </div>

      <div className="number-box">
        <span>사고 접수번호</span>
        <strong title={data.accidentNumber}>{data.accidentNumber}</strong>
        <button className="icon-text-button" type="button" onClick={handleCopy}>
          {copied ? <Check aria-hidden="true" size={16} /> : <Copy aria-hidden="true" size={16} />}
          {copied ? '복사됨' : '복사'}
        </button>
      </div>

      <dl className="summary-list">
        <SummaryItem icon={<ClipboardList size={17} />} label="증권번호" value={data.policyNumber} />
        <SummaryItem icon={<FileText size={17} />} label="사고 유형" value={getAccidentTypeLabel(data.accidentType)} />
        <SummaryItem icon={<CalendarDays size={17} />} label="사고 일시" value={formatDateTime(data.accidentAt)} />
        {data.documentSubmissionDeadline && (
          <SummaryItem
            icon={<CalendarDays size={17} />}
            label="서류 제출 기한"
            value={formatDateTime(data.documentSubmissionDeadline)}
          />
        )}
        <div className="summary-item">
          <dt>사고 상태</dt>
          <dd>
            <StatusBadge status={data.accidentStatus} />
          </dd>
        </div>
      </dl>

      <section className="result-document-section">
        <div className="document-section-heading">
          <h3>첨부 서류</h3>
          <p>저장된 첨부 서류 파일명입니다. 실제 파일 열람은 아직 지원하지 않습니다.</p>
        </div>
        <div className="result-document-grid">
          <DocumentItem label="사고경위서" value={data.accidentReportDocumentName} />
          <DocumentItem label="진단서" value={data.medicalCertificateFileName} />
          <DocumentItem label="청구서류" value={data.claimDocumentName} />
        </div>
      </section>
    </aside>
  );
}

function SummaryItem({ icon, label, value }: { icon: ReactNode; label: string; value: string }) {
  return (
    <div className="summary-item">
      <dt>
        {icon}
        {label}
      </dt>
      <dd>{value}</dd>
    </div>
  );
}

function DocumentItem({ label, value }: { label: string | null; value: string | null }) {
  const hasFileName = Boolean(value?.trim());
  const displayValue = hasFileName ? value!.trim() : '미등록';

  return (
    <div className="document-item result-document-item">
      <FileText aria-hidden="true" size={22} />
      <div>
        <span>{label}</span>
        <strong className={`document-file-name ${!hasFileName ? 'empty-value' : ''}`} title={displayValue}>
          {displayValue}
        </strong>
        {hasFileName && <em>파일명 등록됨</em>}
      </div>
    </div>
  );
}

function formatDateTime(value: string) {
  return new Date(value).toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}
