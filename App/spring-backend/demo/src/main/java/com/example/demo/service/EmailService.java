package com.example.demo.service;

import com.example.demo.logger.LogLevel;
import com.example.demo.logger.LogManager;
import com.example.demo.logger.LogMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("emailTaskExecutor")
    public void sendVerificationEmail(String recipientEmail, String verificationUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin: 0; padding: 0; background-color: #0b0c10; font-family: 'Courier New', Courier, monospace;">
                    <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #0b0c10; padding: 60px 20px;">
                        <tr>
                            <td align="center">
                                <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="max-width: 480px; background-color: #12141d; border: 1px solid #1f2438; border-radius: 12px; padding: 40px 32px; text-align: center;">
                                    <tr>
                                        <td>
                                            <img src="cid:appLogo" alt="Deltaspace Logo" width="280" style="width: 280px; max-width: 90%%; height: auto; margin: 0 auto 28px auto; display: block;" />

                                            <h1 style="color: #ffffff; font-family: 'Courier New', Courier, monospace; font-size: 20px; font-weight: bold; letter-spacing: 1.5px; text-transform: uppercase; margin: 0 0 12px 0;">
                                                Verify Your Email
                                            </h1>

                                            <p style="color: #9ba1b6; font-family: 'Courier New', Courier, monospace; font-size: 13px; line-height: 1.6; letter-spacing: 0.5px; margin: 0 0 28px 0;">
                                                Thank you for signing up. Please confirm your email address to activate your account:
                                            </p>

                                            <table border="0" cellspacing="0" cellpadding="0" style="margin: 0 auto 28px auto;">
                                                <tr>
                                                    <td align="center" style="border-radius: 9999px; background-color: #d902ee;">
                                                        <a href="%s" target="_blank" style="display: inline-block; padding: 14px 40px; font-family: 'Courier New', Courier, monospace; font-size: 13px; font-weight: bold; color: #ffffff; text-decoration: none; border-radius: 9999px; letter-spacing: 1px; text-transform: uppercase;">
                                                            Verify Email Address
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="color: #9ba1b6; font-family: 'Courier New', Courier, monospace; font-size: 12px; letter-spacing: 0.5px; margin: 0 0 6px 0;">Or paste this link into your browser:</p>
                                            <p style="margin: 0 0 24px 0;">
                                                <a href="%s" target="_blank" style="color: #00b4d8; font-family: 'Courier New', Courier, monospace; font-size: 11px; word-break: break-all; text-decoration: underline;">%s</a>
                                            </p>

                                            <p style="color: #555b70; font-family: 'Courier New', Courier, monospace; font-size: 11px; margin: 0; border-top: 1px solid #1f2438; padding-top: 20px;">
                                                This link expires in 24 hours.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
            """.formatted(verificationUrl, verificationUrl, verificationUrl);

            String plainText = """
                Thank you for signing up for Deltaspace!

                Please verify your email address by visiting this link:
                %s

                This link will expire in 24 hours.
                """.formatted(verificationUrl);

            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Complete Your Registration - Deltaspace");

            helper.setText(plainText, htmlContent);

            ClassPathResource logoResource = new ClassPathResource("images/logo.png");
            if (logoResource.exists()) {
                helper.addInline("appLogo", logoResource, "image/png");
            }

            mailSender.send(mimeMessage);
            LogManager.getInstance().addMessage(new LogMessage(LogLevel.INFO, "Verification email dispatched asynchronously to " + recipientEmail));

        } catch (MessagingException e) {
            LogManager.getInstance().addMessage(new LogMessage(LogLevel.SEVERE, "Failed to send async verification email to " + recipientEmail + ": " + e.getMessage()));
        }
    }
}