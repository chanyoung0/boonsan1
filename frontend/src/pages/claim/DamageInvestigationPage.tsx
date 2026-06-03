import { type FormEvent, type KeyboardEvent, useMemo, useState } from 'react';
import { Search } from 'lucide-react';
import {
  completeOutsourceInvestigation,
  createPaymentApprovalDraft,
  getAccidentReportForInvestigation,
  getClaimAlternativeFlowHistory,
  getDamageInvestigationResult,
  getFieldInvestigationMaterials,
  rejectInsuranceProcessing,
  requestFraudInvestigation,
  requestInvestigationApproval,
  requestOutsourceInvestigation,
  saveAdjusterOpinion
} from '../../api/claimApi';
import { ApiError } from '../../api/apiClient';
import { AccidentReportInvestigationSummary } from '../../components/claim/AccidentReportInvestigationSummary';
import { AdjusterOpinionForm } from '../../components/claim/AdjusterOpinionForm';
import { AlertMessage } from '../../components/claim/AlertMessage';
import { DamageAssessmentForm } from '../../components/claim/DamageAssessmentForm';
import { DamageInvestigationResultCard } from '../../components/claim/DamageInvestigationResultCard';
import { FieldInvestigationMaterialCard } from '../../components/claim/FieldInvestigationMaterialCard';
import { FinalPaymentApprovalCard } from '../../components/claim/FinalPaymentApprovalCard';
import { InvestigationApprovalPanel } from '../../components/claim/InvestigationApprovalPanel';
import { InvestigationWorkflowSteps } from '../../components/claim/InvestigationWorkflowSteps';
import { PaymentApprovalDraftCard } from '../../components/claim/PaymentApprovalDraftCard';
import { AppLayout } from '../../components/layout/AppLayout';
import type {
  AdjusterOpinionRequest,
  ClaimAlternativeFlowResponse,
  DamageAssessmentRequest,
  DamageInvestigationResultResponse,
  DamageInvestigationStartResponse,
  FieldInvestigationMaterialResponse,
  InvestigationApprovalRequest,
  PaymentApprovalDocumentResponse,
  PaymentApprovalDraftResponse
} from '../../types/claim';
import { getClaimAlternativeActionLabel } from '../../types/claim';

type LoadingAction =
  | 'lookup'
  | 'materials'
  | 'draft'
  | 'opinion'
  | 'approval'
  | 'reject'
  | 'fraud'
  | 'outsource'
  | 'outsourceComplete'
  | 'history'
  | null;

const emptyRejectForm = {
  employeeNo: '',
  rejectionReason: ''
};

const emptyFraudForm = {
  employeeNo: '',
  confirmation: '?�시?�다'
};

const emptyOutsourceForm = {
  employeeNo: '',
  partnerName: '',
  materialChecklist: '보험계약사항, 청구서류, 진단서, 사고경위서, 현장사진, 블랙박스, 수리견적서',
  requestDetails: ''
};

