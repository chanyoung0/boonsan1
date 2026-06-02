package config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"claim.mapper", "contract.mapper"})
public class MyBatisConfig {
}
