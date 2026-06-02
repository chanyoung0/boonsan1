import { useState } from 'react';
import { approvePayout, cancelPayout, payPayout } from '../../api/contractApi';
import {
  getCalculationBasisLabel,
  getPaymentTypeLabel,
  getPayoutStatusLabel,
  type PayoutResponse
} from '../../types/contract';
import { AlertMessage } from './AlertMessage';

interface PayoutTableProps {
  policyNumber: string;
  payouts: PayoutResponse[];
  onUpdated: (payout: PayoutResponse) => void;
}

export function PayoutTable({ policyNumber, payouts, onUpdated }: PayoutTableProps) {
  const [processorInput, setProcessorInput] = useState<Record<string, string>>({});
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleApprove = async (payoutId: string) => {
    const processor = (processorInput[payoutId] ?? '').trim();
    if (!processor) {
      setError('승인자 사번을 입력하세요.');
      return;
    }
    await runAction(payoutId, () => approvePayout(policyNumber, payoutId, { processor }));
  };

  const handlePay = async (payoutId: string) => {
    await runAction(payoutId, () => payPayout(policyNumber, payoutId));
  };

  const handleCancel = async (payoutId: string) => {
    await runAction(payoutId, () => cancelPayout(policyNumber, payoutId));
  };

  const runAction = async (payoutId: string, action: () => Promise<PayoutResponse>) => {
    setBusyId(payoutId);
    setError(null);
    try {
      onUpdated(await action());
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '처리에 실패했습니다.');
    } finally {
      setBusyId(null);
    }
  };

  if (payouts.length === 0) {
    return (
      <div className="work-panel">
        <div className="panel-header compact">
          <div>
            <h2>제지급금 내역</h2>
            <p>아직 산정된 제지급금이 없습니다.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="work-panel">
      <div className="panel-header compact">
        <div>
          <h2>제지급금 내역 ({payouts.length}건)</h2>
          <p>산정 → 승인 → 지급 흐름. 지급 전까지는 취소 가능.</p>
        </div>
      </div>
      {error && <AlertMessage type="error" message={error} />}
      <div className="payout-table">
        {payouts.map((payout) => (
          <article key={payout.payoutId} className="payout-row">
            <header className="payout-row-header">
              <div>
                <strong>{payout.payoutId}</strong>
                <span className={`status-tag ${payout.payoutStatus.toLowerCase()}`}>
                  {getPayoutStatusLabel(payout.payoutStatus)}
                </span>
              </div>
              <span className="payout-row-sub">
                {getCalculationBasisLabel(payout.calculationBasis)} · {getPaymentTypeLabel(payout.paymentType)}
              </span>
            </header>
            <dl className="detail-grid">
              <div><dt>납입보험료 합계</dt><dd>{payout.paidPremiumAmount.toLocaleString()} 원</dd></div>
              <div><dt>환급률</dt><dd>{(payout.refundRate * 100).toFixed(2)} %</dd></div>
              <div><dt>산정 금액</dt><dd>{payout.calculatedAmount.toLocaleString()} 원</dd></div>
              <div><dt>공제 사유</dt><dd>{payout.deductionItem ?? '-'}</dd></div>
              <div><dt>공제 금액</dt><dd>{payout.deductionAmount.toLocaleString()} 원</dd></div>
              <div><dt>최종 지급액</dt><dd>{payout.finalPaymentAmount.toLocaleString()} 원</dd></div>
              <div><dt>승인자</dt><dd>{payout.processor ?? '-'}</dd></div>
              <div><dt>산정일시</dt><dd>{payout.createdAt}</dd></div>
              <div><dt>승인일시</dt><dd>{payout.approvedAt ?? '-'}</dd></div>
              <div><dt>지급일시</dt><dd>{payout.paidAt ?? '-'}</dd></div>
              <div><dt>취소일시</dt><dd>{payout.cancelledAt ?? '-'}</dd></div>
            </dl>
            <footer className="payout-row-actions">
              {payout.payoutStatus === 'CALCULATED' && (
                <>
                  <input
                    aria-label="승인자 사번"
                    placeholder="승인자 사번"
                    value={processorInput[payout.payoutId] ?? ''}
                    onChange={(event) =>
                      setProcessorInput((current) => ({
                        ...current,
                        [payout.payoutId]: event.target.value
                      }))
                    }
                    disabled={busyId === payout.payoutId}
                  />
                  <button
                    className="button primary"
                    type="button"
                    onClick={() => handleApprove(payout.payoutId)}
                    disabled={busyId === payout.payoutId}
                  >
                    승인
                  </button>
                </>
              )}
              {payout.payoutStatus === 'APPROVED' && (
                <button
                  className="button primary"
                  type="button"
                  onClick={() => handlePay(payout.payoutId)}
                  disabled={busyId === payout.payoutId}
                >
                  지급 실행
                </button>
              )}
              {(payout.payoutStatus === 'CALCULATED' || payout.payoutStatus === 'APPROVED') && (
                <button
                  className="button"
                  type="button"
                  onClick={() => handleCancel(payout.payoutId)}
                  disabled={busyId === payout.payoutId}
                >
                  취소
                </button>
              )}
            </footer>
          </article>
        ))}
      </div>
    </div>
  );
}
