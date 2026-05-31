import { ClipboardList } from 'lucide-react';
import type { PaymentApprovalDraftResponse } from '../../types/claim';

interface PaymentApprovalDraftCardProps {
  draft: PaymentApprovalDraftResponse;
}

export function PaymentApprovalDraftCard({ draft }: PaymentApprovalDraftCardProps) {
  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>지급품의서 초안</h2>
          <p>{draft.draftMessage}</p>
        </div>
        <ClipboardList aria-hidden="true" size={22} />
      </div>

      <dl className="amount-grid">
        <AmountItem label="치료비" value={draft.medicalExpense} />
        <AmountItem label="휴업손해" value={draft.lostIncome} />
        <AmountItem label="수리비" value={draft.repairCost} />
        <AmountItem label="총 손해액" value={draft.totalDamageAmount} strong />
        <AmountItem label="지급 인정 비율(%)" value={`${draft.faultRatio}%`} />
        <AmountItem label="합의금" value={draft.settlementAmount} />
        <AmountItem label="산정 지급액" value={draft.calculatedPaymentAmount} strong />
      </dl>
    </section>
  );
}

function AmountItem({ label, value, strong = false }: { label: string; value: number | string; strong?: boolean }) {
  const displayValue = typeof value === 'number' ? formatMoney(value) : value;
  return (
    <div className={`amount-item ${strong ? 'strong' : ''}`}>
      <dt>{label}</dt>
      <dd>{displayValue}</dd>
    </div>
  );
}

function formatMoney(value: number) {
  return `${Math.round(value).toLocaleString('ko-KR')}원`;
}
