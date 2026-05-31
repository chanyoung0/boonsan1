import { useState, type FormEvent } from 'react';
import { MessageSquareText } from 'lucide-react';
import type { AdjusterOpinionRequest } from '../../types/claim';

interface AdjusterOpinionFormProps {
  accidentNumber: string;
  disabled: boolean;
  isSubmitting: boolean;
  onSubmit: (request: AdjusterOpinionRequest) => Promise<void>;
}

export function AdjusterOpinionForm({
  accidentNumber,
  disabled,
  isSubmitting,
  onSubmit
}: AdjusterOpinionFormProps) {
  const [faultRatioOpinion, setFaultRatioOpinion] = useState('');
  const [adjusterOpinion, setAdjusterOpinion] = useState('');

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await onSubmit({
      accidentNumber,
      faultRatioOpinion: faultRatioOpinion.trim(),
      adjusterOpinion: adjusterOpinion.trim()
    });
  };

  return (
    <form className="work-panel form-panel" onSubmit={handleSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>손해사정인 소견</h2>
          <p>지급 인정 비율 소견과 최종 지급품의서에 반영할 소견을 작성합니다.</p>
        </div>
        <MessageSquareText aria-hidden="true" size={22} />
      </div>

      <div className="field-grid">
        <label className="field">
          <span>지급 인정 비율 소견</span>
          <textarea
            required
            value={faultRatioOpinion}
            onChange={(event) => setFaultRatioOpinion(event.target.value)}
            disabled={disabled || isSubmitting}
            placeholder="지급 인정 비율 판단 근거를 입력하세요."
          />
        </label>
        <label className="field">
          <span>손해사정인 소견</span>
          <textarea
            required
            value={adjusterOpinion}
            onChange={(event) => setAdjusterOpinion(event.target.value)}
            disabled={disabled || isSubmitting}
            placeholder="지급품의서에 반영할 최종 소견을 입력하세요."
          />
        </label>
      </div>

      <div className="form-actions">
        <button className="button primary" type="submit" disabled={disabled || isSubmitting}>
          {isSubmitting ? '소견 저장 중...' : '최종 지급품의서 작성'}
        </button>
      </div>
    </form>
  );
}
