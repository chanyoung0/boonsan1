import { useEffect, useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/contract/AlertMessage';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';
import { PaymentCollectionTable } from '../../components/contract/PaymentCollectionTable';
import {
  listPaymentCollections,
  listPaymentCollectionTargets,
  listPaymentCollectionTransferTargets,
  processPaymentCollectionBatch
} from '../../api/contractApi';
import type {
  ContractResponse,
  PaymentCollectionBatchResponse,
  PaymentCollectionResponse,
  PaymentCollectionTargetResponse,
  PaymentCollectionTransferTargetResponse
} from '../../types/contract';

export function PaymentCollectionPage() {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [collections, setCollections] = useState<PaymentCollectionResponse[]>([]);
  const [targets, setTargets] = useState<PaymentCollectionTargetResponse[]>([]);
  const [transferTargets, setTransferTargets] = useState<PaymentCollectionTransferTargetResponse[]>([]);
  const [selectedPolicyNumbers, setSelectedPolicyNumbers] = useState<string[]>([]);
  const [batchResult, setBatchResult] = useState<PaymentCollectionBatchResponse | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);

  const loadTargets = async () => {
    setIsLoading(true);
    setLoadError(null);
    try {
      const [targetResult, transferTargetResult] = await Promise.all([
        listPaymentCollectionTargets(),
        listPaymentCollectionTransferTargets()
      ]);
      setTargets(targetResult);
      setTransferTargets(transferTargetResult);
      setSelectedPolicyNumbers((current) =>
        current.filter((policyNumber) => targetResult.some((target) => target.policyNumber === policyNumber))
      );
    } catch (caught) {
      setLoadError(caught instanceof Error ? caught.message : '수금 대상 목록을 불러오지 못했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadTargets();
  }, []);

  useEffect(() => {
    if (!contract) {
      setCollections([]);
      return;
    }
    let cancelled = false;
    listPaymentCollections(contract.policyNumber)
      .then((result) => {
        if (!cancelled) setCollections(result);
      })
      .catch((caught) => {
        if (!cancelled) {
          setLoadError(caught instanceof Error ? caught.message : '수금 내역을 불러오지 못했습니다.');
        }
      });
    return () => {
      cancelled = true;
    };
  }, [contract]);

  const toggleTarget = (policyNumber: string) => {
    setSelectedPolicyNumbers((current) =>
      current.includes(policyNumber)
        ? current.filter((item) => item !== policyNumber)
        : [...current, policyNumber]
    );
  };

  const toggleAllTargets = () => {
    setSelectedPolicyNumbers((current) =>
      current.length === targets.length ? [] : targets.map((target) => target.policyNumber)
    );
  };

  const handleBatchProcess = async () => {
    if (selectedPolicyNumbers.length === 0) return;
    setIsProcessing(true);
    setLoadError(null);
    setBatchResult(null);
    try {
      const result = await processPaymentCollectionBatch({ policyNumbers: selectedPolicyNumbers });
      setBatchResult(result);
      await loadTargets();
      if (contract) {
        setCollections(await listPaymentCollections(contract.policyNumber));
      }
    } catch (caught) {
      setLoadError(caught instanceof Error ? caught.message : '일괄 수금 처리에 실패했습니다.');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleContractLoaded = (loaded: ContractResponse) => {
    setContract(loaded);
  };

  const handleContractCleared = () => {
    setContract(null);
    setCollections([]);
  };

  const handleCollectionUpdated = (updated: PaymentCollectionResponse) => {
    setCollections((current) =>
      current.map((collection) =>
        collection.collectionId === updated.collectionId ? updated : collection
      )
    );
    loadTargets();
  };

  return (
    <AppLayout activeMenuId="contract-installment">
      <div className="page-stack contract-page">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>분납/수금 관리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>분납/수금 관리</h1>
              <p>납입기일이 도래한 계약을 추출하고 자동이체 Mock 결과와 미납 이관 대상을 관리합니다.</p>
            </div>
            <span className="page-kicker">계약 관리 · 관리자 페이지</span>
          </div>
        </header>

        <section className="work-panel contract-list-panel">
          <div className="panel-header compact">
            <div>
              <h2>수금 대상 계약 목록</h2>
              <p>납입기일이 도래한 계약을 선택해 일괄 자동이체 처리합니다.</p>
            </div>
            <div className="form-actions compact-actions">
              <button className="button" type="button" onClick={loadTargets} disabled={isLoading}>
                {isLoading ? '조회 중...' : '목록 새로고침'}
              </button>
              <button
                className="button primary"
                type="button"
                onClick={handleBatchProcess}
                disabled={isProcessing || selectedPolicyNumbers.length === 0}
              >
                {isProcessing ? '처리 중...' : `선택 수금 실행 (${selectedPolicyNumbers.length})`}
              </button>
            </div>
          </div>
          {loadError && <AlertMessage type="error" message={loadError} />}
          {batchResult && (
            <AlertMessage
              type="success"
              message={`일괄 수금 ${batchResult.targetCount}건 처리: 성공 ${batchResult.successCount}건, 실패 ${batchResult.failureCount}건, 수금액 ${batchResult.totalCollectedAmount.toLocaleString()}원`}
            />
          )}
          <div className="contract-table-wrap">
            <table className="contract-data-table">
              <thead>
                <tr>
                  <th>
                    <input
                      type="checkbox"
                      aria-label="수금 대상 전체 선택"
                      checked={targets.length > 0 && selectedPolicyNumbers.length === targets.length}
                      onChange={toggleAllTargets}
                    />
                  </th>
                  <th>증권번호</th>
                  <th>피보험자</th>
                  <th>회차</th>
                  <th>납기일</th>
                  <th>보험료</th>
                  <th>자동이체 계좌</th>
                </tr>
              </thead>
              <tbody>
                {targets.length === 0 ? (
                  <tr>
                    <td colSpan={7}>현재 수금 대상 계약이 없습니다.</td>
                  </tr>
                ) : targets.map((target) => (
                  <tr key={target.policyNumber}>
                    <td>
                      <input
                        type="checkbox"
                        aria-label={`${target.policyNumber} 선택`}
                        checked={selectedPolicyNumbers.includes(target.policyNumber)}
                        onChange={() => toggleTarget(target.policyNumber)}
                      />
                    </td>
                    <td className="mono">{target.policyNumber}</td>
                    <td>{target.insuredName}</td>
                    <td>{target.installmentNo}회차</td>
                    <td>{target.dueDate}</td>
                    <td>{target.plannedAmount.toLocaleString()}원</td>
                    <td>{target.accountBank ?? '-'} {target.accountNumber ?? ''}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="notice-box contract-inline-notice">
            <strong>자동이체 Mock 기준</strong>
            <p>계좌번호 또는 증권번호에 따라 항상 같은 성공/실패 결과를 반환합니다. 실제 은행 연동은 하지 않습니다.</p>
          </div>
        </section>

        <section className="work-panel contract-list-panel">
          <div className="panel-header compact">
            <div>
              <h2>미납 지속 이관 대상</h2>
              <p>최신 수금 결과 기준 미납회차가 2회 이상인 계약입니다.</p>
            </div>
          </div>
          <div className="contract-table-wrap">
            <table className="contract-data-table">
              <thead>
                <tr>
                  <th>증권번호</th>
                  <th>피보험자</th>
                  <th>미납회차</th>
                  <th>미납금액</th>
                </tr>
              </thead>
              <tbody>
                {transferTargets.length === 0 ? (
                  <tr>
                    <td colSpan={4}>현재 이관 대상 계약이 없습니다.</td>
                  </tr>
                ) : transferTargets.map((target) => (
                  <tr key={target.policyNumber}>
                    <td className="mono">{target.policyNumber}</td>
                    <td>{target.insuredName}</td>
                    <td>{target.unpaidInstallmentCount}회</td>
                    <td>{target.unpaidAmount.toLocaleString()}원</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <ContractLookupCard
          title="계약별 수금 이력 조회"
          description="특정 계약의 수금 결과와 미납 안내, 이관 처리 이력을 확인합니다."
          placeholder="POL-2024-000001"
          onContractLoaded={handleContractLoaded}
          onCleared={handleContractCleared}
        />

        {contract && (
          <PaymentCollectionTable
            policyNumber={contract.policyNumber}
            collections={collections}
            onUpdated={handleCollectionUpdated}
          />
        )}
      </div>
    </AppLayout>
  );
}
