import { useEffect, useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/contract/AlertMessage';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';
import {
  getMaturityNotice,
  listMaturityRenewalTargets,
  listMaturityTargets,
  processMaturity,
  recordMaturityRenewalIntention,
  sendMaturityNotice
} from '../../api/contractApi';
import {
  getContractStatusLabel,
  type ContractResponse,
  type MaturityNoticeResponse,
  type MaturityTargetResponse
} from '../../types/contract';

export function MaturityContractPage() {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [notice, setNotice] = useState<MaturityNoticeResponse | null>(null);
  const [targets, setTargets] = useState<MaturityTargetResponse[]>([]);
  const [renewalTargets, setRenewalTargets] = useState<MaturityTargetResponse[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [busyKey, setBusyKey] = useState<string | null>(null);

  const loadLists = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const [maturityResult, renewalResult] = await Promise.all([
        listMaturityTargets(),
        listMaturityRenewalTargets()
      ]);
      setTargets(maturityResult);
      setRenewalTargets(renewalResult);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '만기 대상 목록을 불러오지 못했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadLists();
  }, []);

  const runAction = async (key: string, action: () => Promise<unknown>, message: string) => {
    setBusyKey(key);
    setError(null);
    setSuccessMessage(null);
    try {
      await action();
      setSuccessMessage(message);
      await loadLists();
      if (contract) {
        setNotice(await getMaturityNotice(contract.policyNumber));
      }
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '만기 처리에 실패했습니다.');
    } finally {
      setBusyKey(null);
    }
  };

  const handleContractLoaded = async (loaded: ContractResponse) => {
    setContract(loaded);
    setNotice(null);
    setError(null);
    try {
      setNotice(await getMaturityNotice(loaded.policyNumber));
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '만기안내를 불러오지 못했습니다.');
    }
  };

  const handleContractCleared = () => {
    setContract(null);
    setNotice(null);
    setError(null);
  };

  return (
    <AppLayout activeMenuId="contract-maturity">
      <div className="page-stack contract-page">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>만기계약 관리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>만기계약 관리</h1>
              <p>만기 도래 또는 30일 이내 도래 예정 계약을 추출하고 안내 발송과 재계약 의사를 관리합니다.</p>
            </div>
            <span className="page-kicker">계약 관리 · 관리자 페이지</span>
          </div>
        </header>

        {error && <AlertMessage type="error" message={error} />}
        {successMessage && <AlertMessage type="success" message={successMessage} />}

        <section className="work-panel contract-list-panel">
          <div className="panel-header compact">
            <div>
              <h2>만기 대상 계약 목록</h2>
              <p>만기일이 도래했거나 30일 이내 도래 예정인 계약입니다.</p>
            </div>
            <button className="button" type="button" onClick={loadLists} disabled={isLoading}>
              {isLoading ? '조회 중...' : '목록 새로고침'}
            </button>
          </div>
          <div className="contract-table-wrap">
            <table className="contract-data-table">
              <thead>
                <tr>
                  <th>구분</th>
                  <th>증권번호</th>
                  <th>피보험자</th>
                  <th>만기일</th>
                  <th>잔여일수</th>
                  <th>만기환급금</th>
                  <th>안내 발송</th>
                  <th>처리</th>
                </tr>
              </thead>
              <tbody>
                {targets.length === 0 ? (
                  <tr>
                    <td colSpan={8}>현재 만기 대상 계약이 없습니다.</td>
                  </tr>
                ) : targets.map((target) => (
                  <tr key={target.policyNumber}>
                    <td>
                      <span className={`status-tag ${target.maturityTiming === 'DUE' ? 'warning' : 'active'}`}>
                        {target.maturityTiming === 'DUE' ? '만기 도래' : '만기 예정'}
                      </span>
                    </td>
                    <td className="mono">{target.policyNumber}</td>
                    <td>{target.insuredName}</td>
                    <td>{target.contractEndDate}</td>
                    <td>{target.daysUntilMaturity}일</td>
                    <td>{target.maturityRefundAmount.toLocaleString()}원</td>
                    <td>{target.noticeSentAt ?? '미발송'}</td>
                    <td>
                      <div className="contract-table-actions">
                        <button
                          className="button"
                          type="button"
                          onClick={() =>
                            runAction(
                              `notice-${target.policyNumber}`,
                              () => sendMaturityNotice(target.policyNumber),
                              `${target.policyNumber} 만기 안내를 저장했습니다.`
                            )
                          }
                          disabled={busyKey !== null}
                        >
                          안내 발송
                        </button>
                        {target.maturityTiming === 'DUE' && (
                          <button
                            className="button primary"
                            type="button"
                            onClick={() =>
                              runAction(
                                `maturity-${target.policyNumber}`,
                                () => processMaturity(target.policyNumber),
                                `${target.policyNumber} 계약을 만기 상태로 변경했습니다.`
                              )
                            }
                            disabled={busyKey !== null}
                          >
                            만기 처리
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="work-panel contract-list-panel">
          <div className="panel-header compact">
            <div>
              <h2>재계약 의사 확인 대상</h2>
              <p>만기 안내가 발송되고 만기일이 도래한 계약의 재계약 의사를 기록합니다.</p>
            </div>
          </div>
          <div className="contract-table-wrap">
            <table className="contract-data-table">
              <thead>
                <tr>
                  <th>증권번호</th>
                  <th>피보험자</th>
                  <th>만기일</th>
                  <th>계약상태</th>
                  <th>안내 발송일시</th>
                  <th>재계약 의사</th>
                </tr>
              </thead>
              <tbody>
                {renewalTargets.length === 0 ? (
                  <tr>
                    <td colSpan={6}>현재 재계약 의사 확인 대상이 없습니다.</td>
                  </tr>
                ) : renewalTargets.map((target) => (
                  <tr key={target.policyNumber}>
                    <td className="mono">{target.policyNumber}</td>
                    <td>{target.insuredName}</td>
                    <td>{target.contractEndDate}</td>
                    <td>{getContractStatusLabel(target.contractStatus)}</td>
                    <td>{target.noticeSentAt ?? '-'}</td>
                    <td>
                      <div className="contract-table-actions">
                        <button
                          className="button primary"
                          type="button"
                          onClick={() =>
                            runAction(
                              `renew-yes-${target.policyNumber}`,
                              () => recordMaturityRenewalIntention(target.policyNumber, true),
                              `${target.policyNumber} 재계약 의사를 있음으로 저장했습니다.`
                            )
                          }
                          disabled={busyKey !== null}
                        >
                          있음
                        </button>
                        <button
                          className="button"
                          type="button"
                          onClick={() =>
                            runAction(
                              `renew-no-${target.policyNumber}`,
                              () => recordMaturityRenewalIntention(target.policyNumber, false),
                              `${target.policyNumber} 계약을 만기종료 상태로 변경했습니다.`
                            )
                          }
                          disabled={busyKey !== null}
                        >
                          없음
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <ContractLookupCard
          title="만기계약 개별 조회"
          description="특정 계약의 만기 안내 내용과 만기환급금을 확인합니다."
          placeholder="POL-2023-000099"
          onContractLoaded={handleContractLoaded}
          onCleared={handleContractCleared}
        />

        {contract && notice && (
          <section className="work-panel">
            <div className="panel-header compact">
              <div>
                <h2>만기안내 상세</h2>
                <p>{notice.policyNumber} · {getContractStatusLabel(notice.contractStatus)}</p>
              </div>
            </div>
            <dl className="detail-grid">
              <div><dt>피보험자</dt><dd>{notice.insuredName}</dd></div>
              <div><dt>연락처</dt><dd>{notice.insuredContact}</dd></div>
              <div><dt>만기일</dt><dd>{notice.contractEndDate}</dd></div>
              <div><dt>잔여일수</dt><dd>{notice.daysUntilMaturity}일</dd></div>
              <div><dt>만기환급금</dt><dd>{notice.maturityRefundAmount.toLocaleString()}원</dd></div>
              <div><dt>발송일시</dt><dd>{notice.sentAt ?? '미발송'}</dd></div>
              <div className="full-row"><dt>안내문</dt><dd>{notice.noticeMessage}</dd></div>
            </dl>
          </section>
        )}
      </div>
    </AppLayout>
  );
}
