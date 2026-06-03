import { useState } from 'react';
import { getUnpaidNotice, transferPaymentCollection } from '../../api/contractApi';
import {
  TRANSFER_TYPE_LABELS,
  getPaymentMethodLabel,
  getProcessingResultLabel,
  getTransferTypeLabel,
  type PaymentCollectionResponse,
  type TransferType,
  type UnpaidNoticeResponse
} from '../../types/contract';
import { AlertMessage } from './AlertMessage';

interface PaymentCollectionTableProps {
  policyNumber: string;
  collections: PaymentCollectionResponse[];
  onUpdated: (collection: PaymentCollectionResponse) => void;
}

const TRANSFER_TYPE_OPTIONS: TransferType[] = ['VISIT_COLLECTION', 'CANCELLATION', 'DEPARTMENT_CHANGE'];

export function PaymentCollectionTable({ policyNumber, collections, onUpdated }: PaymentCollectionTableProps) {
  const [transferTypeInput, setTransferTypeInput] = useState<Record<string, TransferType>>({});
  const [noticeMap, setNoticeMap] = useState<Record<string, UnpaidNoticeResponse>>({});
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleNotice = async (collectionId: string) => {
    setBusyId(collectionId);
    setError(null);
    try {
      const notice = await getUnpaidNotice(policyNumber, collectionId);
      setNoticeMap((current) => ({ ...current, [collectionId]: notice }));
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '미납안내 조회에 실패했습니다.');
    } finally {
      setBusyId(null);
    }
  };

  const handleTransfer = async (collectionId: string) => {
    const transferType = transferTypeInput[collectionId] ?? 'VISIT_COLLECTION';
    setBusyId(collectionId);
    setError(null);
    try {
      onUpdated(await transferPaymentCollection(policyNumber, collectionId, { transferType }));
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '이관 처리에 실패했습니다.');
    } finally {
      setBusyId(null);
    }
  };

  if (collections.length === 0) {
    return (
      <div className="work-panel">
        <div className="panel-header compact">
          <div>
            <h2>수금 내역</h2>
            <p>아직 처리된 수금 내역이 없습니다.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="work-panel">
      <div className="panel-header compact">
        <div>
          <h2>수금 내역 ({collections.length}건)</h2>
          <p>미수금(FAILED)은 미납안내 조회 및 이관 가능. 이관은 1회만 허용.</p>
        </div>
      </div>
      {error && <AlertMessage type="error" message={error} />}
      <div className="payout-table">
        {collections.map((collection) => {
          const notice = noticeMap[collection.collectionId];
          const isFailed = collection.processingResult === 'FAILED';
          const alreadyTransferred = !!collection.transferType;
          return (
            <article key={collection.collectionId} className="payout-row">
              <header className="payout-row-header">
                <div>
                  <strong>{collection.collectionId}</strong>
                  <span className={`status-tag ${collection.processingResult.toLowerCase()}`}>
                    {getProcessingResultLabel(collection.processingResult)}
                  </span>
                </div>
                <span className="payout-row-sub">
                  {collection.installmentNo}회차 · 납기 {collection.dueDate} · {getPaymentMethodLabel(collection.paymentMethod)}
                </span>
              </header>
              <dl className="detail-grid">
                <div><dt>보험료</dt><dd>{collection.plannedAmount.toLocaleString()} 원</dd></div>
                <div><dt>수금 금액</dt><dd>{collection.collectedAmount.toLocaleString()} 원</dd></div>
                <div><dt>미수금</dt><dd>{collection.unpaidAmount.toLocaleString()} 원</dd></div>
                <div><dt>연체료</dt><dd>{collection.lateFee.toLocaleString()} 원</dd></div>
                <div><dt>수금일시</dt><dd>{collection.collectedAt}</dd></div>
                <div><dt>이관 유형</dt><dd>{collection.transferType ? getTransferTypeLabel(collection.transferType) : '-'}</dd></div>
                <div><dt>이관일시</dt><dd>{collection.transferredAt ?? '-'}</dd></div>
              </dl>

              {isFailed && (
                <footer className="payout-row-actions">
                  <button
                    className="button"
                    type="button"
                    onClick={() => handleNotice(collection.collectionId)}
                    disabled={busyId === collection.collectionId}
                  >
                    미납안내 조회
                  </button>
                  {!alreadyTransferred && (
                    <>
                      <select
                        value={transferTypeInput[collection.collectionId] ?? 'VISIT_COLLECTION'}
                        onChange={(event) =>
                          setTransferTypeInput((current) => ({
                            ...current,
                            [collection.collectionId]: event.target.value as TransferType
                          }))
                        }
                        disabled={busyId === collection.collectionId}
                      >
                        {TRANSFER_TYPE_OPTIONS.map((type) => (
                          <option key={type} value={type}>
                            {TRANSFER_TYPE_LABELS[type]}
                          </option>
                        ))}
                      </select>
                      <button
                        className="button primary"
                        type="button"
                        onClick={() => handleTransfer(collection.collectionId)}
                        disabled={busyId === collection.collectionId}
                      >
                        이관 처리
                      </button>
                    </>
                  )}
                </footer>
              )}

              {notice && (
                <div className="notice-box">
                  <strong>미납안내</strong>
                  <p>{notice.noticeMessage}</p>
                  <small>
                    경과일수 {notice.daysOverdue}일 · 총 {notice.totalAmountDue.toLocaleString()}원 ·
                    발송수단 {notice.deliveryMethod} · 수신자 {notice.insuredName} ({notice.insuredContact})
                  </small>
                </div>
              )}
            </article>
          );
        })}
      </div>
    </div>
  );
}
