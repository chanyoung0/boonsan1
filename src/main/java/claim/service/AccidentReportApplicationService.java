package claim.service;

import claim.dto.AccidentReportCreateRequest;
import claim.dto.AccidentReportResponse;
import claim.mapper.AccidentReportMapper;
import enums.AccidentDetailsType;
import enums.AccidentReportStatus;
import model.accident.AccidentReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccidentReportApplicationService {

    private final AccidentReportMapper accidentReportMapper;

    public AccidentReportApplicationService(AccidentReportMapper accidentReportMapper) {
        this.accidentReportMapper = accidentReportMapper;
    }

    @Transactional
    public AccidentReportResponse create(AccidentReportCreateRequest request) {
        AccidentReport report = toModel(request);
        accidentReportMapper.insertAccidentReport(report);
        return AccidentReportResponse.from(report);
    }

    @Transactional(readOnly = true)
    public AccidentReportResponse findByAccidentNumber(String accidentNumber) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        AccidentReport report = accidentReportMapper.findByAccidentNumber(normalizedAccidentNumber);
        if (report == null) {
            throw new NoSuchElementException("Accident report not found: " + normalizedAccidentNumber);
        }
        return AccidentReportResponse.from(report);
    }

    private AccidentReport toModel(AccidentReportCreateRequest request) {
        return new AccidentReport(
                generateReportNo(),
                requireText(request.getPolicyNumber(), "policyNumber"),
                request.getAccidentAt(),
                requireText(request.getAccidentDescription(), "accidentDescription"),
                requireText(request.getDamageDetails(), "damageDetails"),
                request.getAccidentType() == null ? AccidentDetailsType.VEHICLE : request.getAccidentType(),
                AccidentReportStatus.RECEIVED,
                normalizeOptionalText(request.getAccidentReportDocumentName()),
                normalizeOptionalText(request.getMedicalCertificateFileName()),
                normalizeOptionalText(request.getClaimDocumentName()),
                LocalDateTime.now()
        );
    }

    private String generateReportNo() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "ACC-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }

    private String requireText(String value, String fieldName) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
