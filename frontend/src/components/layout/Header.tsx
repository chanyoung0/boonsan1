import { Monitor, ShieldCheck, UserRound } from 'lucide-react';
import { Link } from 'react-router-dom';
import { apiBaseUrl } from '../../api/apiClient';

export function Header() {
  return (
    <header className="app-header">
      <Link className="brand-mark" to="/" aria-label="대시보드로 이동">
        <ShieldCheck aria-hidden="true" size={24} />
        <span>Boonsan Insurance System</span>
      </Link>
      <div className="header-meta" aria-label="현재 접속 정보">
        <span>
          <UserRound aria-hidden="true" size={16} />
          보상 담당자
        </span>
        <span className="meta-divider" aria-hidden="true" />
        <span>
          <Monitor aria-hidden="true" size={16} />
          운영환경 · {apiBaseUrl}
        </span>
      </div>
    </header>
  );
}
