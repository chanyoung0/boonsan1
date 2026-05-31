import { useState, type FormEvent } from 'react';
import { Calculator } from 'lucide-react';
import type { DamageAssessmentRequest } from '../../types/claim';

interface DamageAssessmentFormProps {
  accidentNumber: string;
  disabled: boolean;
  isSubmitting: boolean;
  onSubmit: (request: DamageAssessmentRequest) => Promise<void>;
}

const initialForm = {
  adjusterId: '',
  investigationAt: '',
  medicalExpense: '',
  lostIncome: '',
  repairCost: '',
  settlementAmount: '',
  faultRatio: ''
};

export function DamageAssessmentForm({
  accidentNumber,
  disabled,
  isSubmitting,
  onSubmit
}: DamageAssessmentFormProps) {
  const [formData, setFormData] = useState(initialForm);

  const updateField = (name: keyof typeof formData, value: string) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await onSubmit({
      accidentNumber,
      adjusterId: formData.adjusterId.trim(),
      investigationAt: formData.investigationAt,
      medicalExpense: toNumber(formData.medicalExpense),
      lostIncome: toNumber(formData.lostIncome),
      repairCost: toNumber(formData.repairCost),
      settlementAmount: toNumber(formData.settlementAmount),
      faultRatio: toNumber(formData.faultRatio)
    });
  };

  return (
    <form className="work-panel form-panel" onSubmit={handleSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>손해액 정보 입력</h2>
          <p>손해사정인이 조사한 손해액과 과실비율을 입력합니다.</p>
        </div>
        <Calculator aria-hidden="true" size={22} />
      </div>

      <div className="field-grid two">
        <label className="field">
          <span>손해사정인 ID</span>
          <input
            required
            value={formData.adjusterId}
            onChange={(event) => updateField('adjusterId', event.target.value)}
            disabled={disabled || isSubmitting}
            placeholder="ADJ-001"
          />
        </label>
        <label className="field">
          <span>조사 일시</span>
          <input
            required
            type="datetime-local"
            value={formData.investigationAt}
            onChange={(event) => updateField('investigationAt', event.target.value)}
            disabled={disabled || isSubmitting}
          />
        </label>
        <MoneyField
          label="치료비"
          value={formData.medicalExpense}
          onChange={(value) => updateField('medicalExpense', value)}
          disabled={disabled || isSubmitting}
        />
        <MoneyField
          label="휴업손해"
          value={formData.lostIncome}
          onChange={(value) => updateField('lostIncome', value)}
          disabled={disabled || isSubmitting}
        />
        <MoneyField
          label="수리비"
          value={formData.repairCost}
          onChange={(value) => updateField('repairCost', value)}
          disabled={disabled || isSubmitting}
        />
        <MoneyField
          label="합의금"
          value={formData.settlementAmount}
          onChange={(value) => updateField('settlementAmount', value)}
          disabled={disabled || isSubmitting}
        />
        <label className="field">
          <span>과실비율</span>
          <input
            required
            min="0"
            max="100"
            step="0.1"
            type="number"
            value={formData.faultRatio}
            onChange={(event) => updateField('faultRatio', event.target.value)}
            disabled={disabled || isSubmitting}
            placeholder="20"
          />
        </label>
      </div>

      <div className="form-actions">
        <button className="button primary" type="submit" disabled={disabled || isSubmitting}>
          {isSubmitting ? '초안 작성 중...' : '지급품의서 초안 작성'}
        </button>
      </div>
    </form>
  );
}

function MoneyField({
  label,
  value,
  onChange,
  disabled
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  disabled: boolean;
}) {
  return (
    <label className="field">
      <span>{label}</span>
      <input
        required
        min="0"
        step="1000"
        type="number"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        disabled={disabled}
        placeholder="0"
      />
    </label>
  );
}

function toNumber(value: string) {
  return Number(value || 0);
}
