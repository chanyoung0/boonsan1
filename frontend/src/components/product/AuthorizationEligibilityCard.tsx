import { BadgeCheck } from 'lucide-react';
import type { AuthorizationEligibilityResponse } from '../../types/product';
import { getInsuranceTypeLabel, getProductStatusLabel } from '../../types/product';

interface AuthorizationEligibilityCardProps {
  data: AuthorizationEligibilityResponse;
}

export function AuthorizationEligibilityCard({ data }: AuthorizationEligibilityCardProps) {
  return (
    <section className={`work-panel detail-panel approval-card ${data.eligible ? 'next-step-panel' : ''}`}>
      <div className="panel-header compact">
        <div>
          <h2>인가 가능 여부</h2>
          <p>{data.message}</p>
        </div>
        <BadgeCheck aria-hidden="true" size={22} />
      </div>
      <dl className="amount-grid">
        <AmountItem label="상품 코드" value={data.productCode} mono />
        <AmountItem label="상품명" value={data.productName ?? '미확인'} />
        <AmountItem
          label="상품유형"
          value={data.insuranceTypeCode ? getInsuranceTypeLabel(data.insuranceTypeCode) : '미확인'}
        />
        <AmountItem
          label="현재 상품 상태"
          value={data.productStatus ? getProductStatusLabel(data.productStatus) : '미확인'}
          strong={data.eligible}
        />
      </dl>
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
