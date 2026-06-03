package contract.mapper;

import contract.dto.PaymentCollectionResponse;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentCollectionMapper {

    void insertCollection(
            @Param("collectionId") String collectionId,
            @Param("policyNumber") String policyNumber,
            @Param("installmentNo") Integer installmentNo,
            @Param("dueDate") LocalDate dueDate,
            @Param("plannedAmount") BigDecimal plannedAmount,
            @Param("collectedAmount") BigDecimal collectedAmount,
            @Param("unpaidAmount") BigDecimal unpaidAmount,
            @Param("lateFee") BigDecimal lateFee,
            @Param("paymentMethod") String paymentMethod,
            @Param("processingResult") String processingResult,
            @Param("collectedAt") LocalDateTime collectedAt,
            @Param("createdAt") LocalDateTime createdAt
    );

    PaymentCollectionResponse findByCollectionId(@Param("collectionId") String collectionId);

    List<PaymentCollectionResponse> findByPolicyNumber(@Param("policyNumber") String policyNumber);

    int updateTransfer(
            @Param("collectionId") String collectionId,
            @Param("transferType") String transferType,
            @Param("transferredAt") LocalDateTime transferredAt
    );
}
