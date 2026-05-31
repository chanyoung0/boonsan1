import { KeyboardEvent, useState } from 'react';
import { Search } from 'lucide-react';
import { getAccidentReport } from '../../api/claimApi';
import type { AccidentReportResponse } from '../../types/claim';
import { AlertMessage } from './AlertMessage';
import { AccidentReportDetailCard } from './AccidentReportDetailCard';

export function AccidentReportSearchBox() {
  const [accidentNumber, setAccidentNumber] = useState('');
  const [result, setResult] = useState<AccidentReportResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = async () => {
    const normalized = accidentNumber.trim();
    if (!normalized) {
      setError('사고 접수번호를 입력하세요.');
      return;
    }

    setIsLoading(true);
    setError(null);
    setResult(null);
    try {
      setResult(await getAccidentReport(normalized));
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '해당 사고 접수번호를 찾을 수 없습니다.');
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
            <h2>사고 접수 조회</h2>
            <p>백엔드에 저장된 사고 접수번호로 상세 정보를 조회합니다.</p>
          </div>
        </div>
        <div className="search-row">
          <input
            aria-label="사고 접수번호"
            value={accidentNumber}
            onChange={(event) => setAccidentNumber(event.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="ACC-2026-783910"
            disabled={isLoading}
          />
          <button className="button primary" type="button" onClick={handleSearch} disabled={isLoading}>
            <Search aria-hidden="true" size={16} />
            {isLoading ? '조회 중...' : '조회'}
          </button>
        </div>
        {error && <AlertMessage type="error" message={error} />}
      </div>

      {result && <AccidentReportDetailCard data={result} />}
    </section>
  );
}
