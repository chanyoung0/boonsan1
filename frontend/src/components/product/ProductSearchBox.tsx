import { useState, type KeyboardEvent } from 'react';
import { Search } from 'lucide-react';
import { getProduct } from '../../api/productApi';
import type { ProductResponse } from '../../types/product';
import { AlertMessage } from './AlertMessage';
import { ProductDetailCard } from './ProductDetailCard';

export function ProductSearchBox() {
  const [productCode, setProductCode] = useState('');
  const [result, setResult] = useState<ProductResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = async () => {
    const normalized = productCode.trim();
    if (!normalized) {
      setError('상품 코드를 입력하세요.');
      return;
    }

    setIsLoading(true);
    setError(null);
    setResult(null);
    try {
      setResult(await getProduct(normalized));
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '해당 상품 코드를 찾을 수 없습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <section className="search-section">
      <div className="work-panel search-panel">
        <div className="panel-header compact">
          <div>
            <h2>상품 조회</h2>
            <p>저장된 상품 코드로 상세 정보를 조회합니다.</p>
          </div>
        </div>
        <div className="search-row">
          <input
            aria-label="상품 코드"
            value={productCode}
            onChange={(event) => setProductCode(event.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="PRD-AUTO-2026-000123"
            disabled={isLoading}
          />
          <button className="button primary" type="button" onClick={handleSearch} disabled={isLoading}>
            <Search aria-hidden="true" size={16} />
            {isLoading ? '조회 중...' : '조회'}
          </button>
        </div>
        {error && <AlertMessage type="error" message={error} />}
      </div>

      {result && <ProductDetailCard data={result} />}
    </section>
  );
}
