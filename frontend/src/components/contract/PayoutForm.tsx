import { FormEvent, useState } from 'react';
import { createPayout } from '../../api/contractApi';
import {
  CALCULATION_BASIS_LABELS,
  PAYMENT_TYPE_LABELS,
  type CalculationBasis,
  type PaymentType,
  type PayoutResponse
} from '../../types/contract';
import { AlertMessage } from './AlertMessage';

interface PayoutFormProps {
  policyNumber: string;
  onCreated: (payout: PayoutResponse) => void;
}

const CALCULATION_BASIS_OPTIONS: CalculationBasis[] = [
  'MATURITY_REFUND',
  'SURRENDER',
  'MID_SURRENDER',
  'DIVIDEND'
];

const PAYMENT_TYPE_OPTIONS: PaymentType[] = ['LUMP_SUM', 'INSTALLMENT', 'LOAN_SETTLEMENT'];

export function PayoutForm({ policyNumber, onCreated }: PayoutFormProps) {
  const [calculationBasis, setCalculationBasis] = useState<CalculationBasis>('MATURITY_REFUND');
  const [paymentType, setPaymentType] = useState<PaymentType>('LUMP_SUM');
  const [paidPremiumAmount, setPaidPremiumAmount] = useState<string>('');
  const [deductionItem, setDeductionItem] = useState<string>('');
  const [deductionAmount, setDeductionAmount] = useState<string>('0');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    const paid = Number(paidPremiumAmount);
    const deduction = Number(deductionAmount || '0');

    if (!Number.isFinite(paid) || paid <= 0) {
      setError('납입보험료 합계는 0보다 큰 숫자여야 합니다.');
      return;
    }
    if (!Number.isFinite(deduction) || deduction < 0) {
      setError('공제 금액은 0 이상 숫자여야 합니다.');
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const response = await createPayout(policyNumber, {
        calculationBasis,
        paymentType,
        paidPremiumAmount: paid,
        deductionItem: deductionItem.trim() ? deductionItem.trim() : null,
        deductionAmount: deduction
      });
      onCreated(response);
      setPaidPremiumAmount('');
      setDeductionItem('');
      setDeductionAmount('0');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '제지급금 산정에 실패했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="work-panel" onSubmit={handleSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>제지급금 산정</h2>
          <p>지급사유와 납입보험료 합계를 입력하면 환급률을 적용해 산정합니다.</p>
        </div>
      </div>

      <div className="form-grid">
        <label>
          <span>지급사유</span>
          <select
            value={calculationBasis}
            onChange={(event) => setCalculationBasis(event.target.value as CalculationBasis)}
            disabled={isSubmitting}
          >
            {CALCULATION_BASIS_OPTIONS.map((basis) => (
              <option key={basis} value={basis}>
                {CALCULATION_BASIS_LABELS[basis]}
              </option>
            ))}
          </select>
        </label>
        <label>
          <span>지급 방식</span>
          <select
            value={paymentType}
            onChange={(event) => setPaymentType(event.target.value as PaymentType)}
            disabled={isSubmitting}
          >
            {PAYMENT_TYPE_OPTIONS.map((type) => (
              <option key={type} value={type}>
                {PAYMENT_TYPE_LABELS[type]}
              </option>
            ))}
          </select>
        </label>
        <label>
          <span>납입보험료 합계 (원)</span>
          <input
            type="number"
            min="0"
            step="1"
            value={paidPremiumAmount}
            onChange={(event) => setPaidPremiumAmount(event.target.value)}
            placeholder="예: 1440000"
            disabled={isSubmitting}
            required
          />
        </label>
        <label>
          <span>공제 사유</span>
          <input
            type="text"
            value={deductionItem}
            onChange={(event) => setDeductionItem(event.target.value)}
            placeholder="예: 미납보험료 / 대출잔액"
            disabled={isSubmitting}
          />
        </label>
        <label>
          <span>공제 금액 (원)</span>
          <input
            type="number"
            min="0"
            step="1"
            value={deductionAmount}
            onChange={(event) => setDeductionAmount(event.target.value)}
            disabled={isSubmitting}
          />
        </label>
      </div>

      {error && <AlertMessage type="error" message={error} />}

      <div className="form-actions">
        <button className="button primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? '산정 중...' : '제지급금 산정'}
        </button>
      </div>
    </form>
  );
}
