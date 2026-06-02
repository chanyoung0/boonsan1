import { KeyboardEvent, useState } from 'react';
import { Search } from 'lucide-react';
import { getContract } from '../../api/contractApi';
import {
  getContractStatusLabel,
  getPaymentCycleLabel,
  type ContractResponse
} from '../../types/contract';
import { AlertMessage } from './AlertMessage';

interface ContractLookupCardProps {
  title?: string;
  description?: string;
  placeholder?: string;
  onContractLoaded?: (contract: ContractResponse) => void;
  onCleared?: () => void;
}

export function ContractLookupCard({
  title = '계약 조회',
  description = '증권번호로 계약 정보를 조회합니다.',
  placeholder = 'POL-2024-000001',
  onContractLoaded,
  onCleared
}: ContractLookupCardProps) {
  const [policyNumber, setPolicyNumber] = useState('');
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = async () => {
    const normalized = policyNumber.trim();
    if (!normalized) {
      setError('증권번호를 입력하세요.');
      return;
    }

    setIsLoading(true);
    setError(null);
    setContract(null);
    onCleared?.();
    try {
      const result = await getContract(normalized);
      setContract(result);
      onContractLoaded?.(result);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '해당 증권번호를 찾을 수 없습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <section className="search-section">
      <div className="work-panel search-panel">
        <div className="panel-header compact">
          <div>
            <h2>{title}</h2>
            <p>{description}</p>
          </div>
        </div>
        <div className="search-row">
          <input
            aria-label="증권번호"
            value={policyNumber}
            onChange={(event) => setPolicyNumber(event.target.value)}
            onKeyDown={handleKeyDown}
            placeholder={placeholder}
            disabled={isLoading}
          />
          <button className="button primary" type="button" onClick={handleSearch} disabled={isLoading}>
            <Search aria-hidden="true" size={16} />
            {isLoading ? '조회 중...' : '조회'}
          </button>
        </div>
        {error && <AlertMessage type="error" message={error} />}
      </div>

      {contract && (
        <div className="work-panel">
          <div className="panel-header compact">
            <div>
              <h3>계약 정보</h3>
              <p>{contract.policyNumber} · {getContractStatusLabel(contract.contractStatus)}</p>
            </div>
          </div>
          <dl className="detail-grid">
            <div><dt>상품코드</dt><dd>{contract.productCode}</dd></div>
            <div><dt>계약상태</dt><dd>{getContractStatusLabel(contract.contractStatus)}</dd></div>
            <div><dt>납입주기</dt><dd>{getPaymentCycleLabel(contract.paymentCycle)}</dd></div>
            <div><dt>보험료</dt><dd>{contract.premiumAmount.toLocaleString()} 원</dd></div>
            <div><dt>계약 시작일</dt><dd>{contract.contractStartDate}</dd></div>
            <div><dt>계약 만기일</dt><dd>{contract.contractEndDate}</dd></div>
            <div><dt>피보험자</dt><dd>{contract.insuredName}</dd></div>
            <div><dt>주민번호</dt><dd>{contract.insuredRrn}</dd></div>
            <div><dt>연락처</dt><dd>{contract.insuredContact}</dd></div>
            <div><dt>자동이체 계좌</dt><dd>{contract.accountBank ?? '-'} {contract.accountNumber ?? ''}</dd></div>
            <div><dt>미납 보험료</dt><dd>{contract.hasUnpaidPremium ? '있음' : '없음'}</dd></div>
          </dl>
        </div>
      )}
    </section>
  );
}
