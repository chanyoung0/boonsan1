import { CalendarDays, ClipboardList, FileText, ShieldCheck, Wallet } from 'lucide-react';
import type { ReactNode } from 'react';
import type { ProductResponse } from '../../types/product';
import { getInsuranceTypeLabel } from '../../types/product';
import { ProductStatusBadge } from './ProductStatusBadge';

interface ProductDetailCardProps {
  data: ProductResponse;
}

export function ProductDetailCard({ data }: ProductDetailCardProps) {
  return (
    <article className="work-panel detail-panel">
      <div className="panel-header detail-title">
        <div>
          <h2>상품 상세</h2>
          <p>{data.productCode}</p>
        </div>
        <ProductStatusBadge status={data.productStatus} />
      </div>

      <div className="detail-grid">
        <DetailItem icon={<ClipboardList size={17} />} label="상품 코드" value={data.productCode} mono />
        <DetailItem icon={<ClipboardList size={17} />} label="상품명" value={data.productName} />
        <DetailItem icon={<ShieldCheck size={17} />} label="상품유형" value={getInsuranceTypeLabel(data.insuranceTypeCode)} />
        <DetailItem icon={<CalendarDays size={17} />} label="설계 일시" value={formatDateTime(data.createdAt)} />
        <DetailItem icon={<FileText size={17} />} label="적용대상" value={data.targetCustomer ?? '-'} />
        <DetailItem icon={<FileText size={17} />} label="판매채널" value={data.salesChannel ?? '-'} />
        <DetailItem icon={<CalendarDays size={17} />} label="보험기간" value={data.insurancePeriod ?? '-'} />
        <DetailItem icon={<CalendarDays size={17} />} label="납입기간" value={data.paymentPeriod ?? '-'} />
        <DetailItem icon={<Wallet size={17} />} label="보험가입금액" value={formatAmount(data.insuredAmount)} />
        <DetailItem icon={<Wallet size={17} />} label="예상 보험료" value={formatAmount(data.premium)} />
        <DetailItem icon={<Wallet size={17} />} label="만기환급금" value={formatAmount(data.maturityRefund)} />
      </div>

      <div className="text-detail-blocks">
        {data.mainCoverage && <TextBox title="주담보 / 보장내용" value={data.mainCoverage} />}
        {data.subscriptionConditions && <TextBox title="가입조건" value={data.subscriptionConditions} />}
        {data.specialContractInfo && <TextBox title="특약 정보" value={data.specialContractInfo} />}
      </div>

      {hasRateInfo(data) && (
        <div className="detail-grid">
          <DetailItem icon={<Wallet size={17} />} label="기초요율" value={formatRate(data.baseRate)} />
          <DetailItem icon={<Wallet size={17} />} label="위험률" value={formatRate(data.riskRate)} />
          <DetailItem icon={<Wallet size={17} />} label="예정이율" value={formatRate(data.expectedInterestRate)} />
          <DetailItem icon={<Wallet size={17} />} label="사업비율" value={formatRate(data.operatingExpenseRatio)} />
          <DetailItem icon={<Wallet size={17} />} label="할인/할증요율" value={formatRate(data.discountSurchargeRate)} />
          <DetailItem icon={<Wallet size={17} />} label="적용요율" value={formatAppliedRate(data.appliedRate)} />
          <DetailItem icon={<Wallet size={17} />} label="손익예상치" value={formatAmount(data.profitLossEstimate)} />
        </div>
      )}

      {renderSubtypeDetails(data)}
    </article>
  );
}

function renderSubtypeDetails(data: ProductResponse) {
  if (data.insuranceTypeCode === 'AUTO' && (data.driverAge != null || data.vehicleType)) {
    return (
      <div className="detail-grid">
        <DetailItem icon={<FileText size={17} />} label="운전자 연령" value={data.driverAge?.toString() ?? '-'} />
        <DetailItem icon={<FileText size={17} />} label="차량 유형" value={data.vehicleType ?? '-'} />
      </div>
    );
  }
  if (data.insuranceTypeCode === 'FIRE' && (data.buildingType || data.location)) {
    return (
      <div className="detail-grid">
        <DetailItem icon={<FileText size={17} />} label="건물 유형" value={data.buildingType ?? '-'} />
        <DetailItem icon={<FileText size={17} />} label="소재지" value={data.location ?? '-'} />
      </div>
    );
  }
  if (data.insuranceTypeCode === 'MARINE' && (data.shippingRoute || data.vesselType)) {
    return (
      <div className="detail-grid">
        <DetailItem icon={<FileText size={17} />} label="운항 항로" value={data.shippingRoute ?? '-'} />
        <DetailItem icon={<FileText size={17} />} label="선박 유형" value={data.vesselType ?? '-'} />
      </div>
    );
  }
  return null;
}

function DetailItem({
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

function TextBox({ title, value }: { title: string; value: string }) {
  return (
    <section className="text-box">
      <h3>{title}</h3>
      <p>{value}</p>
    </section>
  );
}

function formatAmount(value: number | null) {
  if (value == null) return '-';
  return new Intl.NumberFormat('ko-KR').format(value) + ' 원';
}

function formatRate(value: number | null) {
  if (value == null) return '-';
  return value.toFixed(2) + ' %';
}

function formatAppliedRate(value: number | null) {
  if (value == null) return '-';
  return (value * 100).toFixed(4) + ' %';
}

function hasRateInfo(data: ProductResponse) {
  return (
    data.baseRate != null ||
    data.riskRate != null ||
    data.expectedInterestRate != null ||
    data.operatingExpenseRatio != null ||
    data.discountSurchargeRate != null ||
    data.appliedRate != null ||
    data.profitLossEstimate != null
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
