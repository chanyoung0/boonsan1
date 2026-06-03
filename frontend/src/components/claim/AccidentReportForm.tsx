import { useRef, useState, type ChangeEvent, type FormEvent, type ReactNode } from 'react';
import { Clock, FileText, Info, RotateCcw, Save } from 'lucide-react';
import { createAccidentReport } from '../../api/claimApi';
import type { AccidentReportCreateRequest, AccidentReportResponse, AccidentType } from '../../types/claim';
import { ACCIDENT_TYPE_LABELS } from '../../types/claim';
import { AlertMessage } from './AlertMessage';

interface AccidentReportFormProps {
  onSuccess: (data: AccidentReportResponse) => void;
}

const emptyForm = {
  policyNumber: '',
  accidentAt: '',
  accidentType: '' as AccidentType | '',
  accidentDescription: '',
  damageDetails: '',
  accidentReportDocumentName: '',
  medicalCertificateFileName: '',
  claimDocumentName: '',
  submitDocumentsLater: false
};

type AccidentReportFormData = typeof emptyForm;
type TextFormField = Exclude<keyof AccidentReportFormData, 'submitDocumentsLater'>;
type AttachmentField = 'accidentReportDocumentName' | 'medicalCertificateFileName' | 'claimDocumentName';

const attachmentFields: Array<{ name: AttachmentField; label: string }> = [
  { name: 'accidentReportDocumentName', label: '사고경위서' },
  { name: 'medicalCertificateFileName', label: '진단서' },
  { name: 'claimDocumentName', label: '청구서류' }
];

