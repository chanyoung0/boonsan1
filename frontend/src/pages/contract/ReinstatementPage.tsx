import { FormEvent, useEffect, useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/contract/AlertMessage';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';
import { ApiError } from '../../api/apiClient';
import {
  applyReinstatement,
  cancelReinstatement,
  completeReinstatement,
  completeReinstatementUnderwriting,
  getActiveReinstatement,
  getContract,
  getReinstatementUnpaidSummary,
  getReinstatementUnderwriting,
  listReinstatements,
  requestReinstatementUnderwriting,
  settleReinstatementUnpaid
} from '../../api/contractApi';
import {
  REINSTATEMENT_REASON_LABELS,
  REJECTION_REASON_LABELS,
  SURCHARGE_CONDITION_LABELS,
  UNDERWRITING_TYPE_LABELS,
  getContractStatusLabel,
  getReinstatementReasonLabel,
  getReinstatementStatusLabel,
  getRequestStatusLabel,
  getUnderwritingResultLabel,
  type ContractResponse,
  type RejectionReason,
  type ReinstatementReason,
  type ReinstatementResponse,
  type ReinstatementUnpaidSummaryResponse,
  type SurchargeCondition,
  type UnderwritingRequestResponse,
  type UnderwritingResultType,
  type UnderwritingType
} from '../../types/contract';

const REASON_OPTIONS: ReinstatementReason[] = [
  'FINANCIAL_DIFFICULTY',
  'OVERSEAS_ABSENCE',
  'MEDICAL_EMERGENCY',
  'OTHER'
];

const UNDERWRITING_TYPE_OPTIONS: UnderwritingType[] = [
  'AUTO',
  'DIAGNOSIS',
  'SPECIAL',
  'GENERAL',
  'IMAGE',
  'FITNESS'
];

const UW_RESULT_OPTIONS: UnderwritingResultType[] = ['APPROVED', 'SURCHARGE', 'REJECTED'];

const REJECTION_REASON_OPTIONS: RejectionReason[] = [
  'HIGH_RISK',
  'INCOMPLETE_DOCUMENTS',
  'FRAUD_SUSPICION',
  'POLICY_LIMIT'
];

const SURCHARGE_CONDITION_OPTIONS: SurchargeCondition[] = [
  'HIGH_RISK_OCCUPATION',
  'POOR_HEALTH',
  'HAZARDOUS_ACTIVITY',
  'NONE'
];

export function ReinstatementPage() {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [active, setActive] = useState<ReinstatementResponse | null>(null);
  const [activeUw, setActiveUw] = useState<UnderwritingRequestResponse | null>(null);
  const [unpaidSummary, setUnpaidSummary] = useState<ReinstatementUnpaidSummaryResponse | null>(null);
  const [history, setHistory] = useState<ReinstatementResponse[]>([]);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isBusy, setIsBusy] = useState(false);

  const [uwType, setUwType] = useState<UnderwritingType>('GENERAL');
  const [uwResult, setUwResult] = useState<UnderwritingResultType>('APPROVED');
  const [uwRejectionReason, setUwRejectionReason] = useState<RejectionReason>('HIGH_RISK');
  const [uwSurchargeCondition, setUwSurchargeCondition] = useState<SurchargeCondition>('HIGH_RISK_OCCUPATION');

  const [reason, setReason] = useState<ReinstatementReason>('FINANCIAL_DIFFICULTY');
  const [desiredDate, setDesiredDate] = useState('');
  const [hasHealthChanged, setHasHealthChanged] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  const refresh = async (policyNumber: string) => {
    setIsLoading(true);
    setLoadError(null);
    try {
      const [activeResult, historyResult, unpaidSummaryResult] = await Promise.all([
        getActiveReinstatement(policyNumber).catch((caught) => {
          if (caught instanceof ApiError && caught.status === 404) return null;
          throw caught;
        }),
        listReinstatements(policyNumber),
        getReinstatementUnpaidSummary(policyNumber)
      ]);
      setActive(activeResult);
      setHistory(historyResult);
      setUnpaidSummary(unpaidSummaryResult);
      if (activeResult?.underwritingRequestId) {
        try {
          setActiveUw(await getReinstatementUnderwriting(policyNumber));
        } catch {
          setActiveUw(null);
        }
      } else {
        setActiveUw(null);
      }
    } catch (caught) {
      setLoadError(caught instanceof Error ? caught.message : '부활 정보를 불러오지 못했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (contract) {
      refresh(contract.policyNumber);
    } else {
      setActive(null);
      setHistory([]);
      setUnpaidSummary(null);
    }
  }, [contract]);

  const handleContractLoaded = (loaded: ContractResponse) => {
    setContract(loaded);
    setActionError(null);
    setFormError(null);
  };

  const handleContractCleared = () => {
    setContract(null);
    setActive(null);
    setActiveUw(null);
    setUnpaidSummary(null);
    setHistory([]);
    setLoadError(null);
    setActionError(null);
  };

  const handleCompleteUw = async () => {
    if (!contract) return;
    await runAction(() =>
      completeReinstatementUnderwriting(contract.policyNumber, {
        underwritingResult: uwResult,
        rejectionReason: uwResult === 'REJECTED' ? uwRejectionReason : null,
        surchargeCondition: uwResult === 'SURCHARGE' ? uwSurchargeCondition : null
      })
    );
  };

  const handleApply = async (event: FormEvent) => {
    event.preventDefault();
    if (!contract) return;

    if (!desiredDate) {
      setFormError('부활 희망일을 입력하세요.');
      return;
    }
    if (!unpaidSummary || unpaidSummary.unpaidInstallmentCount < 1 || unpaidSummary.unpaidPremium <= 0) {
      setFormError('조회된 미납 보험료가 없어 부활을 신청할 수 없습니다.');
      return;
    }

    setIsBusy(true);
    setFormError(null);
    try {
      await applyReinstatement(contract.policyNumber, {
        reinstatementReason: reason,
        desiredDate,
        hasHealthChanged,
        lastPaidDate: null
      });
      setDesiredDate('');
      setHasHealthChanged(false);
      setReason('FINANCIAL_DIFFICULTY');
      await refresh(contract.policyNumber);
    } catch (caught) {
      setFormError(caught instanceof Error ? caught.message : '부활 신청에 실패했습니다.');
    } finally {
      setIsBusy(false);
    }
  };

  const runAction = async (action: () => Promise<unknown>) => {
    if (!contract) return;
    setIsBusy(true);
    setActionError(null);
    try {
      await action();
      setContract(await getContract(contract.policyNumber));
      await refresh(contract.policyNumber);
    } catch (caught) {
      setActionError(caught instanceof Error ? caught.message : '처리에 실패했습니다.');
    } finally {
      setIsBusy(false);
    }
  };

  const canApply = contract?.contractStatus === 'SUSPENDED' && !active;
  const notSuspended = contract && contract.contractStatus !== 'SUSPENDED';

  return (
    <AppLayout activeMenuId="contract-revival">
      <div className="page-stack contract-page">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>부활 관리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>부활 관리</h1>
              <p>실효된 계약의 미납보험료 정산과 심사 절차를 거쳐 계약 부활을 처리합니다.</p>
            </div>
            <span className="page-kicker">계약 관리 · 관리자 페이지</span>
          </div>
        </header>

        <ContractLookupCard
          title="부활 대상 계약 조회"
          description="부활 신청할 계약의 증권번호를 입력하세요."
          placeholder="POL-2024-000111"
          onContractLoaded={handleContractLoaded}
          onCleared={handleContractCleared}
        />

        {contract && (
          <>
            {notSuspended && (
              <AlertMessage
                type="error"
                message={`현재 계약 상태(${getContractStatusLabel(contract.contractStatus)})는 부활 대상이 아닙니다. 실효 상태의 계약만 신청할 수 있습니다.`}
              />
            )}
            {loadError && <AlertMessage type="error" message={loadError} />}
            {isLoading && <div className="work-panel contract-loading-panel">부활 정보를 불러오는 중...</div>}

            {!isLoading && canApply && (
              <form className="work-panel" onSubmit={handleApply}>
                <div className="panel-header compact">
                  <div>
                    <h2>부활 신청</h2>
                    <p>계약의 미납 내역을 자동 조회한 뒤 부활 사유와 희망일을 입력합니다.</p>
                  </div>
                </div>
                <div className="form-grid">
                  <label>
                    <span>부활 사유</span>
                    <select
                      value={reason}
                      onChange={(event) => setReason(event.target.value as ReinstatementReason)}
                      disabled={isBusy}
                    >
                      {REASON_OPTIONS.map((option) => (
                        <option key={option} value={option}>
                          {REINSTATEMENT_REASON_LABELS[option]}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    <span>부활 희망일</span>
                    <input
                      type="date"
                      value={desiredDate}
                      onChange={(event) => setDesiredDate(event.target.value)}
                      disabled={isBusy}
                      required
                    />
                  </label>
                  <label>
                    <span>최종 납입일</span>
                    <input
                      value={unpaidSummary?.lastPaidDate ?? '-'}
                      readOnly
                    />
                  </label>
                  <label>
                    <span>미납 회차</span>
                    <input
                      value={unpaidSummary ? `${unpaidSummary.unpaidInstallmentCount}회` : '-'}
                      readOnly
                    />
                  </label>
                  <label>
                    <span>회차당 보험료 (원)</span>
                    <input
                      value={unpaidSummary ? unpaidSummary.premiumPerInstallment.toLocaleString() : '-'}
                      readOnly
                    />
                  </label>
                  <label>
                    <span>미납 보험료 합계 (원)</span>
                    <input
                      value={unpaidSummary ? unpaidSummary.unpaidPremium.toLocaleString() : '-'}
                      readOnly
                    />
                  </label>
                  <label>
                    <span>건강 상태 변경 여부</span>
                    <select
                      value={hasHealthChanged ? 'yes' : 'no'}
                      onChange={(event) => setHasHealthChanged(event.target.value === 'yes')}
                      disabled={isBusy}
                    >
                      <option value="no">변경 없음</option>
                      <option value="yes">변경 있음</option>
                    </select>
                  </label>
                </div>
                {formError && <AlertMessage type="error" message={formError} />}
                <div className="form-actions">
                  <button className="button primary" type="submit" disabled={isBusy}>
                    {isBusy ? '신청 중...' : '부활 신청'}
                  </button>
                </div>
              </form>
            )}

            {!isLoading && active && (
              <div className="work-panel">
                <div className="panel-header compact">
                  <div>
                    <h2>진행 중인 부활</h2>
                    <p>{active.reinstatementId} · {getReinstatementStatusLabel(active.reinstatementStatus)}</p>
                  </div>
                </div>
                <dl className="detail-grid">
                  <div><dt>부활 사유</dt><dd>{getReinstatementReasonLabel(active.reinstatementReason)}</dd></div>
                  <div><dt>희망일</dt><dd>{active.desiredDate}</dd></div>
                  <div><dt>최종 납입일</dt><dd>{active.lastPaidDate ?? '-'}</dd></div>
                  <div><dt>건강 변경</dt><dd>{active.hasHealthChanged ? '있음' : '없음'}</dd></div>
                  <div><dt>미납 회차</dt><dd>{active.unpaidInstallmentCount}회</dd></div>
                  <div><dt>회차당 보험료</dt><dd>{active.premiumPerInstallment.toLocaleString()} 원</dd></div>
                  <div><dt>미납보험료</dt><dd><strong>{active.unpaidPremium.toLocaleString()} 원</strong></dd></div>
                  <div><dt>심사요청 ID</dt><dd>{active.underwritingRequestId ?? '-'}</dd></div>
                  <div><dt>신청일시</dt><dd>{active.appliedAt}</dd></div>
                  <div><dt>납부일시</dt><dd>{active.unpaidSettledAt ?? '-'}</dd></div>
                </dl>
                {actionError && <AlertMessage type="error" message={actionError} />}

                {active.reinstatementStatus === 'APPLIED' && !active.underwritingRequestId && (
                  <div className="form-actions">
                    <label>
                      <span>심사 유형</span>
                      <select
                        value={uwType}
                        onChange={(event) => setUwType(event.target.value as UnderwritingType)}
                        disabled={isBusy}
                      >
                        {UNDERWRITING_TYPE_OPTIONS.map((opt) => (
                          <option key={opt} value={opt}>{UNDERWRITING_TYPE_LABELS[opt]}</option>
                        ))}
                      </select>
                    </label>
                    <button
                      className="button primary"
                      type="button"
                      onClick={() =>
                        runAction(() =>
                          requestReinstatementUnderwriting(contract.policyNumber, { underwritingType: uwType })
                        )
                      }
                      disabled={isBusy}
                    >
                      심사 요청
                    </button>
                  </div>
                )}

                {activeUw && (
                  <div className="notice-box">
                    <strong>심사요청 {activeUw.requestId}</strong>
                    <p>
                      상태: {getRequestStatusLabel(activeUw.requestStatus)}
                      {activeUw.underwritingResult && ` · 결과 ${getUnderwritingResultLabel(activeUw.underwritingResult)}`}
                      {activeUw.rejectionReason && ` (${REJECTION_REASON_LABELS[activeUw.rejectionReason]})`}
                      {activeUw.surchargeCondition && ` (${SURCHARGE_CONDITION_LABELS[activeUw.surchargeCondition]})`}
                    </p>
                    {activeUw.requestStatus === 'PENDING' && (
                      <div className="form-actions">
                        <label>
                          <span>심사 결과</span>
                          <select
                            value={uwResult}
                            onChange={(event) => setUwResult(event.target.value as UnderwritingResultType)}
                            disabled={isBusy}
                          >
                            {UW_RESULT_OPTIONS.map((opt) => (
                              <option key={opt} value={opt}>{getUnderwritingResultLabel(opt)}</option>
                            ))}
                          </select>
                        </label>
                        {uwResult === 'REJECTED' && (
                          <label>
                            <span>반려 사유</span>
                            <select
                              value={uwRejectionReason}
                              onChange={(event) => setUwRejectionReason(event.target.value as RejectionReason)}
                              disabled={isBusy}
                            >
                              {REJECTION_REASON_OPTIONS.map((opt) => (
                                <option key={opt} value={opt}>{REJECTION_REASON_LABELS[opt]}</option>
                              ))}
                            </select>
                          </label>
                        )}
                        {uwResult === 'SURCHARGE' && (
                          <label>
                            <span>할증 조건</span>
                            <select
                              value={uwSurchargeCondition}
                              onChange={(event) => setUwSurchargeCondition(event.target.value as SurchargeCondition)}
                              disabled={isBusy}
                            >
                              {SURCHARGE_CONDITION_OPTIONS.map((opt) => (
                                <option key={opt} value={opt}>{SURCHARGE_CONDITION_LABELS[opt]}</option>
                              ))}
                            </select>
                          </label>
                        )}
                        <button
                          className="button primary"
                          type="button"
                          onClick={handleCompleteUw}
                          disabled={isBusy}
                        >
                          심사 완료
                        </button>
                      </div>
                    )}
                  </div>
                )}

                <div className="form-actions">
                  {active.reinstatementStatus === 'APPLIED' && (
                    <button
                      className="button primary"
                      type="button"
                      onClick={() => runAction(() => settleReinstatementUnpaid(contract.policyNumber))}
                      disabled={
                        isBusy ||
                        (!!active.underwritingRequestId && activeUw?.underwritingResult !== 'APPROVED')
                      }
                      title={
                        active.underwritingRequestId && activeUw?.underwritingResult !== 'APPROVED'
                          ? '심사요청이 APPROVED 결과로 완료되어야 합니다.'
                          : undefined
                      }
                    >
                      미납 납부 완료 처리
                    </button>
                  )}
                  {active.reinstatementStatus === 'UNPAID_SETTLED' && (
                    <button
                      className="button primary"
                      type="button"
                      onClick={() => runAction(() => completeReinstatement(contract.policyNumber))}
                      disabled={isBusy}
                    >
                      부활 완료 처리
                    </button>
                  )}
                  <button
                    className="button"
                    type="button"
                    onClick={() => runAction(() => cancelReinstatement(contract.policyNumber))}
                    disabled={isBusy}
                  >
                    취소
                  </button>
                </div>
              </div>
            )}

            {!isLoading && history.length > 0 && (
              <div className="work-panel">
                <div className="panel-header compact">
                  <div>
                    <h2>부활 이력 ({history.length}건)</h2>
                    <p>이 계약에서 처리된 부활 신청 이력을 확인합니다.</p>
                  </div>
                </div>
                <div className="payout-table">
                  {history.map((item) => (
                    <article key={item.reinstatementId} className="payout-row">
                      <header className="payout-row-header">
                        <div>
                          <strong>{item.reinstatementId}</strong>
                          <span className={`status-tag ${item.reinstatementStatus.toLowerCase()}`}>
                            {getReinstatementStatusLabel(item.reinstatementStatus)}
                          </span>
                        </div>
                        <span className="payout-row-sub">
                          {getReinstatementReasonLabel(item.reinstatementReason)} · 신청 {item.appliedAt}
                        </span>
                      </header>
                      <dl className="detail-grid">
                        <div><dt>희망일</dt><dd>{item.desiredDate}</dd></div>
                        <div><dt>미납 회차</dt><dd>{item.unpaidInstallmentCount}회</dd></div>
                        <div><dt>미납보험료</dt><dd>{item.unpaidPremium.toLocaleString()} 원</dd></div>
                        <div><dt>납부일시</dt><dd>{item.unpaidSettledAt ?? '-'}</dd></div>
                        <div><dt>완료일시</dt><dd>{item.completedAt ?? '-'}</dd></div>
                        <div><dt>취소일시</dt><dd>{item.cancelledAt ?? '-'}</dd></div>
                      </dl>
                    </article>
                  ))}
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </AppLayout>
  );
}
