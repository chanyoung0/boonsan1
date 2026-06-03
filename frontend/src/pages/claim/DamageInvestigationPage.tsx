import { KeyboardEvent, useMemo, useState } from 'react';
import { Search } from 'lucide-react';
import {
  createPaymentApprovalDraft,
  getAccidentReportForInvestigation,
  getDamageInvestigationResult,
  getFieldInvestigationMaterials,
  requestInvestigationApproval,
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
  DamageAssessmentRequest,
  DamageInvestigationResultResponse,
  DamageInvestigationStartResponse,
  FieldInvestigationMaterialResponse,
  InvestigationApprovalRequest,
  PaymentApprovalDocumentResponse,
  PaymentApprovalDraftResponse
} from '../../types/claim';

type LoadingAction = 'lookup' | 'materials' | 'draft' | 'opinion' | 'approval' | null;

export function DamageInvestigationPage() {
  const [accidentNumberInput, setAccidentNumberInput] = useState('');
  const [accident, setAccident] = useState<DamageInvestigationStartResponse | null>(null);
  const [materials, setMaterials] = useState<FieldInvestigationMaterialResponse | null>(null);
  const [draft, setDraft] = useState<PaymentApprovalDraftResponse | null>(null);
  const [existingResult, setExistingResult] = useState<DamageInvestigationResultResponse | null>(null);
  const [finalDocument, setFinalDocument] = useState<PaymentApprovalDocumentResponse | null>(null);
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
      setError('사고 접수번호를 입력하세요.');
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
    try {
      const response = await getAccidentReportForInvestigation(accidentNumber);
      setAccident(response);
      setAccidentNumberInput(response.accidentNumber);
      await loadExistingResult(response.accidentNumber);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '사고 접수 내용을 조회할 수 없습니다.');
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
          ? '이미 결재 요청된 손해조사입니다.'
          : '저장된 손해조사 결과를 불러왔습니다.'
      );
    } catch (caught) {
      if (caught instanceof ApiError && caught.status === 404) {
        setSuccess('사고 접수 내용을 조회했습니다. 기존 손해조사 결과가 없어 새 손해조사를 진행할 수 있습니다.');
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
      setSuccess('현장조사 자료를 조회했습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '현장조사 자료를 조회할 수 없습니다.');
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
      setError('이미 저장된 손해조사 결과가 있어 초안을 중복 작성할 수 없습니다.');
      setLoadingAction(null);
      return;
    }
    try {
      setDraft(await createPaymentApprovalDraft(accident.accidentNumber, request));
      setSuccess('지급품의서 초안을 작성했습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '지급품의서 초안 작성에 실패했습니다.');
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
      setSuccess('손해사정인 소견을 저장했습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '손해사정인 소견 저장에 실패했습니다.');
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
      setSuccess('결재 요청이 완료되어 사고 상태가 결재 필요로 변경되었습니다.');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '결재 요청에 실패했습니다.');
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
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>보상 처리</span>
            <span aria-hidden="true">/</span>
            <strong>손해 조사</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>손해 조사</h1>
              <p>사고 접수번호 조회부터 손해액 입력, 지급품의서 작성, 결재 요청까지 Basic Path 순서로 처리합니다.</p>
            </div>
            <span className="page-kicker">보상 처리 · 손해사정</span>
          </div>
        </header>

        <InvestigationWorkflowSteps currentStep={activeStep} />

        <section className="work-panel search-panel investigation-lookup-panel">
          <div className="panel-header compact">
            <div>
              <h2>사고 접수번호 조회</h2>
              <p>손해조사를 시작할 사고 접수번호를 입력합니다.</p>
            </div>
          </div>
          <div className="search-row">
            <input
              aria-label="사고 접수번호"
              value={accidentNumberInput}
              onChange={(event) => setAccidentNumberInput(event.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ACC-2026-783910"
              disabled={loadingAction === 'lookup'}
            />
            <button className="button primary" type="button" onClick={handleLookup} disabled={loadingAction === 'lookup'}>
              <Search aria-hidden="true" size={16} />
              {loadingAction === 'lookup' ? '조회 중...' : '조회'}
            </button>
          </div>
        </section>

        {error && <AlertMessage type="error" message={error} />}
        {success && <AlertMessage type="success" message={success} />}

        {accident && <AccidentReportInvestigationSummary data={accident} />}

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
