package com.boonsan.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "com.boonsan.claim.mapper",
        "com.boonsan.underwriting.mapper",
        "com.boonsan.dashboard.mapper",
        "com.boonsan.product.mapper",
        "com.boonsan.contract.mapper"
})
public class MyBatisConfig {
}
