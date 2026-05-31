import { useState } from 'react';
import { AppLayout } from '../../components/layout/AppLayout';
import { AccidentReportDetailCard } from '../../components/claim/AccidentReportDetailCard';
import { AccidentReportForm } from '../../components/claim/AccidentReportForm';
import { AccidentReportResultCard } from '../../components/claim/AccidentReportResultCard';
import { AccidentReportSearchBox } from '../../components/claim/AccidentReportSearchBox';
import { WorkflowSteps } from '../../components/claim/WorkflowSteps';
import type { AccidentReportResponse } from '../../types/claim';

export function AccidentReportPage() {
  const [registrationResult, setRegistrationResult] = useState<AccidentReportResponse | null>(null);

  return (
    <AppLayout activeMenuId="claim-accident">
      <div className="page-stack">
        <header className="page-header">
          <nav className="breadcrumb" aria-label="현재 위치">
            <span>보상 처리</span>
            <span aria-hidden="true">/</span>
            <strong>사고 접수</strong>
          </nav>
          <div className="page-heading-row">
            <div>
              <h1>사고 접수</h1>
              <p>보험 증권번호, 사고 일시, 사고 경위 및 피해 내용을 입력하여 사고 접수를 등록합니다.</p>
            </div>
            <span className="page-kicker">보상 처리 · 관리자 페이지</span>
          </div>
        </header>

        <WorkflowSteps />

        <section className="content-grid">
          <AccidentReportForm onSuccess={setRegistrationResult} />
          <div className="side-stack">
            {registrationResult ? (
              <AccidentReportResultCard data={registrationResult} />
            ) : (
              <aside className="work-panel empty-result">
                <strong>접수 결과 대기</strong>
                <p>사고 접수를 등록하면 백엔드가 발급한 사고 접수번호가 여기에 표시됩니다.</p>
              </aside>
            )}
            {registrationResult && <AccidentReportDetailCard data={registrationResult} />}
          </div>
        </section>

        <AccidentReportSearchBox />
      </div>
    </AppLayout>
  );
}
