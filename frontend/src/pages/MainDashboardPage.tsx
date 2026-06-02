import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowRight,
  Banknote,
  CalendarDays,
  ClipboardCheck,
  FileCheck2,
  FileSearch,
  Landmark,
  Package,
  ReceiptText,
  ShieldCheck,
  Sparkles,
  UserCheck
} from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import { getDashboardSummary } from '../api/dashboardApi';
import { AppLayout } from '../components/layout/AppLayout';
import type { DashboardSummaryResponse } from '../types/dashboard';

type ModuleStatus = 'active' | 'planned';

interface WorkModule {
  id: string;
  title: string;
  description: string;
  features: string[];
  href: string;
  buttonLabel: string;
  icon: LucideIcon;
  status: ModuleStatus;
}

interface StatusItemDefinition {
  id: string;
  label: string;
  countKey: keyof DashboardSummaryResponse;
  description: string;
}

interface RecentWorkItem {
  id: string;
  type: string;
  number: string;
  description: string;
  status: string;
  date: string;
  tone: 'primary' | 'success' | 'warning' | 'muted';
}

interface QuickAction {
  id: string;
  label: string;
  href: string;
  icon: LucideIcon;
}

const workModules: WorkModule[] = [
  {
    id: 'product',
    title: '상품 관리',
    description: '보험 상품 개발, 인가, 상품 정보 관리를 수행합니다.',
    features: ['상품 개발', '상품 인가', '상품 조회'],
    href: '#',
    buttonLabel: '준비 중',
    icon: Package,
    status: 'planned'
  },
  {
    id: 'underwriting',
    title: '청약 심사',
    description: '보험청약 접수부터 신용정보 조회, 공동인수, 재보험, 증권 발행까지 처리합니다.',
    features: ['보험청약 심사', '신용정보 조회', '공동인수 처리', '재보험 처리', '증권 발행'],
    href: '/underwriting/review',
    buttonLabel: '청약 심사로 이동',
    icon: FileSearch,
    status: 'active'
  },
  {
    id: 'contract',
    title: '계약 관리',
    description: '체결된 계약의 변경, 부활, 수금, 만기 관리를 수행합니다.',
    features: ['배서 관리', '부활 관리', '분납/수금 관리', '만기계약 관리'],
    href: '#',
    buttonLabel: '준비 중',
    icon: ReceiptText,
    status: 'planned'
  },
  {
    id: 'claim',
    title: '보상 처리',
    description: '사고접수부터 손해조사, 보험금 지급, 구상, 이의제기까지 처리합니다.',
    features: ['사고접수', '손해조사', '보험금 지급', '구상 처리', '이의제기 처리'],
    href: '/claims/accident',
    buttonLabel: '보상 처리로 이동',
    icon: ShieldCheck,
    status: 'active'
  }
];

const emptyDashboardSummary: DashboardSummaryResponse = {
  underwritingInProgress: 0,
  paymentApprovalPending: 0,
  subrogationInProgress: 0,
  objectionReceived: 0
};

const statusItemDefinitions: StatusItemDefinition[] = [
  {
    id: 'underwriting',
    label: '청약 심사 진행 중',
    countKey: 'underwritingInProgress',
    description: '최종 심사가 아직 저장되지 않은 청약'
  },
  {
    id: 'payment',
    label: '지급 심사 대기',
    countKey: 'paymentApprovalPending',
    description: '결재 요청 상태의 지급품의서'
  },
  {
    id: 'subrogation',
    label: '구상 처리 중',
    countKey: 'subrogationInProgress',
    description: '구상 요청 후 회수 완료 전 건'
  },
  {
    id: 'objection',
    label: '이의제기 접수',
    countKey: 'objectionReceived',
    description: '접수 상태의 이의제기 건'
  }
];

const recentWorkItems: RecentWorkItem[] = [
  {
    id: 'recent-underwriting',
    type: '청약 심사',
    number: 'APP-2026-244633',
    description: 'AUTO-2026-001 자동심사 승인',
    status: '승인',
    date: '2026. 6. 3.',
    tone: 'success'
  },
  {
    id: 'recent-claim',
    type: '사고접수',
    number: 'ACC-2026-443688',
    description: 'PROD-PAY-APPROVE-001 보상 처리 테스트',
    status: '종결',
    date: '2026. 6. 2.',
    tone: 'muted'
  },
  {
    id: 'recent-payment',
    type: '지급품의서',
    number: 'PAD-2026-495131',
    description: '보험금 지급 완료 후 구상 처리 연계',
    status: '지급 완료',
    date: '2026. 6. 2.',
    tone: 'primary'
  },
  {
    id: 'recent-policy',
    type: '증권 발행',
    number: 'POL-2026-492783',
    description: '청약 심사 후속 처리 발행 결과',
    status: '발행 완료',
    date: '2026. 6. 3.',
    tone: 'success'
  }
];

const quickActions: QuickAction[] = [
  { id: 'accident', label: '사고접수', href: '/claims/accident', icon: ClipboardCheck },
  { id: 'underwriting', label: '보험청약 심사', href: '/underwriting/review', icon: FileSearch },
  { id: 'credit', label: '신용정보 조회', href: '/underwriting/credit', icon: UserCheck },
  { id: 'payment', label: '보험금 지급 조회', href: '/claims/payment', icon: Banknote },
  { id: 'policy', label: '증권 발행', href: '/underwriting/policy', icon: FileCheck2 }
];

