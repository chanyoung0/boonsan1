package com.boonsan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.boonsan",
        "common",
        "config",
        "exception",
        "dashboard",
        "claim",
        "contract",
        "product",
        "underwriting",
        "service",
        "model",
        "enums"
})
public class BoonsanApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoonsanApplication.class, args);
    }
}
