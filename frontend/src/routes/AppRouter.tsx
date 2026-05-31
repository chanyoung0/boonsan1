import { Navigate, Route, Routes } from 'react-router-dom';
import { AccidentReportPage } from '../pages/claim/AccidentReportPage';
import { DamageInvestigationPage } from '../pages/claim/DamageInvestigationPage';

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/claims/accident" replace />} />
      <Route path="/claims/accident" element={<AccidentReportPage />} />
      <Route path="/claims/investigation" element={<DamageInvestigationPage />} />
      <Route path="*" element={<AccidentReportPage />} />
    </Routes>
  );
}
