import { FormEvent, useState } from 'react';
import { createPaymentCollection } from '../../api/contractApi';
import {
  PAYMENT_METHOD_LABELS,
  type PaymentCollectionResponse,
  type PaymentMethod
} from '../../types/contract';
import { AlertMessage } from './AlertMessage';

interface PaymentCollectionFormProps {
  policyNumber: string;
  onCreated: (collection: PaymentCollectionResponse) => void;
}

const PAYMENT_METHOD_OPTIONS: PaymentMethod[] = [
  'AUTO_TRANSFER',
  'BANK_TRANSFER',
  'CREDIT_CARD',
  'VISIT_COLLECTION'
];

export function PaymentCollectionForm({ policyNumber, onCreated }: PaymentCollectionFormProps) {
  const [installmentNo, setInstallmentNo] = useState<string>('1');
  const [dueDate, setDueDate] = useState<string>('');
  const [plannedAmount, setPlannedAmount] = useState<string>('');
  const [collectedAmount, setCollectedAmount] = useState<string>('');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('AUTO_TRANSFER');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    const installment = Number(installmentNo);
    const planned = Number(plannedAmount);
    const collected = Number(collectedAmount);

    if (!Number.isInteger(installment) || installment < 1) {
      setError('회차는 1 이상 정수여야 합니다.');
      return;
    }
    if (!dueDate) {
      setError('납기일을 입력하세요.');
      return;
    }
    if (!Number.isFinite(planned) || planned <= 0) {
      setError('보험료(예정 금액)는 0보다 커야 합니다.');
      return;
    }
    if (!Number.isFinite(collected) || collected < 0) {
      setError('수금 금액은 0 이상이어야 합니다.');
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const response = await createPaymentCollection(policyNumber, {
        installmentNo: installment,
        dueDate,
        plannedAmount: planned,
        collectedAmount: collected,
        paymentMethod
      });
      onCreated(response);
      setCollectedAmount('');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '수금 처리에 실패했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="work-panel" onSubmit={handleSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>수금 처리</h2>
          <p>회차/납기/예정 보험료/수금 금액을 입력합니다. 수금액이 보험료보다 적으면 미수금 + 연체료 자동 산정.</p>
        </div>
      </div>

      <div className="form-grid">
        <label>
          <span>회차</span>
          <input
            type="number"
            min="1"
            step="1"
            value={installmentNo}
            onChange={(event) => setInstallmentNo(event.target.value)}
            disabled={isSubmitting}
            required
          />
        </label>
        <label>
          <span>납기일</span>
          <input
            type="date"
            value={dueDate}
            onChange={(event) => setDueDate(event.target.value)}
            disabled={isSubmitting}
            required
          />
        </label>
        <label>
          <span>보험료 (원)</span>
          <input
            type="number"
            min="0"
            step="1"
            value={plannedAmount}
            onChange={(event) => setPlannedAmount(event.target.value)}
            placeholder="예: 120000"
            disabled={isSubmitting}
            required
          />
        </label>
        <label>
          <span>수금 금액 (원)</span>
          <input
            type="number"
            min="0"
            step="1"
            value={collectedAmount}
            onChange={(event) => setCollectedAmount(event.target.value)}
            placeholder="미수금이면 0"
            disabled={isSubmitting}
            required
          />
        </label>
        <label>
          <span>수금 방식</span>
          <select
            value={paymentMethod}
            onChange={(event) => setPaymentMethod(event.target.value as PaymentMethod)}
            disabled={isSubmitting}
          >
            {PAYMENT_METHOD_OPTIONS.map((method) => (
              <option key={method} value={method}>
                {PAYMENT_METHOD_LABELS[method]}
              </option>
            ))}
          </select>
        </label>
      </div>

      {error && <AlertMessage type="error" message={error} />}

      <div className="form-actions">
        <button className="button primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? '처리 중...' : '수금 처리'}
        </button>
      </div>
    </form>
  );
}
