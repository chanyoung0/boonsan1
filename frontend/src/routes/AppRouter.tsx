import { Route, Routes } from 'react-router-dom';
import { MainDashboardPage } from '../pages/MainDashboardPage';
import { AccidentReportPage } from '../pages/claim/AccidentReportPage';
import { DamageInvestigationPage } from '../pages/claim/DamageInvestigationPage';
import { ObjectionPage } from '../pages/claim/ObjectionPage';
import { PaymentPage } from '../pages/claim/PaymentPage';
import { SubrogationPage } from '../pages/claim/SubrogationPage';
import { ProductAuthorizationPage } from '../pages/product/ProductAuthorizationPage';
import { ProductDesignPage } from '../pages/product/ProductDesignPage';
import { UnderwritingCoinsurancePage } from '../pages/underwriting/UnderwritingCoinsurancePage';
import { UnderwritingCreditPage } from '../pages/underwriting/UnderwritingCreditPage';
import { UnderwritingPolicyPage } from '../pages/underwriting/UnderwritingPolicyPage';
import { UnderwritingReinsurancePage } from '../pages/underwriting/UnderwritingReinsurancePage';
import { UnderwritingReviewPage } from '../pages/underwriting/UnderwritingReviewPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<MainDashboardPage />} />
      <Route path="/products/design" element={<ProductDesignPage />} />
      <Route path="/products/authorization" element={<ProductAuthorizationPage />} />
      <Route path="/claims/accident" element={<AccidentReportPage />} />
      <Route path="/claims/investigation" element={<DamageInvestigationPage />} />
      <Route path="/claims/payment" element={<PaymentPage />} />
      <Route path="/claims/subrogation" element={<SubrogationPage />} />
      <Route path="/claims/objection" element={<ObjectionPage />} />
      <Route path="/underwriting/review" element={<UnderwritingReviewPage />} />
      <Route path="/underwriting/credit" element={<UnderwritingCreditPage />} />
      <Route path="/underwriting/coinsurance" element={<UnderwritingCoinsurancePage />} />
      <Route path="/underwriting/reinsurance" element={<UnderwritingReinsurancePage />} />
      <Route path="/underwriting/policy" element={<UnderwritingPolicyPage />} />
      <Route path="*" element={<AccidentReportPage />} />
    </Routes>
  );
}
