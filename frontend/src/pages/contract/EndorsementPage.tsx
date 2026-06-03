import { FormEvent, useEffect, useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AlertMessage } from '../../components/contract/AlertMessage';
import { ContractLookupCard } from '../../components/contract/ContractLookupCard';
import { ApiError } from '../../api/apiClient';
import {
  applyEndorsement,
  approveEndorsement,
  cancelEndorsement,
  completeEndorsementUnderwriting,
  getActiveEndorsement,
  getContract,
  getEndorsementUnderwriting,
  listEndorsements,
  rejectEndorsement,
  requestEndorsementUnderwriting
} from '../../api/contractApi';
import {
  CHANGE_REASON_LABELS,
  ENDORSEMENT_TYPE_LABELS,
  REJECTION_REASON_LABELS,
  SURCHARGE_CONDITION_LABELS,
  UNDERWRITING_TYPE_LABELS,
  getChangeReasonLabel,
  getEndorsementStatusLabel,
  getEndorsementTypeLabel,
  getRequestStatusLabel,
  getUnderwritingResultLabel,
  type ChangeReason,
  type ContractResponse,
  type EndorsementResponse,
  type EndorsementType,
  type RejectionReason,
  type SurchargeCondition,
  type UnderwritingRequestResponse,
  type UnderwritingResultType,
  type UnderwritingType
} from '../../types/contract';

const ENDORSEMENT_TYPE_OPTIONS: EndorsementType[] = [
  'COVERAGE_CHANGE',
  'BENEFICIARY_CHANGE',
  'PREMIUM_CHANGE',
  'SPECIAL_CONTRACT_CHANGE'
];

