import { ClipboardEdit, FileSignature } from 'lucide-react';

const steps = [
  { id: 'design', label: '상품 설계', icon: ClipboardEdit },
  { id: 'authorization', label: '상품 인가 요청', icon: FileSignature }
];

interface ProductWorkflowStepsProps {
  currentStepId?: string;
}

export function ProductWorkflowSteps({ currentStepId = 'design' }: ProductWorkflowStepsProps) {
  return (
    <ol className="workflow-steps" aria-label="상품 관리 업무 흐름">
      {steps.map((step) => {
        const Icon = step.icon;
        const isActive = step.id === currentStepId;
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
