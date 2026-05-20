package db.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;

// MyBatis 설정/매퍼 XML 파싱 검증용 스모크 테스트 — DB 연결 없이도 SqlSessionFactory 생성만 확인한다
public class MyBatisSmokeTest {

    public static void main(String[] args) throws Exception {
        Properties dummy = new Properties();
        dummy.setProperty("db.url", "jdbc:postgresql://localhost:5432/none");
        dummy.setProperty("db.user", "none");
        dummy.setProperty("db.password", "none");
        try (InputStream in = Resources.getResourceAsStream("mybatis-config.xml")) {
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in, dummy);
            Configuration cfg = factory.getConfiguration();
            System.out.println("Loaded mappers: " + cfg.getMapperRegistry().getMappers().size());
            for (Class<?> mapperType : cfg.getMapperRegistry().getMappers()) {
                System.out.println("  - " + mapperType.getName());
            }
            System.out.println("MappedStatements: " + cfg.getMappedStatementNames().size());
        }
        System.out.println("SMOKE_OK");
    }
}
