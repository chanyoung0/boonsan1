import { useRef, useState, type ChangeEvent, type FormEvent } from 'react';
import { FileSignature, Info, Save } from 'lucide-react';
import { createAuthorization } from '../../api/productApi';
import type { AuthorizationCreateRequest, AuthorizationResponse } from '../../types/product';
import { AlertMessage } from './AlertMessage';

interface AuthorizationFormProps {
  productCode: string;
  onSuccess: (data: AuthorizationResponse) => void;
}

const emptyForm = {
  requestReason: '',
  submissionAgencyName: '금융감독원',
  productDescriptionFileName: '',
  termsAndConditionsFileName: '',
  rateScheduleFileName: '',
  productEvidenceFileName: ''
};

type AttachmentField =
  | 'productDescriptionFileName'
  | 'termsAndConditionsFileName'
  | 'rateScheduleFileName'
  | 'productEvidenceFileName';

const attachmentFields: Array<{ name: AttachmentField; label: string }> = [
  { name: 'productDescriptionFileName', label: '상품설명서' },
  { name: 'termsAndConditionsFileName', label: '약관' },
  { name: 'rateScheduleFileName', label: '요율서' },
  { name: 'productEvidenceFileName', label: '상품개발 근거자료' }
];

export function AuthorizationForm({ productCode, onSuccess }: AuthorizationFormProps) {
  const [formData, setFormData] = useState(emptyForm);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const fileInputRefs = useRef<Record<AttachmentField, HTMLInputElement | null>>({
    productDescriptionFileName: null,
    termsAndConditionsFileName: null,
    rateScheduleFileName: null,
    productEvidenceFileName: null
  });

  const updateField = (name: keyof typeof formData, value: string) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
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

    if (!formData.requestReason.trim() || !formData.submissionAgencyName.trim()) {
      setError('요청 사유와 제출 기관명을 입력하세요.');
      return;
    }

    const request: AuthorizationCreateRequest = {
      requestReason: formData.requestReason.trim(),
      submissionAgencyName: formData.submissionAgencyName.trim(),
      productDescriptionFileName: normalizeOptional(formData.productDescriptionFileName),
      termsAndConditionsFileName: normalizeOptional(formData.termsAndConditionsFileName),
      rateScheduleFileName: normalizeOptional(formData.rateScheduleFileName),
      productEvidenceFileName: normalizeOptional(formData.productEvidenceFileName)
    };

    setIsSubmitting(true);
    try {
      const response = await createAuthorization(productCode, request);
      onSuccess(response);
      setFormData(emptyForm);
      attachmentFields.forEach(({ name }) => {
        const input = fileInputRefs.current[name];
        if (input) input.value = '';
      });
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '인가 요청 등록 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="work-panel form-panel" onSubmit={handleSubmit}>
      <div className="panel-header compact">
        <div>
          <h2>인가 요청 등록</h2>
          <p>요청 사유, 제출 기관명, 첨부 서류를 입력하여 금융감독원에 인가를 요청합니다.</p>
        </div>
        <FileSignature aria-hidden="true" size={22} />
      </div>

      {error && <AlertMessage type="error" message={error} />}

      <div className="field-grid two">
        <label className="field">
          <span>제출 기관명</span>
          <input
            required
            value={formData.submissionAgencyName}
            onChange={(event) => updateField('submissionAgencyName', event.target.value)}
            disabled={isSubmitting}
          />
        </label>
        <label className="field">
          <span>상품 코드</span>
          <input value={productCode} disabled readOnly />
        </label>
      </div>

      <label className="field form-section">
        <span>요청 사유</span>
        <textarea
          required
          value={formData.requestReason}
          onChange={(event) => updateField('requestReason', event.target.value)}
          placeholder="상품 인가 요청 사유를 입력하세요."
          disabled={isSubmitting}
        />
      </label>

      <section className="form-section">
        <div className="section-title">
          <FileSignature size={17} />
          <h3>첨부 서류</h3>
        </div>
        <div className="inline-note">
          <Info aria-hidden="true" size={16} />
          <span>현재 단계에서는 실제 파일 업로드가 아닌 첨부 서류 파일명만 등록합니다.</span>
        </div>
        <div className="field-grid two">
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
                disabled={isSubmitting}
              />
              <div className="selected-file-row">
                <span className={formData[name] ? 'selected-file-name' : 'selected-file-empty'}>
                  {formData[name] || '선택된 파일 없음'}
                </span>
                {formData[name] && (
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
        <button type="submit" className="button primary" disabled={isSubmitting}>
          <Save aria-hidden="true" size={16} />
          {isSubmitting ? '요청 중...' : '인가 요청 등록'}
        </button>
      </div>
    </form>
  );
}

function normalizeOptional(value: string) {
  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed : null;
}
