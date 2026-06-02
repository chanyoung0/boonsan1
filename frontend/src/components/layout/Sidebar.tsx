import { useEffect, useState } from 'react';
import { ChevronDown, ChevronRight, LayoutDashboard } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

const menuGroups = [
  {
    id: 'product',
    label: '상품 관리',
    items: [
      { id: 'product-design', label: '상품 설계' },
      { id: 'product-approval', label: '상품 인가 요청' }
    ]
  },
  {
    id: 'underwriting',
    label: '청약 심사',
    items: [
      { id: 'underwriting-review', label: '보험청약 심사' },
      { id: 'underwriting-credit', label: '신용정보 조회' },
      { id: 'underwriting-coinsurance', label: '공동인수 처리' },
      { id: 'underwriting-reinsurance', label: '재보험 처리' },
      { id: 'underwriting-policy', label: '증권 발행' }
    ]
  },
  {
    id: 'contract',
    label: '계약 관리',
    items: [
      { id: 'contract-endorsement', label: '배서 관리' },
      { id: 'contract-revival', label: '부활 관리' },
      { id: 'contract-payment', label: '제지급금 관리' },
      { id: 'contract-installment', label: '분납/수금 관리' },
      { id: 'contract-maturity', label: '만기계약 관리' }
    ]
  },
  {
    id: 'claim',
    label: '보상 처리',
    items: [
      { id: 'claim-accident', label: '사고 접수' },
      { id: 'claim-investigation', label: '손해 조사' },
      { id: 'claim-payment', label: '보험금 지급' },
      { id: 'claim-subrogation', label: '구상 처리' },
      { id: 'claim-objection', label: '이의제기 처리' }
    ]
  }
];

interface SidebarProps {
  activeMenuId?: string;
}

export function Sidebar({ activeMenuId }: SidebarProps) {
  const location = useLocation();
  const currentActiveMenuId = getMenuIdFromPath(location.pathname) ?? activeMenuId;
  const activeGroupId = menuGroups.find((group) => group.items.some((item) => item.id === currentActiveMenuId))?.id;
  const [expandedGroupIds, setExpandedGroupIds] = useState<string[]>(() => (activeGroupId ? [activeGroupId] : []));

  useEffect(() => {
    setExpandedGroupIds(activeGroupId ? [activeGroupId] : []);
  }, [activeGroupId]);

  const toggleGroup = (groupId: string) => {
    setExpandedGroupIds((current) =>
      current.includes(groupId) ? current.filter((id) => id !== groupId) : [...current, groupId]
    );
  };

  return (
    <aside className="sidebar" aria-label="업무 메뉴">
      <Link className={`menu-dashboard-item ${currentActiveMenuId === 'dashboard' ? 'active' : ''}`} to="/">
        <LayoutDashboard size={17} />
        <span>대시보드</span>
      </Link>
      {menuGroups.map((group) => {
        const isExpanded = expandedGroupIds.includes(group.id);
        const hasActive = group.items.some((item) => item.id === currentActiveMenuId);

        return (
          <section className="menu-group" key={group.id}>
            <button
              className={`menu-group-title ${hasActive ? 'active-group' : ''}`}
              type="button"
              onClick={() => toggleGroup(group.id)}
              aria-expanded={isExpanded}
            >
              <span>{group.label}</span>
              {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
            </button>
            {isExpanded && (
              <div className="menu-items">
                {group.items.map((item) => (
                  <Link
                    key={item.id}
                    className={`menu-item ${item.id === currentActiveMenuId ? 'active' : ''}`}
                    to={getMenuHref(item.id)}
                  >
                    {item.label}
                  </Link>
                ))}
              </div>
            )}
          </section>
        );
      })}
    </aside>
  );
}

function getMenuIdFromPath(pathname: string) {
  if (pathname === '/') return 'dashboard';
  if (pathname === '/claims/accident') return 'claim-accident';
  if (pathname === '/claims/investigation') return 'claim-investigation';
  if (pathname === '/claims/payment') return 'claim-payment';
  if (pathname === '/claims/subrogation') return 'claim-subrogation';
  if (pathname === '/claims/objection') return 'claim-objection';
  if (pathname === '/underwriting/review') return 'underwriting-review';
  if (pathname === '/underwriting/credit') return 'underwriting-credit';
  if (pathname === '/underwriting/coinsurance') return 'underwriting-coinsurance';
  if (pathname === '/underwriting/reinsurance') return 'underwriting-reinsurance';
  if (pathname === '/underwriting/policy') return 'underwriting-policy';
  if (pathname.startsWith('/claims/')) return 'claim-accident';
  if (pathname.startsWith('/underwriting/')) return 'underwriting-review';
  if (pathname.startsWith('/contract/')) return 'contract-endorsement';
  if (pathname.startsWith('/product/')) return 'product-design';
  return null;
}

function getMenuHref(itemId: string) {
  if (itemId === 'claim-accident') return '/claims/accident';
  if (itemId === 'claim-investigation') return '/claims/investigation';
  if (itemId === 'claim-payment') return '/claims/payment';
  if (itemId === 'claim-subrogation') return '/claims/subrogation';
  if (itemId === 'claim-objection') return '/claims/objection';
  if (itemId === 'underwriting-review') return '/underwriting/review';
  if (itemId === 'underwriting-credit') return '/underwriting/credit';
  if (itemId === 'underwriting-coinsurance') return '/underwriting/coinsurance';
  if (itemId === 'underwriting-reinsurance') return '/underwriting/reinsurance';
  if (itemId === 'underwriting-policy') return '/underwriting/policy';
  return '#';
}