export function DamageInvestigationPage() {
  const [accidentNumberInput, setAccidentNumberInput] = useState('');
  const [accident, setAccident] = useState<DamageInvestigationStartResponse | null>(null);
  const [materials, setMaterials] = useState<FieldInvestigationMaterialResponse | null>(null);
  const [draft, setDraft] = useState<PaymentApprovalDraftResponse | null>(null);
  const [existingResult, setExistingResult] = useState<DamageInvestigationResultResponse | null>(null);
  const [finalDocument, setFinalDocument] = useState<PaymentApprovalDocumentResponse | null>(null);
  const [alternativeHistory, setAlternativeHistory] = useState<ClaimAlternativeFlowResponse[]>([]);
  const [rejectForm, setRejectForm] = useState(emptyRejectForm);
  const [fraudForm, setFraudForm] = useState(emptyFraudForm);
  const [outsourceForm, setOutsourceForm] = useState(emptyOutsourceForm);
  const [loadingAction, setLoadingAction] = useState<LoadingAction>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const activeStep = useMemo(() => {
    if (!accident) return 1;
    if (existingResult) return 7;
    if (!materials) return 2;
    if (!draft) return 3;
    if (!finalDocument) return 5;
    if (finalDocument.accidentStatus !== 'APPROVAL_REQUIRED') return 7;
    return 7;
  }, [accident, existingResult, materials, draft, finalDocument]);

  const normalizedAccidentNumber = accident?.accidentNumber ?? accidentNumberInput.trim();

  const handleLookup = async () => {
    const accidentNumber = accidentNumberInput.trim();
    if (!accidentNumber) {
      setError('?�고 ?�수번호�??�력?�세??');
      return;
    }

    setLoadingAction('lookup');
    setError(null);
    setSuccess(null);
    setAccident(null);
    setMaterials(null);
    setDraft(null);
    setExistingResult(null);
    setFinalDocument(null);
    setAlternativeHistory([]);
    try {
      const response = await getAccidentReportForInvestigation(accidentNumber);
      setAccident(response);
      setAccidentNumberInput(response.accidentNumber);
      await loadExistingResult(response.accidentNumber);
      await loadAlternativeHistory(response.accidentNumber, false);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '?�고 ?�수 ?�용??조회?????�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const loadExistingResult = async (accidentNumber: string) => {
    try {
      const [result, fieldMaterials] = await Promise.all([
        getDamageInvestigationResult(accidentNumber),
        getFieldInvestigationMaterials(accidentNumber)
      ]);
      setExistingResult(result);
      setFinalDocument(result);
      setMaterials(fieldMaterials);
      setSuccess(
        result.accidentStatus === 'APPROVAL_REQUIRED'
          ? '?��? 결재 ?�청???�해조사?�니??'
          : '?�?�된 ?�해조사 결과�?불러?�습?�다.'
      );
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setSuccess('?�고 ?�수 ?�용??조회?�습?�다. 기존 ?�해조사 결과가 ?�어 ???�해조사�?진행?????�습?�다.');
        return;
      }
      throw caught;
    }
  };

  const loadAlternativeHistory = async (accidentNumber: string, showMessage = true) => {
    try {
      const history = await getClaimAlternativeFlowHistory(accidentNumber);
      setAlternativeHistory(history);
      if (showMessage) {
        setSuccess('Alternative Flow ?�력??조회?�습?�다.');
      }
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setAlternativeHistory([]);
        return;
      }
      throw caught;
    }
  };

  const handleLoadMaterials = async () => {
    if (!accident) return;

    setLoadingAction('materials');
    setError(null);
    setSuccess(null);
    try {
      setMaterials(await getFieldInvestigationMaterials(accident.accidentNumber));
      setSuccess('?�장조사 ?�료�?조회?�습?�다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '?�장조사 ?�료�?조회?????�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleRejectInsuranceProcessing = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!accident) return;
    if (!rejectForm.employeeNo.trim() || !rejectForm.rejectionReason.trim()) {
      setError('?�당??번호?� 반려 ?�유�??�력?�세??');
      return;
    }

    setLoadingAction('reject');
    setError(null);
    setSuccess(null);
    try {
      const response = await rejectInsuranceProcessing(accident.accidentNumber, {
        employeeNo: rejectForm.employeeNo.trim(),
        rejectionReason: rejectForm.rejectionReason.trim()
      });
      setAccident((prev) => (prev ? { ...prev, accidentStatus: 'REJECTED' } : prev));
      setSuccess(response.resultMessage || '보험 처리 반려가 ?�?�되?�습?�다.');
      setRejectForm(emptyRejectForm);
      await loadAlternativeHistory(accident.accidentNumber, false);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '보험 처리 반려 ?�?�에 ?�패?�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleRequestFraudInvestigation = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!accident) return;
    if (!fraudForm.employeeNo.trim() || !fraudForm.confirmation.trim()) {
      setError('?�당??번호?� ?�시 ?��?�??�력?�세??');
      return;
    }

    setLoadingAction('fraud');
    setError(null);
    setSuccess(null);
    try {
      const response = await requestFraudInvestigation(accident.accidentNumber, {
        employeeNo: fraudForm.employeeNo.trim(),
        confirmation: fraudForm.confirmation.trim()
      });
      setAccident((prev) => (prev ? { ...prev, accidentStatus: 'FRAUD_INVESTIGATION' } : prev));
      setSuccess(response.resultMessage || '보험?�기 조사 ?�청???�?�되?�습?�다.');
      setFraudForm(emptyFraudForm);
      await loadAlternativeHistory(accident.accidentNumber, false);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '보험?�기 조사 ?�청???�패?�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleRequestOutsourceInvestigation = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!accident) return;
    if (
      !outsourceForm.employeeNo.trim() ||
      !outsourceForm.partnerName.trim() ||
      !outsourceForm.materialChecklist.trim() ||
      !outsourceForm.requestDetails.trim()
    ) {
      setError('?�당??번호, ?�탁 ?�체, ?�달 ?�류, ?�탁 ?�청 ?�용??모두 ?�력?�세??');
      return;
    }

    setLoadingAction('outsource');
    setError(null);
    setSuccess(null);
    try {
      const response = await requestOutsourceInvestigation(accident.accidentNumber, {
        employeeNo: outsourceForm.employeeNo.trim(),
        partnerName: outsourceForm.partnerName.trim(),
        materialChecklist: outsourceForm.materialChecklist.trim(),
        requestDetails: outsourceForm.requestDetails.trim()
      });
      setAccident((prev) => (prev ? { ...prev, accidentStatus: 'OUTSOURCED_INVESTIGATION' } : prev));
      setSuccess(response.resultMessage || '?�해조사 ?�탁 ?�청???�?�되?�습?�다.');
      await loadAlternativeHistory(accident.accidentNumber, false);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '?�해조사 ?�탁 ?�청???�패?�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleCompleteOutsourceInvestigation = async () => {
    if (!accident) return;

    setLoadingAction('outsourceComplete');
    setError(null);
    setSuccess(null);
    try {
      const response = await completeOutsourceInvestigation(accident.accidentNumber);
      const refreshed = await getAccidentReportForInvestigation(accident.accidentNumber);
      setAccident(refreshed);
      setSuccess(response.resultMessage || '?�탁 ?�해조사 결과가 반영?�었?�니??');
      setOutsourceForm(emptyOutsourceForm);
      await loadAlternativeHistory(accident.accidentNumber, false);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '?�탁 ?�해조사 ?�료 처리???�패?�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleCreateDraft = async (request: DamageAssessmentRequest) => {
    if (!accident) return;

    setLoadingAction('draft');
    setError(null);
    setSuccess(null);
    setFinalDocument(null);
    if (existingResult) {
      setError('?��? ?�?�된 ?�해조사 결과가 ?�어 초안??중복 ?�성?????�습?�다.');
      setLoadingAction(null);
      return;
    }
    try {
      setDraft(await createPaymentApprovalDraft(accident.accidentNumber, request));
      setSuccess('지급품?�서 초안???�성?�습?�다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '지급품?�서 초안 ?�성???�패?�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleSaveOpinion = async (request: AdjusterOpinionRequest) => {
    if (!accident) return;

    setLoadingAction('opinion');
    setError(null);
    setSuccess(null);
    try {
      setFinalDocument(await saveAdjusterOpinion(accident.accidentNumber, request));
      setSuccess('?�해?�정???�견???�?�했?�니??');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '?�해?�정???�견 ?�?�에 ?�패?�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleRequestApproval = async (request: InvestigationApprovalRequest) => {
    if (!accident) return;

    setLoadingAction('approval');
    setError(null);
    setSuccess(null);
    try {
      const response = await requestInvestigationApproval(accident.accidentNumber, request);
      setFinalDocument(response);
      setAccident((prev) => (prev ? { ...prev, accidentStatus: response.accidentStatus } : prev));
      setSuccess('결재 ?�청???�료?�어 ?�고 ?�태가 결재 ?�요�?변경되?�습?�다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '결재 ?�청???�패?�습?�다.');
    } finally {
      setLoadingAction(null);
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleLookup();
    }
  };

  return (
    <AppLayout activeMenuId="claim-investigation">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="?�재 ?�치">
            <span>보상 처리</span>
            <span aria-hidden="true">/</span>
            <strong>?�해 조사</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>?�해 조사</h1>
              <p>?�고 ?�수번호 조회부???�해???�력, 지급품?�서 ?�성, 결재 ?�청까�? Basic Path ?�서�?처리?�니??</p>
            </div>
            <span className="page-kicker">보상 처리 · ?�해?�정</span>
          </div>
        </header>

        <InvestigationWorkflowSteps currentStep={activeStep} />

        <section className="work-panel search-panel investigation-lookup-panel">
          <div className="panel-header compact">
            <div>
              <h2>?�고 ?�수번호 조회</h2>
              <p>?�해조사�??�작???�고 ?�수번호�??�력?�니??</p>
            </div>
          </div>
          <div className="search-row">
            <input
              aria-label="?�고 ?�수번호"
              value={accidentNumberInput}
              onChange={(event) => setAccidentNumberInput(event.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ACC-2026-783910"
              disabled={loadingAction === 'lookup'}
            />
            <button className="button primary" type="button" onClick={handleLookup} disabled={loadingAction === 'lookup'}>
              <Search aria-hidden="true" size={16} />
              {loadingAction === 'lookup' ? '조회 �?..' : '조회'}
            </button>
          </div>
        </section>

        {error && <AlertMessage type="error" message={error} />}
        {success && <AlertMessage type="success" message={success} />}

        {accident && <AccidentReportInvestigationSummary data={accident} />}

        {accident && (
          <section className="work-panel form-panel">
            <div className="panel-header">
              <div>
                <h2>?�해조사 Alternative Flow</h2>
                <p>?�나리오 기�? 반려, 보험?�기 조사 ?�청, ?��? ?�해조사 ?�탁??처리?�니??</p>
              </div>
            </div>

            <div className="field-grid three">
              <form className="form-section" onSubmit={handleRejectInsuranceProcessing}>
                <h3>보험 처리 반려</h3>
                <label className="field full">
                  <span>?�당??번호</span>
                  <input
                    value={rejectForm.employeeNo}
                    onChange={(event) => setRejectForm((prev) => ({ ...prev, employeeNo: event.target.value }))}
                    placeholder="EMP-001"
                    disabled={loadingAction === 'reject'}
                  />
                </label>
                <label className="field full">
                  <span>반려 ?�유</span>
                  <textarea
                    value={rejectForm.rejectionReason}
                    onChange={(event) => setRejectForm((prev) => ({ ...prev, rejectionReason: event.target.value }))}
                    placeholder="계약 보장 범위?� 관???�는 ?�고�??�단"
                    disabled={loadingAction === 'reject'}
                  />
                </label>
                <button className="button secondary" type="submit" disabled={loadingAction === 'reject'}>
                  {loadingAction === 'reject' ? '반려 처리 �?..' : '보험 처리 반려'}
                </button>
              </form>

              <form className="form-section" onSubmit={handleRequestFraudInvestigation}>
                <h3>보험?�기 조사 ?�청</h3>
                <label className="field full">
                  <span>?�당??번호</span>
                  <input
                    value={fraudForm.employeeNo}
                    onChange={(event) => setFraudForm((prev) => ({ ...prev, employeeNo: event.target.value }))}
                    placeholder="EMP-001"
                    disabled={loadingAction === 'fraud'}
                  />
                </label>
                <label className="field full">
                  <span>?�시 ?��?</span>
                  <input
                    value={fraudForm.confirmation}
                    onChange={(event) => setFraudForm((prev) => ({ ...prev, confirmation: event.target.value }))}
                    placeholder="?�시?�다"
                    disabled={loadingAction === 'fraud'}
                  />
                </label>
                <button className="button secondary" type="submit" disabled={loadingAction === 'fraud'}>
                  {loadingAction === 'fraud' ? '?�청 �?..' : '보험?�기 조사 ?�청'}
                </button>
              </form>

              <form className="form-section" onSubmit={handleRequestOutsourceInvestigation}>
                <h3>?�해조사 ?�탁</h3>
                <label className="field full">
                  <span>?�당??번호</span>
                  <input
                    value={outsourceForm.employeeNo}
                    onChange={(event) => setOutsourceForm((prev) => ({ ...prev, employeeNo: event.target.value }))}
                    placeholder="EMP-001"
                    disabled={loadingAction === 'outsource'}
                  />
                </label>
                <label className="field full">
                  <span>?�탁 ?�체</span>
                  <input
                    value={outsourceForm.partnerName}
                    onChange={(event) => setOutsourceForm((prev) => ({ ...prev, partnerName: event.target.value }))}
                    placeholder="대한손해사정"
                    disabled={loadingAction === 'outsource'}
                  />
                </label>
                <label className="field full">
                  <span>?�달 ?�류</span>
                  <textarea
                    value={outsourceForm.materialChecklist}
                    onChange={(event) => setOutsourceForm((prev) => ({ ...prev, materialChecklist: event.target.value }))}
                    disabled={loadingAction === 'outsource'}
                  />
                </label>
                <label className="field full">
                  <span>?�탁 ?�청 ?�용</span>
                  <textarea
                    value={outsourceForm.requestDetails}
                    onChange={(event) => setOutsourceForm((prev) => ({ ...prev, requestDetails: event.target.value }))}
                    placeholder="?�체 조사 범위�?초과?�여 ?��? ?�해?�정 ?�탁 ?�청"
                    disabled={loadingAction === 'outsource'}
                  />
                </label>
                <div className="form-actions">
                  <button className="button secondary" type="submit" disabled={loadingAction === 'outsource'}>
                    {loadingAction === 'outsource' ? '?�탁 ?�청 �?..' : '?�해조사 ?�탁'}
                  </button>
                  <button
                    className="button primary"
                    type="button"
                    onClick={handleCompleteOutsourceInvestigation}
                    disabled={loadingAction === 'outsourceComplete'}
                  >
                    {loadingAction === 'outsourceComplete' ? '?�료 처리 �?..' : '?�탁 결과 반영'}
                  </button>
                </div>
              </form>
            </div>
          </section>
        )}

        {accident && (
          <section className="work-panel detail-panel">
            <div className="panel-header detail-title">
              <div>
                <h2>Alternative Flow 처리 ?�력</h2>
                <p>반려, 보험?�기 조사, ?�탁 처리 결과�?조회?�니??</p>
              </div>
              <button
                className="button secondary"
                type="button"
                onClick={() => loadAlternativeHistory(accident.accidentNumber)}
                disabled={loadingAction === 'history'}
              >
                {loadingAction === 'history' ? '조회 �?..' : '?�력 ?�로고침'}
              </button>
            </div>
            {alternativeHistory.length === 0 ? (
              <p className="empty-value">?�?�된 Alternative Flow ?�력???�습?�다.</p>
            ) : (
              <div className="document-list">
                {alternativeHistory.map((item) => (
                  <div className="document-item" key={item.actionId}>
                    <div>
                      <span>{getClaimAlternativeActionLabel(item.actionType)}</span>
                      <strong>{item.resultMessage}</strong>
                      <em>
                        ?�당??{item.employeeNo} · {formatDateTime(item.createdAt)}
                      </em>
                      {item.reason && <p>{item.reason}</p>}
                      {item.partnerName && <p>?�탁 ?�체: {item.partnerName}</p>}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        )}

        <FieldInvestigationMaterialCard
          materials={materials}
          isLoading={loadingAction === 'materials'}
          disabled={!accident}
          onLoad={handleLoadMaterials}
        />

        {existingResult && <DamageInvestigationResultCard result={existingResult} />}

        {accident && materials && !existingResult && (
          <DamageAssessmentForm
            accidentNumber={normalizedAccidentNumber}
            disabled={!materials}
            isSubmitting={loadingAction === 'draft'}
            onSubmit={handleCreateDraft}
          />
        )}

        {draft && !existingResult && <PaymentApprovalDraftCard draft={draft} />}

        {draft && !existingResult && (
          <AdjusterOpinionForm
            accidentNumber={normalizedAccidentNumber}
            disabled={!draft}
            isSubmitting={loadingAction === 'opinion'}
            onSubmit={handleSaveOpinion}
          />
        )}

        {finalDocument && <FinalPaymentApprovalCard document={finalDocument} />}

        {finalDocument && ['DRAFT', 'OPINION_SAVED'].includes(finalDocument.submissionStatus) && (
  <InvestigationApprovalPanel
    accidentNumber={normalizedAccidentNumber}
    disabled={!finalDocument}
    isSubmitting={loadingAction === 'approval'}
    onSubmit={handleRequestApproval}
  />
)}

        {finalDocument?.accidentStatus === 'APPROVAL_REQUIRED' && (
          <aside className="work-panel empty-result next-step-panel">
            <strong>{existingResult ? '이미 결재 요청된 손해조사입니다.' : '다음 단계 가능'}</strong>
            <p>손해조사가 완료되어 결재 필요 상태가 되었습니다. 보험금 지급 단계로 자동 이동하지 않습니다.</p>
          </aside>
        )}
      </div>
    </AppLayout>
  );
}

function formatDateTime(value: string) {
  return new Date(value).toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}
