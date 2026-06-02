import type { ProductStatus } from '../../types/product';
import { getProductStatusLabel } from '../../types/product';

interface ProductStatusBadgeProps {
  status: ProductStatus;
}

export function ProductStatusBadge({ status }: ProductStatusBadgeProps) {
  return <span className={`status-badge status-${status.toLowerCase()}`}>{getProductStatusLabel(status)}</span>;
}
