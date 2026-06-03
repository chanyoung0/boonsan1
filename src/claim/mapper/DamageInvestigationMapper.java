package claim.mapper;

import claim.dto.DamageAssessmentRequest;
import claim.dto.DamageInvestigationResultResponse;
import claim.dto.ClaimAlternativeFlowResponse;
import claim.dto.PaymentApprovalDocumentResponse;
import model.accident.AccidentReport;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DamageInvestigationMapper {

    AccidentReport findAccidentReportForInvestigation(@Param("accidentNumber") String accidentNumber);

    void insertDamageInvestigation(
            @Param("investigationId") String investigationId,
            @Param("request") DamageAssessmentRequest request,
            @Param("createdAt") LocalDateTime createdAt
    );

    void insertPaymentApprovalDocument(
            @Param("documentId") String documentId,
            @Param("accidentNumber") String accidentNumber,
            @Param("investigationId") String investigationId,
            @Param("documentType") String documentType,
            @Param("submissionStatus") String submissionStatus,
            @Param("totalDamageAmount") BigDecimal totalDamageAmount,
            @Param("faultRatio") Float faultRatio,
            @Param("createdAt") LocalDateTime createdAt
    );

    int updateLatestPaymentApprovalOpinion(
            @Param("accidentNumber") String accidentNumber,
            @Param("faultRatioOpinion") String faultRatioOpinion,
            @Param("adjusterOpinion") String adjusterOpinion,
            @Param("submissionStatus") String submissionStatus
    );

    int submitLatestPaymentApprovalDocument(
            @Param("accidentNumber") String accidentNumber,
            @Param("employeeNo") String employeeNo,
            @Param("submissionStatus") String submissionStatus,
            @Param("submittedAt") LocalDateTime submittedAt
    );

    int updateLatestPaymentApprovalStatus(
            @Param("accidentNumber") String accidentNumber,
            @Param("currentStatus") String currentStatus,
            @Param("submissionStatus") String submissionStatus
    );

    PaymentApprovalDocumentResponse findPaymentApprovalDocumentByAccidentNumber(
            @Param("accidentNumber") String accidentNumber
    );

    DamageInvestigationResultResponse findDamageInvestigationResultByAccidentNumber(
            @Param("accidentNumber") String accidentNumber
    );

    int updateAccidentStatusToApprovalRequired(@Param("accidentNumber") String accidentNumber);

    int updateAccidentStatus(
            @Param("accidentNumber") String accidentNumber,
            @Param("accidentStatus") String accidentStatus
    );

    void insertAlternativeFlowHistory(
            @Param("actionId") String actionId,
            @Param("accidentNumber") String accidentNumber,
            @Param("actionType") String actionType,
            @Param("employeeNo") String employeeNo,
            @Param("reason") String reason,
            @Param("partnerName") String partnerName,
            @Param("materialChecklist") String materialChecklist,
            @Param("resultMessage") String resultMessage,
            @Param("createdAt") LocalDateTime createdAt
    );

    int completeLatestAlternativeFlow(
            @Param("accidentNumber") String accidentNumber,
            @Param("actionType") String actionType,
            @Param("resultMessage") String resultMessage,
            @Param("completedAt") LocalDateTime completedAt
    );

    List<ClaimAlternativeFlowResponse> findAlternativeFlowHistory(
            @Param("accidentNumber") String accidentNumber
    );
}
