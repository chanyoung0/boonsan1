import type { AuthorizationStatus } from '../../types/product';
import { getAuthorizationStatusLabel } from '../../types/product';

interface AuthorizationStatusBadgeProps {
  status: AuthorizationStatus;
}

export function AuthorizationStatusBadge({ status }: AuthorizationStatusBadgeProps) {
  return (
    <span className={`status-badge status-${status.toLowerCase()}`}>
      {getAuthorizationStatusLabel(status)}
    </span>
  );
}
