import { useState, type FormEvent } from 'react';
import { Send } from 'lucide-react';
import type { InvestigationApprovalRequest } from '../../types/claim';

interface InvestigationApprovalPanelProps {
  accidentNumber: string;
  disabled: boolean;
  isSubmitting: boolean;
  onSubmit: (request: InvestigationApprovalRequest) => Promise<void>;
}

export function InvestigationApprovalPanel({
  accidentNumber,
  disabled,
  isSubmitting,
  onSubmit
}: InvestigationApprovalPanelProps) {
  const [employeeNo, setEmployeeNo] = useState('');

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await onSubmit({
      accidentNumber,
      employeeNo: employeeNo.trim()
    });
  };

  return (
    <form className="work-panel form-panel" onSubmit={handleSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>결재 요청</h2>
          <p>결재 요청 후 사고 접수 상태는 결재 필요로 변경됩니다.</p>
        </div>
        <Send aria-hidden="true" size={22} />
      </div>

      <label className="field">
        <span>사원번호</span>
        <input
          required
          value={employeeNo}
          onChange={(event) => setEmployeeNo(event.target.value)}
          disabled={disabled || isSubmitting}
          placeholder="EMP-001"
        />
      </label>

      <div className="inline-note investigation-note">
        <span>보험금 지급 단계로 자동 이동하지 않고, 이번 단계에서는 결재 필요 상태까지만 변경합니다.</span>
      </div>

      <div className="form-actions">
        <button className="button primary" type="submit" disabled={disabled || isSubmitting}>
          {isSubmitting ? '결재 요청 중...' : '결재 요청'}
        </button>
      </div>
    </form>
  );
}
