import { Check, CheckCircle2, ClipboardList, Copy, Wallet } from 'lucide-react';
import { useState, type ReactNode } from 'react';
import type { ProductResponse } from '../../types/product';
import { getInsuranceTypeLabel } from '../../types/product';
import { ProductStatusBadge } from './ProductStatusBadge';

interface ProductDesignResultCardProps {
  data: ProductResponse;
}

export function ProductDesignResultCard({ data }: ProductDesignResultCardProps) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    await navigator.clipboard.writeText(data.productCode);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1600);
  };

  return (
    <aside className="work-panel result-panel">
      <div className="result-heading">
        <CheckCircle2 aria-hidden="true" size={22} />
        <div>
          <h2>설계 완료</h2>
          <p>상품 설계가 정상적으로 저장되었습니다.</p>
        </div>
      </div>

      <div className="number-box">
        <span>상품 코드</span>
        <strong title={data.productCode}>{data.productCode}</strong>
        <button className="icon-text-button" type="button" onClick={handleCopy}>
          {copied ? <Check aria-hidden="true" size={16} /> : <Copy aria-hidden="true" size={16} />}
          {copied ? '복사됨' : '복사'}
        </button>
      </div>

      <dl className="summary-list">
        <SummaryItem icon={<ClipboardList size={17} />} label="상품명" value={data.productName} />
        <SummaryItem icon={<ClipboardList size={17} />} label="상품유형" value={getInsuranceTypeLabel(data.insuranceTypeCode)} />
        <SummaryItem icon={<Wallet size={17} />} label="보험가입금액" value={formatAmount(data.insuredAmount)} />
        <div className="summary-item">
          <dt>상품 상태</dt>
          <dd>
            <ProductStatusBadge status={data.productStatus} />
          </dd>
        </div>
      </dl>
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

function formatAmount(value: number | null) {
  if (value == null) return '-';
  return new Intl.NumberFormat('ko-KR').format(value) + ' 원';
}
