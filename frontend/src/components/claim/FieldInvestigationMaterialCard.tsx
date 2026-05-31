import { FileText, FolderSearch } from 'lucide-react';
import type { FieldInvestigationMaterialResponse } from '../../types/claim';

interface FieldInvestigationMaterialCardProps {
  materials: FieldInvestigationMaterialResponse | null;
  isLoading: boolean;
  disabled: boolean;
  onLoad: () => void;
}

export function FieldInvestigationMaterialCard({
  materials,
  isLoading,
  disabled,
  onLoad
}: FieldInvestigationMaterialCardProps) {
  return (
    <section className="work-panel detail-panel">
      <div className="panel-header">
        <div>
          <h2>현장조사 자료</h2>
          <p>현재 단계에서는 실제 파일 업로드 없이 Mock/파일명 기반 자료를 표시합니다.</p>
        </div>
        <button className="button secondary" type="button" onClick={onLoad} disabled={disabled || isLoading}>
          <FolderSearch aria-hidden="true" size={16} />
          {isLoading ? '조회 중...' : '현장조사 자료'}
        </button>
      </div>

      {materials ? (
        <div className="document-grid">
          <MaterialItem label="사고현장 사진" value={materials.accidentScenePhotoName} />
          <MaterialItem label="블랙박스 영상" value={materials.blackBoxVideoName} />
          <MaterialItem label="수리 견적" value={materials.repairEstimateFileName} />
        </div>
      ) : (
        <div className="empty-inline">사고 접수번호 조회 후 현장조사 자료 버튼을 누르세요.</div>
      )}
    </section>
  );
}

function MaterialItem({ label, value }: { label: string; value: string }) {
  return (
    <div className="document-item">
      <FileText aria-hidden="true" size={22} />
      <div>
        <span>{label}</span>
        <strong className="document-file-name" title={value}>
          {value}
        </strong>
        <em>Mock 파일명</em>
      </div>
    </div>
  );
}
