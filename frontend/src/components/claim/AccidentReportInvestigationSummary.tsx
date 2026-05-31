import { AlertTriangle, CalendarDays, FileText, Paperclip } from 'lucide-react';
import type { ReactNode } from 'react';
import type { DamageInvestigationStartResponse } from '../../types/claim';
import { getAccidentTypeLabel } from '../../types/claim';
import { StatusBadge } from './StatusBadge';

interface AccidentReportInvestigationSummaryProps {
  data: DamageInvestigationStartResponse;
}

export function AccidentReportInvestigationSummary({ data }: AccidentReportInvestigationSummaryProps) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header detail-title">
        <div>
          <h2>사고 접수 내용</h2>
          <p>{data.accidentNumber}</p>
        </div>
        <StatusBadge status={data.accidentStatus} />
      </div>

      <div className="detail-grid investigation-summary-grid">
        <SummaryItem icon={<FileText size={17} />} label="사고 접수번호" value={data.accidentNumber} mono />
        <SummaryItem icon={<FileText size={17} />} label="증권번호" value={data.policyNumber} />
        <SummaryItem icon={<CalendarDays size={17} />} label="사고 일시" value={formatDateTime(data.accidentAt)} />
        <SummaryItem icon={<AlertTriangle size={17} />} label="사고 유형" value={getAccidentTypeLabel(data.accidentType)} />
      </div>

      <div className="text-detail-blocks">
        <TextBlock title="사고 경위" value={data.accidentDescription} />
        <TextBlock title="피해 내용" value={data.damageDetails} />
      </div>

      <section className="document-section">
        <div className="document-section-heading">
          <h3>
            <Paperclip aria-hidden="true" size={18} />
            접수 첨부 서류
          </h3>
          <p>저장된 첨부 서류 파일명입니다. 실제 파일 열람은 아직 지원하지 않습니다.</p>
        </div>
        <div className="document-list">
          <DocumentName label="사고경위서" value={data.accidentReportDocumentName} />
          <DocumentName label="진단서" value={data.medicalCertificateFileName} />
          <DocumentName label="청구서류" value={data.claimDocumentName} />
        </div>
      </section>
    </section>
  );
}

function SummaryItem({
  icon,
  label,
  value,
  mono = false
}: {
  icon: ReactNode;
  label: string;
  value: string;
  mono?: boolean;
}) {
  return (
    <div className="detail-item">
      <span className="detail-icon">{icon}</span>
      <div>
        <span>{label}</span>
        <strong className={mono ? 'mono' : ''} title={value}>
          {value}
        </strong>
      </div>
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

function DocumentName({ label, value }: { label: string; value: string | null }) {
  const displayValue = value?.trim() || '미등록';
  const hasValue = displayValue !== '미등록';

  return (
    <div className="document-item">
      <FileText aria-hidden="true" size={22} />
      <div>
        <span>{label}</span>
        <strong className={`document-file-name ${!hasValue ? 'empty-value' : ''}`} title={displayValue}>
          {displayValue}
        </strong>
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
