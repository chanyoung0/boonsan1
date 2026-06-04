package com.boonsan.global.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "com.boonsan.domain.accident.mapper",
        "com.boonsan.domain.underwriting.mapper",
        "com.boonsan.domain.dashboard.mapper",
        "com.boonsan.domain.product.mapper",
        "com.boonsan.domain.contract.mapper"
})
public class MyBatisConfig {
}
