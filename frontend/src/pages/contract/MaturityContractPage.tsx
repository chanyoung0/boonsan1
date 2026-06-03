import { useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/contract/AlertMessage';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';
import { getMaturityNotice, processMaturity } from '../../api/contractApi';
import {
  getContractStatusLabel,
  type ContractResponse,
  type MaturityNoticeResponse,
  type MaturityProcessResponse
} from '../../types/contract';

export function MaturityContractPage() {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [notice, setNotice] = useState<MaturityNoticeResponse | null>(null);
  const [processResult, setProcessResult] = useState<MaturityProcessResponse | null>(null);
  const [noticeError, setNoticeError] = useState<string | null>(null);
  const [processError, setProcessError] = useState<string | null>(null);
  const [isLoadingNotice, setIsLoadingNotice] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);

  const handleContractLoaded = (loaded: ContractResponse) => {
    setContract(loaded);
    setNotice(null);
    setProcessResult(null);
    setNoticeError(null);
    setProcessError(null);
  };

  const handleContractCleared = () => {
    setContract(null);
    setNotice(null);
    setProcessResult(null);
    setNoticeError(null);
    setProcessError(null);
  };

  const handleLoadNotice = async () => {
    if (!contract) return;
    setIsLoadingNotice(true);
    setNoticeError(null);
    try {
      setNotice(await getMaturityNotice(contract.policyNumber));
    } catch (caught) {
      setNoticeError(caught instanceof Error ? caught.message : '만기안내를 불러오지 못했습니다.');
    } finally {
      setIsLoadingNotice(false);
    }
  };

  const handleProcess = async () => {
    if (!contract) return;
    setIsProcessing(true);
    setProcessError(null);
    try {
      setProcessResult(await processMaturity(contract.policyNumber));
    } catch (caught) {
      setProcessError(caught instanceof Error ? caught.message : '만기 처리에 실패했습니다.');
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <AppLayout activeMenuId="contract-maturity">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>만기계약 관리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>만기계약 관리</h1>
              <p>증권번호로 계약을 조회하고, 만기안내 발송 및 만기 처리(ACTIVE → EXPIRED)를 수행합니다.</p>
            </div>
            <span className="page-kicker">계약 관리 · 관리자 페이지</span>
          </div>
        </header>

        <ContractLookupCard
          title="계약 조회"
          description="만기 처리를 진행할 계약의 증권번호를 입력하세요."
          placeholder="POL-2023-000099"
          onContractLoaded={handleContractLoaded}
          onCleared={handleContractCleared}
        />

        {contract && (
          <section className="content-grid">
            <div className="work-panel">
              <div className="panel-header compact">
                <div>
                  <h2>만기안내</h2>
                  <p>피보험자에게 발송할 만기안내 메시지를 미리 확인합니다. (실제 발송 없음)</p>
                </div>
                <button
                  className="button"
                  type="button"
                  onClick={handleLoadNotice}
                  disabled={isLoadingNotice}
                >
                  {isLoadingNotice ? '조회 중...' : '만기안내 조회'}
                </button>
              </div>
              {noticeError && <AlertMessage type="error" message={noticeError} />}
              {notice && (
                <dl className="detail-grid">
                  <div><dt>증권번호</dt><dd>{notice.policyNumber}</dd></div>
                  <div><dt>피보험자</dt><dd>{notice.insuredName}</dd></div>
                  <div><dt>연락처</dt><dd>{notice.insuredContact}</dd></div>
                  <div><dt>만기일</dt><dd>{notice.contractEndDate}</dd></div>
                  <div><dt>잔여일수</dt><dd>{notice.daysUntilMaturity}일</dd></div>
                  <div><dt>발송 수단</dt><dd>{notice.deliveryMethod}</dd></div>
                  <div className="full-row"><dt>안내문</dt><dd>{notice.noticeMessage}</dd></div>
                </dl>
              )}
            </div>

            <div className="side-stack">
              <div className="work-panel">
                <div className="panel-header compact">
                  <div>
                    <h2>만기 처리</h2>
                    <p>계약 상태를 ACTIVE → EXPIRED로 전환합니다. 만기일 도래 후에만 실행 가능합니다.</p>
                  </div>
                </div>
                <button
                  className="button primary"
                  type="button"
                  onClick={handleProcess}
                  disabled={isProcessing || contract.contractStatus !== 'ACTIVE'}
                >
                  {isProcessing ? '처리 중...' : '만기 처리 실행'}
                </button>
                {contract.contractStatus !== 'ACTIVE' && (
                  <AlertMessage
                    type="error"
                    message={`현재 상태(${getContractStatusLabel(contract.contractStatus)})에서는 만기 처리할 수 없습니다.`}
                  />
                )}
                {processError && <AlertMessage type="error" message={processError} />}
                {processResult && (
                  <>
                    <AlertMessage type="success" message={processResult.message} />
                    <dl className="detail-grid">
                      <div><dt>증권번호</dt><dd>{processResult.policyNumber}</dd></div>
                      <div><dt>이전 상태</dt><dd>{getContractStatusLabel(processResult.previousStatus)}</dd></div>
                      <div><dt>현재 상태</dt><dd>{getContractStatusLabel(processResult.contractStatus)}</dd></div>
                      <div><dt>만기일</dt><dd>{processResult.contractEndDate}</dd></div>
                      <div><dt>처리일시</dt><dd>{processResult.processedAt}</dd></div>
                    </dl>
                  </>
                )}
              </div>
            </div>
          </section>
        )}
      </div>
    </AppLayout>
  );
}
