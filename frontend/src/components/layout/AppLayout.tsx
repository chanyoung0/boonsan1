import type { ReactNode } from 'react';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

interface AppLayoutProps {
  children: ReactNode;
  activeMenuId?: string;
}

export function AppLayout({ children, activeMenuId }: AppLayoutProps) {
  return (
    <div className="app-shell">
      <Header />
      <div className="app-body">
        <Sidebar activeMenuId={activeMenuId} />
        <main className="main-content">{children}</main>
      </div>
    </div>
  );
}
