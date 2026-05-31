package com.boonsan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "common",
        "config",
        "exception",
        "claim",
        "service",
        "model",
        "enums"
})
public class BoonsanApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoonsanApplication.class, args);
    }
}
