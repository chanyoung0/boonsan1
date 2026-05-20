package db.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

// MyBatis SqlSessionFactory 공급자 — 단일 세션 진입점
public final class MyBatisSessionFactory {

    private static final String CONFIG_RESOURCE = "mybatis-config.xml";
    private static final String DB_PROPERTIES_FILE = "db.properties";
    private static volatile SqlSessionFactory factory;

    private MyBatisSessionFactory() {}

    // 자동 커밋 세션 열기
    public static SqlSession openSession() {
        return getFactory().openSession(true);
    }

    public static SqlSession openSession(boolean autoCommit) {
        return getFactory().openSession(autoCommit);
    }

    private static SqlSessionFactory getFactory() {
        SqlSessionFactory local = factory;
        if (local == null) {
            synchronized (MyBatisSessionFactory.class) {
                local = factory;
                if (local == null) {
                    factory = local = buildFactory();
                }
            }
        }
        return local;
    }

    private static SqlSessionFactory buildFactory() {
        Properties dbProperties = loadDbProperties();
        try (InputStream in = Resources.getResourceAsStream(CONFIG_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException(CONFIG_RESOURCE + "을 클래스패스에서 찾을 수 없습니다.");
            }
            return new SqlSessionFactoryBuilder().build(in, dbProperties);
        } catch (IOException e) {
            throw new UncheckedIOException(CONFIG_RESOURCE + " 로드 실패", e);
        }
    }

    private static Properties loadDbProperties() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(DB_PROPERTIES_FILE);
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            properties.load(reader);
            applyBomFallback(properties, "db.url", "db.user", "db.password");
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(DB_PROPERTIES_FILE + " 파일을 읽을 수 없습니다.", e);
        }
    }

    private static void applyBomFallback(Properties properties, String... keys) {
        for (String key : keys) {
            if (properties.getProperty(key) == null) {
                String bomKey = "﻿" + key;
                String value = properties.getProperty(bomKey);
                if (value != null) {
                    properties.setProperty(key, value);
                }
            }
        }
    }
}