export function AccidentReportForm({ onSuccess }: AccidentReportFormProps) {
  const [formData, setFormData] = useState(emptyForm);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const fileInputRefs = useRef<Record<AttachmentField, HTMLInputElement | null>>({
    accidentReportDocumentName: null,
    medicalCertificateFileName: null,
    claimDocumentName: null
  });

  const updateField = (name: TextFormField, value: string) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const updateSubmitDocumentsLater = (checked: boolean) => {
    setFormData((prev) => ({
      ...prev,
      submitDocumentsLater: checked,
      accidentReportDocumentName: checked ? '' : prev.accidentReportDocumentName,
      medicalCertificateFileName: checked ? '' : prev.medicalCertificateFileName,
      claimDocumentName: checked ? '' : prev.claimDocumentName
    }));
    if (checked) {
      attachmentFields.forEach(({ name }) => {
        const input = fileInputRefs.current[name];
        if (input) {
          input.value = '';
        }
      });
    }
  };

  const handleFileChange = (name: AttachmentField, event: ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.currentTarget.files?.[0];
    updateField(name, selectedFile?.name ?? '');
  };

  const handleClearFile = (name: AttachmentField) => {
    updateField(name, '');
    const input = fileInputRefs.current[name];
    if (input) {
      input.value = '';
    }
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (
      !formData.policyNumber.trim() ||
      !formData.accidentAt ||
      !formData.accidentType ||
      !formData.accidentDescription.trim() ||
      !formData.damageDetails.trim()
    ) {
      setError('필수 항목을 모두 입력하세요.');
      return;
    }

    const request: AccidentReportCreateRequest = {
      policyNumber: formData.policyNumber.trim(),
      accidentAt: formData.accidentAt,
      accidentDescription: formData.accidentDescription.trim(),
      damageDetails: formData.damageDetails.trim(),
      accidentType: formData.accidentType,
      accidentReportDocumentName: formData.submitDocumentsLater
        ? null
        : normalizeOptional(formData.accidentReportDocumentName),
      medicalCertificateFileName: formData.submitDocumentsLater
        ? null
        : normalizeOptional(formData.medicalCertificateFileName),
      claimDocumentName: formData.submitDocumentsLater ? null : normalizeOptional(formData.claimDocumentName),
      submitDocumentsLater: formData.submitDocumentsLater
    };

    setIsSubmitting(true);
    try {
      const response = await createAccidentReport(request);
      onSuccess(response);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '사고 접수 등록 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleReset = () => {
    setFormData(emptyForm);
    setError(null);
    attachmentFields.forEach(({ name }) => {
      const input = fileInputRefs.current[name];
      if (input) {
        input.value = '';
      }
    });
  };

  return (
    <form className="work-panel form-panel" onSubmit={handleSubmit}>
      <div className="panel-header">
        <div>
          <h2>사고 접수 입력</h2>
          <p>보험 증권번호, 사고 일시, 사고 경위 및 피해 내용을 입력합니다.</p>
        </div>
      </div>

      {error && <AlertMessage type="error" message={error} />}

      <section className="form-section">
        <SectionTitle icon={<FileText size={17} />} title="계약 정보" />
        <label className="field full">
          <span>증권번호</span>
          <input
            required
            value={formData.policyNumber}
            onChange={(event) => updateField('policyNumber', event.target.value)}
            placeholder="P2024-003456"
            disabled={isSubmitting}
          />
        </label>
      </section>

      <section className="form-section">
        <SectionTitle icon={<FileText size={17} />} title="사고 기본 정보" />
        <div className="field-grid two">
          <label className="field">
            <span>사고 일시</span>
            <input
              required
              type="datetime-local"
              value={formData.accidentAt}
              onChange={(event) => updateField('accidentAt', event.target.value)}
              disabled={isSubmitting}
            />
          </label>
          <label className="field">
            <span>사고 유형</span>
            <select
              required
              value={formData.accidentType}
              onChange={(event) => updateField('accidentType', event.target.value)}
              disabled={isSubmitting}
            >
              <option value="">사고 유형을 선택하세요</option>
              {(Object.keys(ACCIDENT_TYPE_LABELS) as AccidentType[]).map((type) => (
                <option value={type} key={type}>
                  {ACCIDENT_TYPE_LABELS[type]}
                </option>
              ))}
            </select>
          </label>
        </div>
      </section>

      <section className="form-section">
        <SectionTitle icon={<FileText size={17} />} title="사고 내용" />
        <label className="field full">
          <span>사고 경위</span>
          <textarea
            required
            value={formData.accidentDescription}
            onChange={(event) => updateField('accidentDescription', event.target.value)}
            placeholder="사고 발생 상황을 입력하세요."
            disabled={isSubmitting}
          />
        </label>
        <label className="field full">
          <span>피해 내용</span>
          <textarea
            required
            value={formData.damageDetails}
            onChange={(event) => updateField('damageDetails', event.target.value)}
            placeholder="차량 파손, 신체 피해, 재물 피해 등 피해 내용을 입력하세요."
            disabled={isSubmitting}
          />
        </label>
      </section>

      <section className="form-section">
        <SectionTitle icon={<FileText size={17} />} title="첨부 서류" />
        <div className="inline-note">
          <Info aria-hidden="true" size={16} />
          <span>현재 단계에서는 실제 파일 업로드가 아닌 첨부 서류 파일명만 등록합니다.</span>
        </div>
        <label className="field full">
          <span>서류 제출 방식</span>
          <label className="checkbox-row">
            <input
              type="checkbox"
              checked={formData.submitDocumentsLater}
              onChange={(event) => updateSubmitDocumentsLater(event.target.checked)}
              disabled={isSubmitting}
            />
            <span>
              <Clock aria-hidden="true" size={15} />
              서류 나중에 제출
            </span>
          </label>
        </label>
        {formData.submitDocumentsLater && (
          <div className="inline-note">
            <Clock aria-hidden="true" size={16} />
            <span>접수 후 상태가 서류 보완 필요로 저장되고, 백엔드가 제출 기한을 자동 산정합니다.</span>
          </div>
        )}
        <div className="field-grid three">
          {attachmentFields.map(({ name, label }) => (
            <div className="field file-field" key={name}>
              <label htmlFor={name}>{label}</label>
              <input
                id={name}
                ref={(element) => {
                  fileInputRefs.current[name] = element;
                }}
                type="file"
                onChange={(event) => handleFileChange(name, event)}
                disabled={isSubmitting || formData.submitDocumentsLater}
              />
              <div className="selected-file-row">
                <span className={formData[name] ? 'selected-file-name' : 'selected-file-empty'}>
                  {formData.submitDocumentsLater ? '서류 나중 제출' : formData[name] || '선택된 파일 없음'}
                </span>
                {formData[name] && !formData.submitDocumentsLater && (
                  <button
                    type="button"
                    className="file-clear-button"
                    onClick={() => handleClearFile(name)}
                    disabled={isSubmitting}
                  >
                    선택 취소
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </section>

      <div className="form-actions">
        <button type="button" className="button secondary" onClick={handleReset} disabled={isSubmitting}>
          <RotateCcw aria-hidden="true" size={16} />
          입력 초기화
        </button>
        <button type="submit" className="button primary" disabled={isSubmitting}>
          <Save aria-hidden="true" size={16} />
          {isSubmitting ? '등록 중...' : '사고 접수 등록'}
        </button>
      </div>
    </form>
  );
}

function SectionTitle({ icon, title }: { icon: ReactNode; title: string }) {
  return (
    <div className="section-title">
      {icon}
      <h3>{title}</h3>
    </div>
  );
}

function normalizeOptional(value: string) {
  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed : null;
}
