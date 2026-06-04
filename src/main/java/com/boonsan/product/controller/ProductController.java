package com.boonsan.product.controller;

import com.boonsan.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.boonsan.product.dto.ProductDesignRequest;
import com.boonsan.product.dto.ProductResponse;
import com.boonsan.product.service.ProductDesignApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductDesignApplicationService productDesignApplicationService;

    public ProductController(ProductDesignApplicationService productDesignApplicationService) {
        this.productDesignApplicationService = productDesignApplicationService;
    }

    @PostMapping
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductDesignRequest request) {
        ProductResponse response = productDesignApplicationService.create(request);
        return ApiResponse.success(response, "Product designed");
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> findAll() {
        List<ProductResponse> response = productDesignApplicationService.findAll();
        return ApiResponse.success(response, "Products found");
    }

    @GetMapping("/{productCode}")
    public ApiResponse<ProductResponse> findByProductCode(@PathVariable String productCode) {
        ProductResponse response = productDesignApplicationService.findByProductCode(productCode);
        return ApiResponse.success(response, "Product found");
    }
}
