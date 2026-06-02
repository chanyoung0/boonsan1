import { Navigate, Route, Routes } from 'react-router-dom';
import { AccidentReportPage } from '../pages/claim/AccidentReportPage';
import { DamageInvestigationPage } from '../pages/claim/DamageInvestigationPage';
import { ObjectionPage } from '../pages/claim/ObjectionPage';
import { PaymentPage } from '../pages/claim/PaymentPage';
import { SubrogationPage } from '../pages/claim/SubrogationPage';
import { MaturityContractPage } from '../pages/contract/MaturityContractPage';
import { PayoutPage } from '../pages/contract/PayoutPage';
import { PaymentCollectionPage } from '../pages/contract/PaymentCollectionPage';
import { ReinstatementPage } from '../pages/contract/ReinstatementPage';
import { EndorsementPage } from '../pages/contract/EndorsementPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/claims/accident" replace />} />
      <Route path="/claims/accident" element={<AccidentReportPage />} />
      <Route path="/claims/investigation" element={<DamageInvestigationPage />} />
      <Route path="/claims/payment" element={<PaymentPage />} />
      <Route path="/claims/subrogation" element={<SubrogationPage />} />
      <Route path="/claims/objection" element={<ObjectionPage />} />
      <Route path="/contracts/maturity" element={<MaturityContractPage />} />
      <Route path="/contracts/payouts" element={<PayoutPage />} />
      <Route path="/contracts/payment-collections" element={<PaymentCollectionPage />} />
      <Route path="/contracts/reinstatements" element={<ReinstatementPage />} />
      <Route path="/contracts/endorsements" element={<EndorsementPage />} />
      <Route path="*" element={<AccidentReportPage />} />
    </Routes>
  );
}
