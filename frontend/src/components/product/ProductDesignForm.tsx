import { useState, type FormEvent, type ReactNode } from 'react';
import { FileText, RotateCcw, Save, Settings2, ShieldCheck, Wallet } from 'lucide-react';
import { createProduct } from '../../api/productApi';
import type {
  InsuranceTypeCode,
  ProductDesignRequest,
  ProductResponse
} from '../../types/product';
import { INSURANCE_TYPE_LABELS } from '../../types/product';
import { AlertMessage } from './AlertMessage';

interface ProductDesignFormProps {
  onSuccess: (data: ProductResponse) => void;
}

const emptyForm = {
  productName: '',
  insuranceTypeCode: '' as InsuranceTypeCode | '',
  targetCustomer: '',
  salesChannel: '',
  insurancePeriod: '',
  paymentPeriod: '',
  insuredAmount: '',
  premium: '',
  maturityRefund: '',
  mainCoverage: '',
  subscriptionConditions: '',
  rateInformation: '',
  specialContractInfo: '',
  driverAge: '',
  vehicleType: '',
  buildingType: '',
  location: '',
  shippingRoute: '',
  vesselType: ''
};

export function ProductDesignForm({ onSuccess }: ProductDesignFormProps) {
  const [formData, setFormData] = useState(emptyForm);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const updateField = (name: keyof typeof formData, value: string) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (!formData.productName.trim() || !formData.insuranceTypeCode || !formData.insuredAmount.trim()) {
      setError('상품명, 상품유형, 보험가입금액은 필수 항목입니다.');
      return;
    }

    const insuredAmountNumber = Number(formData.insuredAmount);
    if (!Number.isFinite(insuredAmountNumber) || insuredAmountNumber <= 0) {
      setError('보험가입금액은 양수로 입력하세요.');
      return;
    }

    const request: ProductDesignRequest = {
      productName: formData.productName.trim(),
      insuranceTypeCode: formData.insuranceTypeCode,
      targetCustomer: normalizeOptional(formData.targetCustomer),
      salesChannel: normalizeOptional(formData.salesChannel),
      insurancePeriod: normalizeOptional(formData.insurancePeriod),
      paymentPeriod: normalizeOptional(formData.paymentPeriod),
      insuredAmount: insuredAmountNumber,
      premium: optionalNumber(formData.premium),
      maturityRefund: optionalNumber(formData.maturityRefund),
      mainCoverage: normalizeOptional(formData.mainCoverage),
      subscriptionConditions: normalizeOptional(formData.subscriptionConditions),
      rateInformation: normalizeOptional(formData.rateInformation),
      specialContractInfo: normalizeOptional(formData.specialContractInfo),
      driverAge: formData.insuranceTypeCode === 'AUTO' ? optionalNumber(formData.driverAge) : null,
      vehicleType: formData.insuranceTypeCode === 'AUTO' ? normalizeOptional(formData.vehicleType) : null,
      buildingType: formData.insuranceTypeCode === 'FIRE' ? normalizeOptional(formData.buildingType) : null,
      location: formData.insuranceTypeCode === 'FIRE' ? normalizeOptional(formData.location) : null,
      shippingRoute: formData.insuranceTypeCode === 'MARINE' ? normalizeOptional(formData.shippingRoute) : null,
      vesselType: formData.insuranceTypeCode === 'MARINE' ? normalizeOptional(formData.vesselType) : null
    };

    setIsSubmitting(true);
    try {
      const response = await createProduct(request);
      onSuccess(response);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '상품 설계 등록 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleReset = () => {
    setFormData(emptyForm);
    setError(null);
  };

  const insuranceType = formData.insuranceTypeCode;

  return (
    <form className="work-panel form-panel" onSubmit={handleSubmit}>
      <div className="panel-header">
        <div>
          <h2>상품 설계 입력</h2>
          <p>상품 기본정보, 담보, 가입조건, 보험요율, 특약 정보를 입력합니다.</p>
        </div>
      </div>

      {error && <AlertMessage type="error" message={error} />}

      <section className="form-section">
        <SectionTitle icon={<FileText size={17} />} title="상품 기본정보" />
        <div className="field-grid two">
          <label className="field">
            <span>상품명</span>
            <input
              required
              value={formData.productName}
              onChange={(event) => updateField('productName', event.target.value)}
              placeholder="분산 운전자보험"
              disabled={isSubmitting}
            />
          </label>
          <label className="field">
            <span>상품유형</span>
            <select
              required
              value={formData.insuranceTypeCode}
              onChange={(event) => updateField('insuranceTypeCode', event.target.value)}
              disabled={isSubmitting}
            >
              <option value="">상품유형을 선택하세요</option>
              {(Object.keys(INSURANCE_TYPE_LABELS) as InsuranceTypeCode[]).map((type) => (
                <option value={type} key={type}>
                  {INSURANCE_TYPE_LABELS[type]}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            <span>적용대상</span>
            <input
              value={formData.targetCustomer}
              onChange={(event) => updateField('targetCustomer', event.target.value)}
              placeholder="만 19세 이상 개인"
              disabled={isSubmitting}
            />
          </label>
          <label className="field">
            <span>판매채널</span>
            <input
              value={formData.salesChannel}
              onChange={(event) => updateField('salesChannel', event.target.value)}
              placeholder="설계사 / 다이렉트"
              disabled={isSubmitting}
            />
          </label>
          <label className="field">
            <span>보험기간</span>
            <input
              value={formData.insurancePeriod}
              onChange={(event) => updateField('insurancePeriod', event.target.value)}
              placeholder="10년"
              disabled={isSubmitting}
            />
          </label>
          <label className="field">
            <span>납입기간</span>
            <input
              value={formData.paymentPeriod}
              onChange={(event) => updateField('paymentPeriod', event.target.value)}
              placeholder="월납 10년"
              disabled={isSubmitting}
            />
          </label>
        </div>
      </section>

      <section className="form-section">
        <SectionTitle icon={<ShieldCheck size={17} />} title="담보·보장 정보" />
        <label className="field full">
          <span>주담보 및 보장내용</span>
          <textarea
            value={formData.mainCoverage}
            onChange={(event) => updateField('mainCoverage', event.target.value)}
            placeholder="주담보명, 보장내용, 보험가입금액 한도, 면책조건, 지급조건"
            disabled={isSubmitting}
          />
        </label>
        <label className="field full">
          <span>가입조건</span>
          <textarea
            value={formData.subscriptionConditions}
            onChange={(event) => updateField('subscriptionConditions', event.target.value)}
            placeholder="가입가능연령, 가입제한조건, 직업조건, 건강조건, 계약제한사유"
            disabled={isSubmitting}
          />
        </label>
      </section>

      <section className="form-section">
        <SectionTitle icon={<Wallet size={17} />} title="보험금·요율" />
        <div className="field-grid three">
          <label className="field">
            <span>보험가입금액</span>
            <input
              required
              type="number"
              min="0"
              value={formData.insuredAmount}
              onChange={(event) => updateField('insuredAmount', event.target.value)}
              placeholder="100000000"
              disabled={isSubmitting}
            />
          </label>
          <label className="field">
            <span>예상 보험료</span>
            <input
              type="number"
              min="0"
              value={formData.premium}
              onChange={(event) => updateField('premium', event.target.value)}
              placeholder="120000"
              disabled={isSubmitting}
            />
          </label>
          <label className="field">
            <span>만기환급금</span>
            <input
              type="number"
              min="0"
              value={formData.maturityRefund}
              onChange={(event) => updateField('maturityRefund', event.target.value)}
              placeholder="0"
              disabled={isSubmitting}
            />
          </label>
        </div>
        <label className="field full">
          <span>요율 정보</span>
          <textarea
            value={formData.rateInformation}
            onChange={(event) => updateField('rateInformation', event.target.value)}
            placeholder="기초요율, 위험률, 예정이율, 사업비율, 할인/할증요율"
            disabled={isSubmitting}
          />
        </label>
      </section>

      <section className="form-section">
        <SectionTitle icon={<FileText size={17} />} title="특약 정보" />
        <label className="field full">
          <span>특약 정보</span>
          <textarea
            value={formData.specialContractInfo}
            onChange={(event) => updateField('specialContractInfo', event.target.value)}
            placeholder="특약명, 보장내용, 가입조건, 특약보험료, 중복가입 가능여부"
            disabled={isSubmitting}
          />
        </label>
      </section>

      {insuranceType && (
        <section className="form-section">
          <SectionTitle icon={<Settings2 size={17} />} title={`${INSURANCE_TYPE_LABELS[insuranceType]} 세부 정보`} />
          <div className="field-grid two">
            {insuranceType === 'AUTO' && (
              <>
                <label className="field">
                  <span>운전자 연령</span>
                  <input
                    type="number"
                    min="0"
                    value={formData.driverAge}
                    onChange={(event) => updateField('driverAge', event.target.value)}
                    placeholder="만 26세 이상"
                    disabled={isSubmitting}
                  />
                </label>
                <label className="field">
                  <span>차량 유형</span>
                  <input
                    value={formData.vehicleType}
                    onChange={(event) => updateField('vehicleType', event.target.value)}
                    placeholder="승용차 / 화물차"
                    disabled={isSubmitting}
                  />
                </label>
              </>
            )}
            {insuranceType === 'FIRE' && (
              <>
                <label className="field">
                  <span>건물 유형</span>
                  <input
                    value={formData.buildingType}
                    onChange={(event) => updateField('buildingType', event.target.value)}
                    placeholder="주거용 / 상업용"
                    disabled={isSubmitting}
                  />
                </label>
                <label className="field">
                  <span>소재지</span>
                  <input
                    value={formData.location}
                    onChange={(event) => updateField('location', event.target.value)}
                    placeholder="서울시 종로구"
                    disabled={isSubmitting}
                  />
                </label>
              </>
            )}
            {insuranceType === 'MARINE' && (
              <>
                <label className="field">
                  <span>운항 항로</span>
                  <input
                    value={formData.shippingRoute}
                    onChange={(event) => updateField('shippingRoute', event.target.value)}
                    placeholder="부산 - 상하이"
                    disabled={isSubmitting}
                  />
                </label>
                <label className="field">
                  <span>선박 유형</span>
                  <input
                    value={formData.vesselType}
                    onChange={(event) => updateField('vesselType', event.target.value)}
                    placeholder="컨테이너선"
                    disabled={isSubmitting}
                  />
                </label>
              </>
            )}
          </div>
        </section>
      )}

      <div className="form-actions">
        <button type="button" className="button secondary" onClick={handleReset} disabled={isSubmitting}>
          <RotateCcw aria-hidden="true" size={16} />
          입력 초기화
        </button>
        <button type="submit" className="button primary" disabled={isSubmitting}>
          <Save aria-hidden="true" size={16} />
          {isSubmitting ? '저장 중...' : '상품 설계 저장'}
        </button>
      </div>
    </form>
  );
}

function SectionTitle({ icon, title }: { icon: ReactNode; title: string }) {
  return (
    <div className="section-title">
      {icon}
      <h3>{title}</h3>
    </div>
  );
}

function normalizeOptional(value: string) {
  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed : null;
}

function optionalNumber(value: string): number | null {
  const trimmed = value.trim();
  if (trimmed.length === 0) return null;
  const numeric = Number(trimmed);
  return Number.isFinite(numeric) ? numeric : null;
}
