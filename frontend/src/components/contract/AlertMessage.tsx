import { AlertCircle, CheckCircle2 } from 'lucide-react';

interface AlertMessageProps {
  type: 'error' | 'success';
  message: string;
}

export function AlertMessage({ type, message }: AlertMessageProps) {
  const Icon = type === 'error' ? AlertCircle : CheckCircle2;

  return (
    <div className={`alert-message ${type}`} role={type === 'error' ? 'alert' : 'status'}>
      <Icon aria-hidden="true" size={18} />
      <span>{message}</span>
    </div>
  );
}
