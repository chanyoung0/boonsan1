package config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "claim.mapper",
        "underwriting.mapper",
        "dashboard.mapper",
        "product.mapper",
        "contract.mapper"
})
public class MyBatisConfig {
}
