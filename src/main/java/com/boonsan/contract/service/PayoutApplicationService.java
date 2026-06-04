package com.boonsan.contract.service;

import com.boonsan.contract.dto.PayoutApproveRequest;
import com.boonsan.contract.dto.PayoutCreateRequest;
import com.boonsan.contract.dto.PayoutResponse;
import com.boonsan.contract.mapper.PayoutMapper;
import com.boonsan.enums.CalculationBasis;
import com.boonsan.enums.PayoutStatus;
import com.boonsan.model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PayoutApplicationService {

    // 환급률: insurance-system-architecture 스킬의 만기환급금 = 납입보험료 합계 × 0.9 외에는 사용자(찬영) 2026-06-02 확정값.
    private static final BigDecimal RATE_MATURITY_REFUND = new BigDecimal("0.90");
    private static final BigDecimal RATE_SURRENDER = new BigDecimal("0.70");
    private static final BigDecimal RATE_MID_SURRENDER = new BigDecimal("0.50");
    private static final BigDecimal RATE_DIVIDEND = new BigDecimal("0.02");

    private final ContractApplicationService contractApplicationService;
    private final PayoutMapper payoutMapper;

    public PayoutApplicationService(
            ContractApplicationService contractApplicationService,
            PayoutMapper payoutMapper
    ) {
        this.contractApplicationService = contractApplicationService;
        this.payoutMapper = payoutMapper;
    }

    @Transactional
    public PayoutResponse createPayout(String policyNumber, PayoutCreateRequest request) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        if (request.getCalculationBasis() == null) {
            throw new IllegalArgumentException("calculationBasis is required.");
        }
        if (request.getPaymentType() == null) {
            throw new IllegalArgumentException("paymentType is required.");
        }
        if (request.getPaidPremiumAmount() == null || request.getPaidPremiumAmount().signum() <= 0) {
            throw new IllegalArgumentException("paidPremiumAmount must be greater than 0.");
        }
        BigDecimal deductionAmount = request.getDeductionAmount() == null
                ? BigDecimal.ZERO
                : request.getDeductionAmount();
        if (deductionAmount.signum() < 0) {
            throw new IllegalArgumentException("deductionAmount must be >= 0.");
        }

        BigDecimal refundRate = resolveRefundRate(request.getCalculationBasis());
        BigDecimal calculatedAmount = request.getPaidPremiumAmount()
                .multiply(refundRate)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalPaymentAmount = calculatedAmount.subtract(deductionAmount);
        if (finalPaymentAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "finalPaymentAmount cannot be negative (calculated " + calculatedAmount
                            + " - deduction " + deductionAmount + ").");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        String payoutId = generatePayoutId();

        payoutMapper.insertPayout(
                payoutId,
                contract.getPolicyNumber(),
                request.getCalculationBasis().name(),
                request.getPaymentType().name(),
                request.getPaidPremiumAmount(),
                refundRate,
                calculatedAmount,
                normalizeOptionalText(request.getDeductionItem()),
                deductionAmount,
                finalPaymentAmount,
                PayoutStatus.CALCULATED.name(),
                createdAt
        );

        return requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
    }

    @Transactional(readOnly = true)
    public List<PayoutResponse> listByPolicyNumber(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        return payoutMapper.findByPolicyNumber(contract.getPolicyNumber());
    }

    @Transactional(readOnly = true)
    public PayoutResponse findByPayoutId(String policyNumber, String payoutId) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        return requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
    }

    @Transactional
    public PayoutResponse approve(String policyNumber, String payoutId, PayoutApproveRequest request) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        PayoutResponse existing = requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
        if (existing.getPayoutStatus() != PayoutStatus.CALCULATED) {
            throw new IllegalArgumentException(
                    "Payout can be approved only from CALCULATED. Current: " + existing.getPayoutStatus());
        }

        String processor = requireText(request.getProcessor(), "processor");
        int updated = payoutMapper.updateStatusToApproved(
                payoutId,
                PayoutStatus.CALCULATED.name(),
                PayoutStatus.APPROVED.name(),
                processor,
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Payout approval failed (concurrent modification): " + payoutId);
        }
        return requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
    }

    @Transactional
    public PayoutResponse pay(String policyNumber, String payoutId) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        PayoutResponse existing = requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
        if (existing.getPayoutStatus() != PayoutStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "Payout can be paid only from APPROVED. Current: " + existing.getPayoutStatus());
        }

        int updated = payoutMapper.updateStatusToPaid(
                payoutId,
                PayoutStatus.APPROVED.name(),
                PayoutStatus.PAID.name(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Payout payment failed (concurrent modification): " + payoutId);
        }
        return requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
    }

    @Transactional
    public PayoutResponse cancel(String policyNumber, String payoutId) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        PayoutResponse existing = requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
        if (existing.getPayoutStatus() == PayoutStatus.PAID) {
            throw new IllegalArgumentException("Paid payout cannot be cancelled: " + payoutId);
        }
        if (existing.getPayoutStatus() == PayoutStatus.CANCELLED) {
            throw new IllegalArgumentException("Payout is already cancelled: " + payoutId);
        }

        int updated = payoutMapper.updateStatusToCancelled(
                payoutId,
                PayoutStatus.CANCELLED.name(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Payout cancellation failed (concurrent modification): " + payoutId);
        }
        return requirePayoutOwnedBy(payoutId, contract.getPolicyNumber());
    }

    private PayoutResponse requirePayoutOwnedBy(String payoutId, String policyNumber) {
        String normalizedPayoutId = requireText(payoutId, "payoutId");
        PayoutResponse payout = payoutMapper.findByPayoutId(normalizedPayoutId);
        if (payout == null) {
            throw new NoSuchElementException("Payout not found: " + normalizedPayoutId);
        }
        if (!policyNumber.equals(payout.getPolicyNumber())) {
            throw new IllegalArgumentException(
                    "Payout " + normalizedPayoutId + " does not belong to contract " + policyNumber);
        }
        return payout;
    }

    private BigDecimal resolveRefundRate(CalculationBasis basis) {
        return switch (basis) {
            case MATURITY_REFUND -> RATE_MATURITY_REFUND;
            case SURRENDER -> RATE_SURRENDER;
            case MID_SURRENDER -> RATE_MID_SURRENDER;
            case DIVIDEND -> RATE_DIVIDEND;
        };
    }

    private String generatePayoutId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "PAY-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
