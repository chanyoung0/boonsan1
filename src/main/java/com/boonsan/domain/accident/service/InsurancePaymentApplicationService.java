package com.boonsan.domain.accident.service;

import com.boonsan.domain.accident.dto.PaymentApprovalDocumentResponse;
import com.boonsan.domain.accident.mapper.DamageInvestigationMapper;
import com.boonsan.domain.model.accident.AccidentReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

// 보험금지급 유스케이스(승인된 지급품의서를 지급 처리)를 담당하는 서비스
@Service
public class InsurancePaymentApplicationService {

    private static final String APPROVED_STATUS = "APPROVED";
    private static final String PAID_STATUS = "PAID";
    private static final String COMPLETED_ACCIDENT_STATUS = "COMPLETED";

    private final DamageInvestigationMapper damageInvestigationMapper;

    public InsurancePaymentApplicationService(DamageInvestigationMapper damageInvestigationMapper) {
        this.damageInvestigationMapper = damageInvestigationMapper;
    }

    // 승인된 지급품의서를 지급 처리하고 사고 상태를 완료로 전환한다.
    @Transactional
    public PaymentApprovalDocumentResponse payPaymentApprovalDocument(String accidentNumber) {
        String normalizedAccidentNumber = requireAccidentReport(accidentNumber).getReportNo();
        PaymentApprovalDocumentResponse existing = requirePaymentApprovalDocument(normalizedAccidentNumber);
        if (!APPROVED_STATUS.equals(existing.getSubmissionStatus())) {
            throw new IllegalArgumentException("Payment approval document can only be paid from APPROVED status.");
        }
        int updated = damageInvestigationMapper.updateLatestPaymentApprovalStatus(
                normalizedAccidentNumber,
                APPROVED_STATUS,
                PAID_STATUS
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Payment approval document can only be paid from APPROVED status.");
        }
        damageInvestigationMapper.updateAccidentStatus(normalizedAccidentNumber, COMPLETED_ACCIDENT_STATUS);
        return requirePaymentApprovalDocument(normalizedAccidentNumber);
    }

    private AccidentReport requireAccidentReport(String accidentNumber) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        AccidentReport report = damageInvestigationMapper.findAccidentReportForInvestigation(normalizedAccidentNumber);
        if (report == null) {
            throw new NoSuchElementException("Accident report not found: " + normalizedAccidentNumber);
        }
        return report;
    }

    private PaymentApprovalDocumentResponse requirePaymentApprovalDocument(String accidentNumber) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(accidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Payment approval document not found: " + accidentNumber);
        }
        return response;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}
