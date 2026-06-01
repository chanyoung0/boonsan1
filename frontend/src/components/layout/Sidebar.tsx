import { ChevronDown, ChevronRight } from 'lucide-react';

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

export function Sidebar({ activeMenuId = 'claim-accident' }: SidebarProps) {
  return (
    <aside className="sidebar" aria-label="업무 메뉴">
      {menuGroups.map((group) => {
        const isExpanded = group.id === 'claim';
        const hasActive = group.items.some((item) => item.id === activeMenuId);

        return (
          <section className="menu-group" key={group.id}>
            <div className={`menu-group-title ${hasActive ? 'active-group' : ''}`}>
              <span>{group.label}</span>
              {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
            </div>
            {isExpanded && (
              <div className="menu-items">
                {group.items.map((item) => (
                  <a
                    key={item.id}
                    className={`menu-item ${item.id === activeMenuId ? 'active' : ''}`}
                    href={
                      item.id === 'claim-accident'
                        ? '/claims/accident'
                        : item.id === 'claim-investigation'
                          ? '/claims/investigation'
                          : item.id === 'claim-payment'
                            ? '/claims/payment'
                            : item.id === 'claim-subrogation'
                              ? '/claims/subrogation'
                              : item.id === 'claim-objection'
                                ? '/claims/objection'
                                : '#'
                    }
                  >
                    {item.label}
                  </a>
                ))}
              </div>
            )}
          </section>
        );
      })}
    </aside>
  );
}
