import { useEffect, useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/contract/AlertMessage';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';
import { PayoutForm } from '../../components/contract/PayoutForm';
import { PayoutTable } from '../../components/contract/PayoutTable';
import { listPayouts } from '../../api/contractApi';
import type { ContractResponse, PayoutResponse } from '../../types/contract';

export function PayoutPage() {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [payouts, setPayouts] = useState<PayoutResponse[]>([]);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!contract) {
      setPayouts([]);
      return;
    }
    let cancelled = false;
    setIsLoading(true);
    setLoadError(null);
    listPayouts(contract.policyNumber)
      .then((result) => {
        if (!cancelled) setPayouts(result);
      })
      .catch((caught) => {
        if (!cancelled) {
          setLoadError(caught instanceof Error ? caught.message : '제지급금 내역을 불러오지 못했습니다.');
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [contract]);

  const handleContractLoaded = (loaded: ContractResponse) => {
    setContract(loaded);
  };

  const handleContractCleared = () => {
    setContract(null);
    setPayouts([]);
    setLoadError(null);
  };

  const handlePayoutCreated = (created: PayoutResponse) => {
    setPayouts((current) => [created, ...current]);
  };

  const handlePayoutUpdated = (updated: PayoutResponse) => {
    setPayouts((current) =>
      current.map((payout) => (payout.payoutId === updated.payoutId ? updated : payout))
    );
  };

  return (
    <AppLayout activeMenuId="contract-payment">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>제지급금 관리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>제지급금 관리</h1>
              <p>만기환급금/해지환급금/중도해지환급금/배당금을 산정하고 승인·지급합니다. (보험금 지급과 별개)</p>
            </div>
            <span className="page-kicker">계약 관리 · 관리자 페이지</span>
          </div>
        </header>

        <ContractLookupCard
          title="계약 조회"
          description="제지급금을 산정할 계약의 증권번호를 입력하세요."
          placeholder="POL-2024-000001"
          onContractLoaded={handleContractLoaded}
          onCleared={handleContractCleared}
        />

        {contract && (
          <>
            <PayoutForm policyNumber={contract.policyNumber} onCreated={handlePayoutCreated} />
            {loadError && <AlertMessage type="error" message={loadError} />}
            {isLoading ? (
              <div className="work-panel">제지급금 내역을 불러오는 중...</div>
            ) : (
              <PayoutTable
                policyNumber={contract.policyNumber}
                payouts={payouts}
                onUpdated={handlePayoutUpdated}
              />
            )}
          </>
        )}
      </div>
    </AppLayout>
  );
}
