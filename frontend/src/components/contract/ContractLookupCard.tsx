import { KeyboardEvent, useState } from 'react';
import {
  BadgeDollarSign,
  CalendarDays,
  CircleAlert,
  CircleCheck,
  CreditCard,
  FileText,
  Phone,
  Search,
  ShieldCheck,
  ListChecks,
  UserRound,
  WalletCards
} from 'lucide-react';
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
    <section className="search-section contract-lookup-section">
      <div className="work-panel search-panel contract-search-panel">
        <div className="panel-header compact contract-panel-heading">
          <div className="contract-panel-heading-content">
            <span className="contract-heading-icon" aria-hidden="true">
              <Search size={20} />
            </span>
            <div>
              <h2>{title}</h2>
              <p>{description}</p>
            </div>
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
        <div className="work-panel contract-summary-panel">
          <div className="contract-summary-header">
            <div className="contract-summary-identity">
              <span className="contract-summary-icon" aria-hidden="true">
                <FileText size={22} />
              </span>
              <div>
                <span className="contract-summary-eyebrow">조회된 계약</span>
                <h3>{contract.policyNumber}</h3>
                <p>{contract.productCode}</p>
              </div>
            </div>
            <span className={`status-tag contract-status ${contract.contractStatus.toLowerCase()}`}>
              {getContractStatusLabel(contract.contractStatus)}
            </span>
          </div>
          <dl className="contract-summary-grid">
            <div className="contract-summary-item">
              <ShieldCheck aria-hidden="true" size={19} />
              <div>
                <dt>상품코드</dt>
                <dd className="mono">{contract.productCode}</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <WalletCards aria-hidden="true" size={19} />
              <div>
                <dt>납입 정보</dt>
                <dd>{getPaymentCycleLabel(contract.paymentCycle)} · {contract.premiumAmount.toLocaleString()}원</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <BadgeDollarSign aria-hidden="true" size={19} />
              <div>
                <dt>보험가입금액</dt>
                <dd>{contract.insuredAmount == null ? '-' : `${contract.insuredAmount.toLocaleString()}원`}</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <ListChecks aria-hidden="true" size={19} />
              <div>
                <dt>특약 정보</dt>
                <dd title={contract.specialContractList ?? undefined}>{contract.specialContractList ?? '-'}</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <CalendarDays aria-hidden="true" size={19} />
              <div>
                <dt>계약 기간</dt>
                <dd>{contract.contractStartDate} ~ {contract.contractEndDate}</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <UserRound aria-hidden="true" size={19} />
              <div>
                <dt>피보험자</dt>
                <dd>{contract.insuredName} · {contract.insuredRrn}</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <Phone aria-hidden="true" size={19} />
              <div>
                <dt>연락처</dt>
                <dd>{contract.insuredContact}</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <CreditCard aria-hidden="true" size={19} />
              <div>
                <dt>자동이체 계좌</dt>
                <dd>{contract.accountBank ?? '-'} {contract.accountNumber ?? ''}</dd>
              </div>
            </div>
            <div className="contract-summary-item">
              <WalletCards aria-hidden="true" size={19} />
              <div>
                <dt>만기환급금</dt>
                <dd>{contract.maturityRefundAmount.toLocaleString()}원</dd>
              </div>
            </div>
            <div className={`contract-summary-item ${contract.hasUnpaidPremium ? 'warning' : 'success'}`}>
              {contract.hasUnpaidPremium
                ? <CircleAlert aria-hidden="true" size={19} />
                : <CircleCheck aria-hidden="true" size={19} />}
              <div>
                <dt>미납 보험료</dt>
                <dd>{contract.hasUnpaidPremium ? '미납 내역 있음' : '미납 내역 없음'}</dd>
              </div>
            </div>
          </dl>
        </div>
      )}
    </section>
  );
}
