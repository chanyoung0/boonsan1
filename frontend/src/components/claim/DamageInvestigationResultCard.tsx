import { ClipboardCheck } from 'lucide-react';
import type { DamageInvestigationResultResponse } from '../../types/claim';

interface DamageInvestigationResultCardProps {
  result: DamageInvestigationResultResponse;
}

export function DamageInvestigationResultCard({ result }: DamageInvestigationResultCardProps) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>저장된 손해액 정보</h2>
          <p>이미 저장된 손해조사 결과입니다. 중복 작성을 막기 위해 입력 폼을 표시하지 않습니다.</p>
        </div>
        <ClipboardCheck aria-hidden="true" size={22} />
      </div>

      <dl className="amount-grid">
        <AmountItem label="손해사정인 ID" value={result.adjusterId} />
        <AmountItem label="조사 일시" value={formatDateTime(result.investigationAt)} />
        <AmountItem label="치료비" value={result.medicalExpense} />
        <AmountItem label="휴업손해" value={result.lostIncome} />
        <AmountItem label="수리비" value={result.repairCost} />
        <AmountItem label="합의금" value={result.settlementAmount} />
        <AmountItem label="지급 인정 비율(%)" value={`${result.faultRatio}%`} />
        <AmountItem label="총 손해액" value={result.totalDamageAmount} strong />
        <AmountItem label="산정 지급액" value={result.calculatedPaymentAmount} strong />
      </dl>
    </section>
  );
}

function AmountItem({ label, value, strong = false }: { label: string; value: number | string; strong?: boolean }) {
  const displayValue = typeof value === 'number' ? `${Math.round(value).toLocaleString('ko-KR')}원` : value;
  return (
    <div className={`amount-item ${strong ? 'strong' : ''}`}>
      <dt>{label}</dt>
      <dd title={displayValue}>{displayValue}</dd>
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
