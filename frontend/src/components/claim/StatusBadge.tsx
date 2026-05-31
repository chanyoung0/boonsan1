import type { AccidentStatus } from '../../types/claim';
import { getAccidentStatusLabel } from '../../types/claim';

interface StatusBadgeProps {
  status: AccidentStatus;
}

export function StatusBadge({ status }: StatusBadgeProps) {
  return <span className={`status-badge status-${status.toLowerCase()}`}>{getAccidentStatusLabel(status)}</span>;
}
