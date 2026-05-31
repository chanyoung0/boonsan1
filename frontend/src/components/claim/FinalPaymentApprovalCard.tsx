import { FileCheck2 } from 'lucide-react';
import type { PaymentApprovalDocumentResponse } from '../../types/claim';
import { getAccidentStatusLabel } from '../../types/claim';

interface FinalPaymentApprovalCardProps {
  document: PaymentApprovalDocumentResponse;
}

export function FinalPaymentApprovalCard({ document }: FinalPaymentApprovalCardProps) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>최종 지급품의서</h2>
          <p>{document.documentId}</p>
        </div>
        <FileCheck2 aria-hidden="true" size={22} />
      </div>

      <dl className="amount-grid">
        <AmountItem label="사고 접수번호" value={document.accidentNumber} mono />
        <AmountItem label="손해조사번호" value={document.investigationId} mono />
        <AmountItem label="문서 유형" value={document.documentType} />
        <AmountItem label="제출 상태" value={document.submissionStatus} />
        <AmountItem label="총 손해액" value={document.totalDamageAmount} strong />
        <AmountItem label="과실비율" value={`${document.faultRatio}%`} />
        <AmountItem label="결재 사원번호" value={document.employeeNo || '미입력'} />
        <AmountItem label="사고 상태" value={getAccidentStatusLabel(document.accidentStatus)} strong />
      </dl>

      <div className="text-detail-blocks approval-opinions">
        <TextBlock title="과실비율 소견" value={document.faultRatioOpinion || '미입력'} />
        <TextBlock title="손해사정인 소견" value={document.adjusterOpinion || '미입력'} />
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
