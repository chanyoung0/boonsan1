import { FileSignature, FileText } from 'lucide-react';
import type { AuthorizationResponse } from '../../types/product';
import { getAuthorizationStatusLabel, getProductStatusLabel } from '../../types/product';

interface AuthorizationStatusCardProps {
  data: AuthorizationResponse;
}

export function AuthorizationStatusCard({ data }: AuthorizationStatusCardProps) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>인가 요청 상세</h2>
          <p>{data.requestId}</p>
        </div>
        <FileSignature aria-hidden="true" size={22} />
      </div>

      <dl className="amount-grid">
        <AmountItem label="요청 번호" value={data.requestId} mono />
        <AmountItem label="상품 코드" value={data.productCode} mono />
        <AmountItem
          label="인가 요청 상태"
          value={getAuthorizationStatusLabel(data.authorizationStatus)}
          strong={data.authorizationStatus === 'APPROVED'}
        />
        <AmountItem
          label="상품 상태"
          value={getProductStatusLabel(data.productStatus)}
          strong={data.productStatus === 'AUTHORIZED'}
        />
        <AmountItem label="요청 일시" value={formatDateTime(data.requestedAt)} />
        <AmountItem label="승인 일시" value={data.approvedAt ? formatDateTime(data.approvedAt) : '미처리'} />
        <AmountItem label="제출 기관" value={data.submissionAgencyName} />
        <AmountItem label="최종 갱신" value={formatDateTime(data.updatedAt)} />
      </dl>

      <div className="text-detail-blocks approval-opinions">
        <section className="text-box">
          <h3>요청 사유</h3>
          <p>{data.requestReason}</p>
        </section>
        {data.revisionRequest && (
          <section className="text-box">
            <h3>보완 요청 사항</h3>
            <p>{data.revisionRequest}</p>
          </section>
        )}
      </div>

      <section className="document-section">
        <div className="document-section-heading">
          <h3>
            <FileText aria-hidden="true" size={18} />
            첨부 서류
          </h3>
          <p>저장된 첨부 서류 파일명입니다. 실제 파일 열람은 아직 지원하지 않습니다.</p>
        </div>
        <div className="document-list">
          <DocumentItem label="상품설명서" value={data.productDescriptionFileName} />
          <DocumentItem label="약관" value={data.termsAndConditionsFileName} />
          <DocumentItem label="요율서" value={data.rateScheduleFileName} />
          <DocumentItem label="상품개발 근거자료" value={data.productEvidenceFileName} />
        </div>
      </section>
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
  value: string;
  strong?: boolean;
  mono?: boolean;
}) {
  return (
    <div className={`amount-item ${strong ? 'strong' : ''}`}>
      <dt>{label}</dt>
      <dd className={mono ? 'mono' : ''} title={value}>
        {value}
      </dd>
    </div>
  );
}

function DocumentItem({ label, value }: { label: string; value: string | null }) {
  const hasFileName = Boolean(value?.trim());
  const displayValue = hasFileName ? value!.trim() : '미등록';
  return (
    <div className="document-item">
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
  return new Intl.DateTimeFormat('ko-KR', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value));
}
