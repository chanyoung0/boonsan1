import { useEffect, useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/contract/AlertMessage';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';
import { PaymentCollectionForm } from '../../components/contract/PaymentCollectionForm';
import { PaymentCollectionTable } from '../../components/contract/PaymentCollectionTable';
import { listPaymentCollections } from '../../api/contractApi';
import type { ContractResponse, PaymentCollectionResponse } from '../../types/contract';

export function PaymentCollectionPage() {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [collections, setCollections] = useState<PaymentCollectionResponse[]>([]);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!contract) {
      setCollections([]);
      return;
    }
    let cancelled = false;
    setIsLoading(true);
    setLoadError(null);
    listPaymentCollections(contract.policyNumber)
      .then((result) => {
        if (!cancelled) setCollections(result);
      })
      .catch((caught) => {
        if (!cancelled) {
          setLoadError(caught instanceof Error ? caught.message : '수금 내역을 불러오지 못했습니다.');
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
    setCollections([]);
    setLoadError(null);
  };

  const handleCollectionCreated = (created: PaymentCollectionResponse) => {
    setCollections((current) => [created, ...current]);
  };

  const handleCollectionUpdated = (updated: PaymentCollectionResponse) => {
    setCollections((current) =>
      current.map((collection) =>
        collection.collectionId === updated.collectionId ? updated : collection
      )
    );
  };

  return (
    <AppLayout activeMenuId="contract-installment">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>분납/수금 관리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>분납/수금 관리</h1>
              <p>회차별로 수금 처리합니다. 미수금이 발생하면 연체료(미납금액 × 5%) 자동 산정, 미납안내·이관 가능.</p>
            </div>
            <span className="page-kicker">계약 관리 · 관리자 페이지</span>
          </div>
        </header>

        <ContractLookupCard
          title="계약 조회"
          description="수금 처리할 계약의 증권번호를 입력하세요."
          placeholder="POL-2024-000001"
          onContractLoaded={handleContractLoaded}
          onCleared={handleContractCleared}
        />

        {contract && (
          <>
            <PaymentCollectionForm
              policyNumber={contract.policyNumber}
              onCreated={handleCollectionCreated}
            />
            {loadError && <AlertMessage type="error" message={loadError} />}
            {isLoading ? (
              <div className="work-panel">수금 내역을 불러오는 중...</div>
            ) : (
              <PaymentCollectionTable
                policyNumber={contract.policyNumber}
                collections={collections}
                onUpdated={handleCollectionUpdated}
              />
            )}
          </>
        )}
      </div>
    </AppLayout>
  );
}
