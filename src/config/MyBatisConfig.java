package config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "claim.mapper",
        "underwriting.mapper"
})
public class MyBatisConfig {
}
