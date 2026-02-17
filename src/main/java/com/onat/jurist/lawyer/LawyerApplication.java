package com.onat.jurist.lawyer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LawyerApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .directory("C:/Users/ranim/OneDrive/Bureau/lawyer")
                .ignoreIfMissing()
                .load();

        // Set environment variables as system properties
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME", ""));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD", ""));
        System.out.println("MAIL_USERNAME=" + dotenv.get("MAIL_USERNAME"));
        System.out.println("MAIL_PASSWORD=" + (dotenv.get("MAIL_PASSWORD") != null ? "****" : null));

        SpringApplication.run(LawyerApplication.class, args);

    }



}
