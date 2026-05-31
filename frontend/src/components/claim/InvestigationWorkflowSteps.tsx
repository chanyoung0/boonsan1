import { CheckCircle2, Circle } from 'lucide-react';

const steps = [
  '접수 내용 조회',
  '현장조사 자료',
  '손해액 입력',
  '품의서 초안',
  '소견 작성',
  '최종 품의서',
  '결재 요청'
];

interface InvestigationWorkflowStepsProps {
  currentStep: number;
}

export function InvestigationWorkflowSteps({ currentStep }: InvestigationWorkflowStepsProps) {
  return (
    <ol className="investigation-steps" aria-label="손해조사 진행 단계">
      {steps.map((label, index) => {
        const stepNo = index + 1;
        const isDone = stepNo < currentStep;
        const isCurrent = stepNo === currentStep;

        return (
          <li
            className={`investigation-step ${isCurrent ? 'current' : ''} ${isDone ? 'done' : ''}`}
            key={label}
          >
            <span className="workflow-icon">
              {isDone ? <CheckCircle2 size={16} /> : <Circle size={16} />}
            </span>
            <span>{label}</span>
          </li>
        );
      })}
    </ol>
  );
}
