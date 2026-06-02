import { Navigate, Route, Routes } from 'react-router-dom';
import { AccidentReportPage } from '../pages/claim/AccidentReportPage';
import { DamageInvestigationPage } from '../pages/claim/DamageInvestigationPage';
import { ObjectionPage } from '../pages/claim/ObjectionPage';
import { PaymentPage } from '../pages/claim/PaymentPage';
import { SubrogationPage } from '../pages/claim/SubrogationPage';
import { UnderwritingCreditPage } from '../pages/underwriting/UnderwritingCreditPage';
import { UnderwritingReviewPage } from '../pages/underwriting/UnderwritingReviewPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/claims/accident" replace />} />
      <Route path="/claims/accident" element={<AccidentReportPage />} />
      <Route path="/claims/investigation" element={<DamageInvestigationPage />} />
      <Route path="/claims/payment" element={<PaymentPage />} />
      <Route path="/claims/subrogation" element={<SubrogationPage />} />
      <Route path="/claims/objection" element={<ObjectionPage />} />
      <Route path="/underwriting/review" element={<UnderwritingReviewPage />} />
      <Route path="/underwriting/credit" element={<UnderwritingCreditPage />} />
      <Route path="*" element={<AccidentReportPage />} />
    </Routes>
  );
}
