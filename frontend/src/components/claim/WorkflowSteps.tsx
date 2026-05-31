import { Banknote, CheckCircle2, ClipboardCheck, Search } from 'lucide-react';

const steps = [
  { id: 'accident', label: '사고접수', icon: ClipboardCheck },
  { id: 'investigation', label: '손해조사', icon: Search },
  { id: 'payment', label: '보험금 지급', icon: Banknote },
  { id: 'closed', label: '종결', icon: CheckCircle2 }
];

export function WorkflowSteps() {
  return (
    <ol className="workflow-steps" aria-label="보상 처리 업무 흐름">
      {steps.map((step, index) => {
        const Icon = step.icon;
        const isActive = index === 0;
        return (
          <li className={`workflow-step ${isActive ? 'current' : ''}`} key={step.id}>
            <span className="workflow-icon">
              <Icon aria-hidden="true" size={18} />
            </span>
            <span>{step.label}</span>
          </li>
        );
      })}
    </ol>
  );
}
