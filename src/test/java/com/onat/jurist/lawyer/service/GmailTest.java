package com.onat.jurist.lawyer.service;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.SimpleMailMessage;
import java.util.Properties;

public class GmailTest {
    public static void main(String[] args) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("leggy.services@gmail.com");
        mailSender.setPassword("ebvu ohkc orel olwo");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true");


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("leggy.services@gmail.com");
        message.setTo("leggy.services@gmail.com");
        message.setSubject("Test Email");
        message.setText("Hello from Spring Mail!");

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
