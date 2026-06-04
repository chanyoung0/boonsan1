package com.boonsan.domain.product.mapper;

import com.boonsan.domain.model.insurance.Authorization;
import org.apache.ibatis.annotations.Param;
import com.boonsan.domain.product.dto.AuthorizationEligibilityResponse;

public interface AuthorizationMapper {

    AuthorizationEligibilityResponse findEligibilityByProductCode(@Param("productCode") String productCode);

    Authorization findLatestByProductCode(@Param("productCode") String productCode);

    void insertAuthorization(Authorization authorization);

    int updateAuthorizationStatus(@Param("requestId") String requestId,
                                  @Param("authorizationStatus") String authorizationStatus,
                                  @Param("isApproved") boolean isApproved,
                                  @Param("approvedAt") java.time.LocalDateTime approvedAt,
                                  @Param("revisionRequest") String revisionRequest,
                                  @Param("updatedAt") java.time.LocalDateTime updatedAt);
}