const CHANGE_REASON_OPTIONS: ChangeReason[] = [
  'INSURED_AMOUNT_CHANGE',
  'PAYMENT_CYCLE_CHANGE',
  'SPECIAL_CONTRACT_ADD',
  'SPECIAL_CONTRACT_REMOVE',
  'BENEFICIARY_CHANGE'
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

function requiresEndorsementUnderwriting(changeReason: ChangeReason) {
  return changeReason === 'INSURED_AMOUNT_CHANGE'
    || changeReason === 'SPECIAL_CONTRACT_ADD'
    || changeReason === 'SPECIAL_CONTRACT_REMOVE';
}

export function EndorsementPage() {
  const [contract, setContract] = useState<ContractResponse | null>(null);
  const [active, setActive] = useState<EndorsementResponse | null>(null);
  const [activeUw, setActiveUw] = useState<UnderwritingRequestResponse | null>(null);
  const [history, setHistory] = useState<EndorsementResponse[]>([]);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isBusy, setIsBusy] = useState(false);

  // Apply form state
  const [endorsementType, setEndorsementType] = useState<EndorsementType>('PREMIUM_CHANGE');
  const [changeReason, setChangeReason] = useState<ChangeReason>('INSURED_AMOUNT_CHANGE');
  const [previousContent, setPreviousContent] = useState('');
  const [newContent, setNewContent] = useState('');
  const [formError, setFormError] = useState<string | null>(null);

  // UW request form state
  const [uwType, setUwType] = useState<UnderwritingType>('GENERAL');
  // UW complete form state
  const [uwResult, setUwResult] = useState<UnderwritingResultType>('APPROVED');
  const [uwRejectionReason, setUwRejectionReason] = useState<RejectionReason>('HIGH_RISK');
  const [uwSurchargeCondition, setUwSurchargeCondition] = useState<SurchargeCondition>('HIGH_RISK_OCCUPATION');

  const refresh = async (policyNumber: string) => {
    setIsLoading(true);
    setLoadError(null);
    try {
      const [activeResult, historyResult] = await Promise.all([
        getActiveEndorsement(policyNumber).catch((caught) => {
          if (caught instanceof ApiError && caught.status === 404) return null;
          throw caught;
        }),
        listEndorsements(policyNumber)
      ]);
      setActive(activeResult);
      setHistory(historyResult);
      if (activeResult?.underwritingRequestId) {
        try {
          setActiveUw(await getEndorsementUnderwriting(policyNumber));
        } catch {
          setActiveUw(null);
        }
      } else {
        setActiveUw(null);
      }
    } catch (caught) {
      setLoadError(caught instanceof Error ? caught.message : '배서 정보를 불러오지 못했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (contract) {
      refresh(contract.policyNumber);
    } else {
      setActive(null);
      setActiveUw(null);
      setHistory([]);
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
    setHistory([]);
    setLoadError(null);
  };

  const handleApply = async (event: FormEvent) => {
    event.preventDefault();
    if (!contract) return;
    if (!previousContent.trim() || !newContent.trim()) {
      setFormError('변경 전/후 내용을 모두 입력하세요.');
      return;
    }
    setIsBusy(true);
    setFormError(null);
    try {
      await applyEndorsement(contract.policyNumber, {
        endorsementType,
        changeReason,
        previousContent: previousContent.trim(),
        newContent: newContent.trim()
      });
      setPreviousContent('');
      setNewContent('');
      await refresh(contract.policyNumber);
    } catch (caught) {
      setFormError(caught instanceof Error ? caught.message : '배서 신청에 실패했습니다.');
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

  const handleCompleteUw = async () => {
    if (!contract) return;
    await runAction(() =>
      completeEndorsementUnderwriting(contract.policyNumber, {
        underwritingResult: uwResult,
        rejectionReason: uwResult === 'REJECTED' ? uwRejectionReason : null,
        surchargeCondition: uwResult === 'SURCHARGE' ? uwSurchargeCondition : null
      })
    );
  };

  const canApply = contract && !active;
  const activeRequiresUnderwriting = active ? requiresEndorsementUnderwriting(active.changeReason) : false;

  return (
    <AppLayout activeMenuId="contract-endorsement">
      <div className="page-stack contract-page">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>계약 관리</span>
            <span aria-hidden="true">/</span>
            <strong>배서 관리</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>배서 관리</h1>
              <p>계약 내용 변경 신청과 심사, 승인 또는 반려 이력을 관리합니다.</p>
            </div>
            <span className="page-kicker">계약 관리 · 관리자 페이지</span>
          </div>
        </header>

        <ContractLookupCard
          title="배서 대상 계약 조회"
          description="배서 신청할 계약의 증권번호를 입력하세요."
          placeholder="POL-2024-000001"
          onContractLoaded={handleContractLoaded}
          onCleared={handleContractCleared}
        />

        {contract && (
          <>
            {loadError && <AlertMessage type="error" message={loadError} />}
            {isLoading && <div className="work-panel contract-loading-panel">배서 정보를 불러오는 중...</div>}

            {!isLoading && canApply && (
              <form className="work-panel" onSubmit={handleApply}>
                <div className="panel-header compact">
                  <div>
                    <h2>배서 신청</h2>
                    <p>유형/사유와 변경 전/후 내용을 입력합니다.</p>
                  </div>
                </div>
                <div className="form-grid">
                  <label>
                    <span>배서 유형</span>
                    <select
                      value={endorsementType}
                      onChange={(event) => setEndorsementType(event.target.value as EndorsementType)}
                      disabled={isBusy}
                    >
                      {ENDORSEMENT_TYPE_OPTIONS.map((opt) => (
                        <option key={opt} value={opt}>{ENDORSEMENT_TYPE_LABELS[opt]}</option>
                      ))}
                    </select>
                  </label>
                  <label>
                    <span>변경 사유</span>
                    <select
                      value={changeReason}
                      onChange={(event) => setChangeReason(event.target.value as ChangeReason)}
                      disabled={isBusy}
                    >
                      {CHANGE_REASON_OPTIONS.map((opt) => (
                        <option key={opt} value={opt}>{CHANGE_REASON_LABELS[opt]}</option>
                      ))}
                    </select>
                  </label>
                  <label className="full-row">
                    <span>변경 전 내용</span>
                    <textarea
                      value={previousContent}
                      onChange={(event) => setPreviousContent(event.target.value)}
                      rows={3}
                      placeholder="예: 보험료 월 120,000원"
                      disabled={isBusy}
                      required
                    />
                  </label>
                  <label className="full-row">
                    <span>변경 후 내용</span>
                    <textarea
                      value={newContent}
                      onChange={(event) => setNewContent(event.target.value)}
                      rows={3}
                      placeholder="예: 보험료 월 150,000원"
                      disabled={isBusy}
                      required
                    />
                  </label>
                </div>
                {formError && <AlertMessage type="error" message={formError} />}
                <div className="form-actions">
                  <button className="button primary" type="submit" disabled={isBusy}>
                    {isBusy ? '신청 중...' : '배서 신청'}
                  </button>
                </div>
              </form>
            )}

            {!isLoading && active && (
              <div className="work-panel">
                <div className="panel-header compact">
                  <div>
                    <h2>진행 중인 배서</h2>
                    <p>{active.endorsementId} · {getEndorsementStatusLabel(active.endorsementStatus)}</p>
                  </div>
                </div>
                <dl className="detail-grid">
                  <div><dt>유형</dt><dd>{getEndorsementTypeLabel(active.endorsementType)}</dd></div>
                  <div><dt>변경 사유</dt><dd>{getChangeReasonLabel(active.changeReason)}</dd></div>
                  <div className="full-row"><dt>변경 전</dt><dd>{active.previousContent}</dd></div>
                  <div className="full-row"><dt>변경 후</dt><dd>{active.newContent}</dd></div>
                  <div><dt>신청일시</dt><dd>{active.appliedAt}</dd></div>
                  <div><dt>심사요청 ID</dt><dd>{active.underwritingRequestId ?? '-'}</dd></div>
                  <div><dt>승인일시</dt><dd>{active.approvedAt ?? '-'}</dd></div>
                  <div><dt>반려일시</dt><dd>{active.rejectedAt ?? '-'}</dd></div>
                </dl>
                {actionError && <AlertMessage type="error" message={actionError} />}

                {active.endorsementStatus === 'APPLIED' && (
                  <>
                    {!activeRequiresUnderwriting && (
                      <div className="notice-box">
                        <strong>심사 불필요 배서</strong>
                        <p>납입주기 변경과 수익자 변경은 확인 후 바로 승인할 수 있습니다.</p>
                      </div>
                    )}
                    {activeRequiresUnderwriting && !active.underwritingRequestId && (
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
                              requestEndorsementUnderwriting(contract.policyNumber, { underwritingType: uwType })
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
                                  onChange={(event) =>
                                    setUwRejectionReason(event.target.value as RejectionReason)
                                  }
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
                                  onChange={(event) =>
                                    setUwSurchargeCondition(event.target.value as SurchargeCondition)
                                  }
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
                      <button
                        className="button primary"
                        type="button"
                        onClick={() => runAction(() => approveEndorsement(contract.policyNumber))}
                        disabled={
                          isBusy
                          || (
                            activeRequiresUnderwriting
                            && (!activeUw
                              || (
                                activeUw.underwritingResult !== 'APPROVED'
                                && activeUw.underwritingResult !== 'SURCHARGE'
                              ))
                          )
                        }
                      >
                        배서 승인
                      </button>
                      <button
                        className="button"
                        type="button"
                        onClick={() => runAction(() => rejectEndorsement(contract.policyNumber))}
                        disabled={
                          isBusy
                          || !activeRequiresUnderwriting
                          || !activeUw
                          || activeUw.underwritingResult !== 'REJECTED'
                        }
                      >
                        배서 반려
                      </button>
                      <button
                        className="button"
                        type="button"
                        onClick={() => runAction(() => cancelEndorsement(contract.policyNumber))}
                        disabled={isBusy}
                      >
                        배서 취소
                      </button>
                    </div>
                  </>
                )}
              </div>
            )}

            {!isLoading && history.length > 0 && (
              <div className="work-panel">
                <div className="panel-header compact">
                  <div>
                    <h2>배서 이력 ({history.length}건)</h2>
                    <p>이 계약에서 처리된 배서 신청 이력을 확인합니다.</p>
                  </div>
                </div>
                <div className="payout-table">
                  {history.map((item) => (
                    <article key={item.endorsementId} className="payout-row">
                      <header className="payout-row-header">
                        <div>
                          <strong>{item.endorsementId}</strong>
                          <span className={`status-tag ${item.endorsementStatus.toLowerCase()}`}>
                            {getEndorsementStatusLabel(item.endorsementStatus)}
                          </span>
                        </div>
                        <span className="payout-row-sub">
                          {getEndorsementTypeLabel(item.endorsementType)} · {getChangeReasonLabel(item.changeReason)}
                        </span>
                      </header>
                      <dl className="detail-grid">
                        <div className="full-row"><dt>변경 전</dt><dd>{item.previousContent}</dd></div>
                        <div className="full-row"><dt>변경 후</dt><dd>{item.newContent}</dd></div>
                        <div><dt>신청일시</dt><dd>{item.appliedAt}</dd></div>
                        <div><dt>심사요청</dt><dd>{item.underwritingRequestId ?? '-'}</dd></div>
                        <div><dt>승인일시</dt><dd>{item.approvedAt ?? '-'}</dd></div>
                        <div><dt>반려일시</dt><dd>{item.rejectedAt ?? '-'}</dd></div>
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
