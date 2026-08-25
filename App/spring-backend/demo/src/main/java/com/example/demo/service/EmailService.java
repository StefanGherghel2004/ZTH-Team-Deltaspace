package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin: 0; padding: 0; background-color: #0b0c10; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                    <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #0b0c10; padding: 60px 20px;">
                        <tr>
                            <td align="center">
                                <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="max-width: 600px; background-color: #12141d; border: 1px solid #1f2438; border-radius: 16px; padding: 48px 40px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.5);">
                                    <tr>
                                        <td>
                                            <!-- Logo Header -->
                                            <img src="cid:appLogo" alt="Deltaspace Logo" width="320" style="width: 320px; max-width: 95%%; height: auto; margin: 0 auto 36px auto; display: block;" />

                                            <h1 style="color: #ffffff; font-size: 28px; font-weight: 700; margin: 0 0 16px 0; letter-spacing: -0.5px;">Verify Your Email</h1>

                                            <p style="color: #9ba1b6; font-size: 16px; line-height: 1.6; margin: 0 0 36px 0;">
                                                Thank you for signing up for the Deltaspace platform! Please confirm your email address by clicking the button below:
                                            </p>

                                            <!-- Deltaspace Gradient Button -->
                                            <table border="0" cellspacing="0" cellpadding="0" style="margin: 0 auto 36px auto;">
                                                <tr>
                                                    <td align="center" style="border-radius: 9999px; background: #9c27b0; background: linear-gradient(90deg, #d902ee 0%%, #7b2cbf 50%%, #00b4d8 100%%); box-shadow: 0 0 20px rgba(217, 2, 238, 0.35);">
                                                        <a href="%s" target="_blank" style="display: inline-block; padding: 16px 48px; font-size: 16px; font-weight: 700; color: #ffffff; text-decoration: none; border-radius: 9999px; letter-spacing: 0.5px; text-transform: uppercase;">
                                                            Verify Email Address
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="color: #9ba1b6; font-size: 14px; margin: 0 0 8px 0;">Or paste this link into your browser:</p>
                                            <p style="margin: 0 0 32px 0;">
                                                <a href="%s" target="_blank" style="color: #00b4d8; font-size: 13px; word-break: break-all; text-decoration: underline;">%s</a>
                                            </p>

                                            <p style="color: #555b70; font-size: 13px; margin: 0; border-top: 1px solid #1f2438; padding-top: 24px;">
                                                This link will expire in 24 hours.
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
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}