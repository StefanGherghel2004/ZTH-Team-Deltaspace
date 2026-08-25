package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String recipientEmail, String verificationUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>Verify Your Email</h2>
                    <p>Thank you for signing up for the Deltaspace platform! Please confirm your email address by clicking the link below:</p>
                    <p style="margin: 20px 0;">
                        <a href="%s" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Verify Email Address
                        </a>
                    </p>
                    <p>Or paste this link into your browser:</p>
                    <p><a href="%s">%s</a></p>
                    <p>This link will expire in 24 hours.</p>
                </div>
            """.formatted(verificationUrl, verificationUrl, verificationUrl);

            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Complete Your Registration");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}