export function MainDashboardPage() {
  const [summary, setSummary] = useState<DashboardSummaryResponse>(emptyDashboardSummary);
  const [summaryLoading, setSummaryLoading] = useState(true);
  const [summaryError, setSummaryError] = useState<string | null>(null);
  const today = new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }).format(new Date());

  useEffect(() => {
    let mounted = true;

    getDashboardSummary()
      .then((data) => {
        if (!mounted) {
          return;
        }
        setSummary(data);
        setSummaryError(null);
      })
      .catch((error: unknown) => {
        if (!mounted) {
          return;
        }
        setSummary(emptyDashboardSummary);
        setSummaryError(error instanceof Error ? error.message : '대시보드 요약 정보를 불러오지 못했습니다.');
      })
      .finally(() => {
        if (mounted) {
          setSummaryLoading(false);
        }
      });

    return () => {
      mounted = false;
    };
  }, []);

  return (
    <AppLayout activeMenuId="dashboard">
      <div className="page-stack dashboard-page">
        <section className="dashboard-hero">
          <div className="dashboard-hero-copy">
            <div className="breadcrumb">
              <span>운영 포털</span>
              <span>/</span>
              <strong>메인 대시보드</strong>
            </div>
            <h1>보험사 차세대 업무 시스템</h1>
            <p>청약 심사, 계약 관리, 보상 처리 업무를 통합 관리합니다.</p>
            <div className="dashboard-hero-actions">
              <Link className="button primary" to="/claims/accident">
                사고접수 시작
                <ArrowRight size={16} />
              </Link>
              <Link className="button secondary" to="/underwriting/review">
                청약 심사 이동
              </Link>
            </div>
          </div>
          <div className="dashboard-hero-status" aria-label="운영 상태">
            <span className="dashboard-date">
              <CalendarDays size={16} />
              {today}
            </span>
            <span className="dashboard-system-badge">
              <Sparkles size={15} />
              Vercel · Render · Supabase 운영 중
            </span>
          </div>
        </section>

        <section className="dashboard-section">
          <div className="dashboard-section-heading">
            <h2>빠른 실행</h2>
            <p>자주 사용하는 업무 화면으로 바로 이동합니다.</p>
          </div>
          <div className="dashboard-quick-grid">
            {quickActions.map((action) => (
              <Link className="dashboard-quick-action" key={action.id} to={action.href}>
                <action.icon size={18} />
                <span>{action.label}</span>
              </Link>
            ))}
          </div>
        </section>

        <section className="dashboard-section">
          <div className="dashboard-section-heading">
            <h2>업무 진행 현황</h2>
            <p>PostgreSQL 업무 테이블을 기준으로 집계합니다.</p>
          </div>
          {summaryError && <div className="alert-message error">{summaryError}</div>}
          <div className="dashboard-status-grid">
            {statusItemDefinitions.map((item) => (
              <article className="work-panel dashboard-status-card" key={item.id}>
                <span>{item.label}</span>
                <strong>{summaryLoading ? '-' : summary[item.countKey]}</strong>
                <p>{item.description}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="dashboard-section">
          <div className="dashboard-section-heading">
            <h2>업무 모듈</h2>
            <p>구현 완료 모듈은 바로 이동할 수 있고, 예정 모듈은 팀원 기능 이식 후 연결합니다.</p>
          </div>
          <div className="dashboard-module-grid">
            {workModules.map((module) => (
              <article className={`work-panel dashboard-module-card ${module.status}`} key={module.id}>
                <div className="dashboard-module-header">
                  <span className="dashboard-module-icon">
                    <module.icon size={22} />
                  </span>
                  <div>
                    <h3>{module.title}</h3>
                    <span className={`dashboard-module-state ${module.status}`}>
                      {module.status === 'active' ? '구현 완료' : '준비 중'}
                    </span>
                  </div>
                </div>
                <p>{module.description}</p>
                <div className="dashboard-feature-list">
                  {module.features.map((feature) => (
                    <span key={feature}>{feature}</span>
                  ))}
                </div>
                {module.status === 'active' ? (
                  <Link className="dashboard-module-link" to={module.href}>
                    {module.buttonLabel}
                    <ArrowRight size={15} />
                  </Link>
                ) : (
                  <button className="dashboard-module-link disabled" type="button" disabled>
                    {module.buttonLabel}
                  </button>
                )}
              </article>
            ))}
          </div>
        </section>

        <section className="dashboard-section">
          <div className="dashboard-section-heading">
            <h2>최근 업무 내역</h2>
            <p>테스트와 배포 확인에 사용한 대표 업무 흐름입니다.</p>
          </div>
          <div className="work-panel dashboard-history-panel">
            <div className="dashboard-history-list">
              {recentWorkItems.map((item) => (
                <article className="dashboard-history-item" key={item.id}>
                  <span className="dashboard-history-type">{item.type}</span>
                  <strong className="mono">{item.number}</strong>
                  <p>{item.description}</p>
                  <span className={`dashboard-history-status ${item.tone}`}>{item.status}</span>
                  <time>{item.date}</time>
                </article>
              ))}
            </div>
          </div>
        </section>

        <section className="dashboard-ops-note">
          <Landmark size={18} />
          <div>
            <strong>현재 배포 구조</strong>
            <p>Frontend Vercel, Backend Render, DB Supabase PostgreSQL, DNS Cloudflare, Domain mjusw.site</p>
          </div>
        </section>
      </div>
    </AppLayout>
  );
}
