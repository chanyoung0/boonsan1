import { Navigate, Route, Routes } from 'react-router-dom';
import { AccidentReportPage } from '../pages/claim/AccidentReportPage';
import { DamageInvestigationPage } from '../pages/claim/DamageInvestigationPage';
import { PaymentPage } from '../pages/claim/PaymentPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/claims/accident" replace />} />
      <Route path="/claims/accident" element={<AccidentReportPage />} />
      <Route path="/claims/investigation" element={<DamageInvestigationPage />} />
      <Route path="/claims/payment" element={<PaymentPage />} />
      <Route path="*" element={<AccidentReportPage />} />
    </Routes>
  );
}
