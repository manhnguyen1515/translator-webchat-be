package com.translator.webchat.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class SendEmailOtp {

    private JavaMailSender mailSender;
    private final Environment env;
    private final GenerateOTP generateOTP;

    @Autowired
    public SendEmailOtp(JavaMailSender mailSender, Environment env, GenerateOTP generateOTP) {
        this.mailSender = mailSender;
        this.env = env;
        this.generateOTP = generateOTP;
    }

    public int SendOtpEmail(String email) {
        String otp = generateOTP.generateOTP(email);
        MimeMessage mail = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(email);
            String fromEmail = env.getProperty("spring.mail.username");
            if (fromEmail != null) {
                helper.setFrom(fromEmail);
            }
            helper.setSubject("Your OTP for change forgotten password");

            ClassPathResource resource = new ClassPathResource("templates/otp_template.html");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));
            content = content.replace("${otp}", otp);

            helper.setText(content, true); // true indicates HTML content
            mailSender.send(mail);
            return 0; // Email sent successfully
        } catch (MessagingException e) {
            e.printStackTrace();
            return 1; // Failed to send email
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}