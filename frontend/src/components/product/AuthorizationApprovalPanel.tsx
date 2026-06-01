import { useState } from 'react';
import { CheckCircle2, FileEdit, XCircle, XOctagon } from 'lucide-react';
import {
  approveAuthorization,
  cancelAuthorization,
  rejectAuthorization,
  requestAuthorizationRevision
} from '../../api/productApi';
import type { AuthorizationResponse } from '../../types/product';
import { AlertMessage } from './AlertMessage';

interface AuthorizationApprovalPanelProps {
  productCode: string;
  data: AuthorizationResponse;
  onUpdate: (data: AuthorizationResponse) => void;
}

export function AuthorizationApprovalPanel({ productCode, data, onUpdate }: AuthorizationApprovalPanelProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [revisionMode, setRevisionMode] = useState(false);
  const [revisionRequest, setRevisionRequest] = useState('');

  const isRequested = data.authorizationStatus === 'REQUESTED';

  const runAction = async (action: () => Promise<AuthorizationResponse>) => {
    setIsSubmitting(true);
    setError(null);
    try {
      onUpdate(await action());
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '인가 상태 전환 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleApprove = () => runAction(() => approveAuthorization(productCode));
  const handleReject = () => runAction(() => rejectAuthorization(productCode));
  const handleCancel = () => runAction(() => cancelAuthorization(productCode));
  const handleRevisionSubmit = async () => {
    if (!revisionRequest.trim()) {
      setError('보완 요청 사항을 입력하세요.');
      return;
    }
    await runAction(() =>
      requestAuthorizationRevision(productCode, { revisionRequest: revisionRequest.trim() })
    );
    setRevisionMode(false);
    setRevisionRequest('');
  };

  return (
    <section className="work-panel detail-panel approval-card">
      <div className="panel-header compact">
        <div>
          <h2>인가 결과 처리</h2>
          <p>
            {isRequested
              ? '금융감독원 역할로 인가 결과를 선택합니다. 요청 취소는 상품개발자가 진행합니다.'
              : `현재 상태(${data.authorizationStatus})에서는 추가 처리가 불가합니다.`}
          </p>
        </div>
      </div>

      {error && <AlertMessage type="error" message={error} />}

      {isRequested && revisionMode ? (
        <>
          <label className="field form-section">
            <span>보완 요청 사항</span>
            <textarea
              value={revisionRequest}
              onChange={(event) => setRevisionRequest(event.target.value)}
              placeholder="약관 수정사항, 요율 수정사항, 첨부서류 보완사항 등을 입력하세요."
              disabled={isSubmitting}
            />
          </label>
          <div className="form-actions">
            <button
              type="button"
              className="button secondary"
              onClick={() => {
                setRevisionMode(false);
                setRevisionRequest('');
                setError(null);
              }}
              disabled={isSubmitting}
            >
              취소
            </button>
            <button type="button" className="button primary" onClick={handleRevisionSubmit} disabled={isSubmitting}>
              <FileEdit aria-hidden="true" size={16} />
              {isSubmitting ? '처리 중...' : '보완 요청 등록'}
            </button>
          </div>
        </>
      ) : isRequested ? (
        <div className="form-actions">
          <button type="button" className="button primary" onClick={handleApprove} disabled={isSubmitting}>
            <CheckCircle2 aria-hidden="true" size={16} />
            인가 승인
          </button>
          <button type="button" className="button secondary" onClick={handleReject} disabled={isSubmitting}>
            <XCircle aria-hidden="true" size={16} />
            인가 불허
          </button>
          <button
            type="button"
            className="button secondary"
            onClick={() => setRevisionMode(true)}
            disabled={isSubmitting}
          >
            <FileEdit aria-hidden="true" size={16} />
            보완 요청
          </button>
          <button type="button" className="button secondary" onClick={handleCancel} disabled={isSubmitting}>
            <XOctagon aria-hidden="true" size={16} />
            요청 취소
          </button>
        </div>
      ) : null}
    </section>
  );
}
