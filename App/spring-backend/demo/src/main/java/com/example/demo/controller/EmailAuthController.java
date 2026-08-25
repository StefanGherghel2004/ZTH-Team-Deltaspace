package com.example.demo.controller;

import com.example.demo.annotation.RateLimit;
import com.example.demo.logger.Logger;
import com.example.demo.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email-auth")
@RequiredArgsConstructor
public class EmailAuthController {

    private final AuthService authService;

    @RateLimit(requests = 20)
    @GetMapping(value = "/verify", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmailToken(token);
            Logger.info("Email verified successfully for token: %s", token);
            return ResponseEntity.ok(renderSuccessPage());
        } catch (Exception ex) {
            Logger.warning("Email verification failed: %s", ex.getMessage());
            return ResponseEntity.badRequest().body(renderErrorPage(ex.getMessage()));
        }
    }

    private String renderSuccessPage() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Verified - Deltaspace</title>
                <style>
                    * { 
                        margin: 0; 
                        padding: 0; 
                        box-sizing: border-box; 
                        font-family: 'Courier New', Courier, monospace; 
                    }
                    body { 
                        min-height: 100vh; 
                        display: flex; 
                        align-items: center; 
                        justify-content: center; 
                        background: #0b0c10; 
                        color: #9ba1b6; 
                        padding: 20px; 
                    }
                    .card { 
                        background: #12141d; 
                        border: 1px solid #1f2438; 
                        border-radius: 12px; 
                        padding: 40px 32px; 
                        max-width: 440px; 
                        width: 100%; 
                        text-align: center; 
                    }
                    .icon { 
                        font-size: 32px; 
                        color: #00b4d8; 
                        margin-bottom: 16px; 
                        font-weight: bold; 
                    }
                    h1 { 
                        font-size: 20px; 
                        font-weight: bold; 
                        letter-spacing: 1.5px; 
                        margin-bottom: 12px; 
                        color: #ffffff; 
                        text-transform: uppercase; 
                    }
                    p { 
                        font-size: 13px; 
                        line-height: 1.6; 
                        letter-spacing: 0.5px; 
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="icon">[ ✓ ]</div>
                    <h1>Email Verified</h1>
                    <p>Your account is now activated. You can close this window and return to Deltaspace.</p>
                </div>
            </body>
            </html>
            """;
    }

    private String renderErrorPage(String errorMessage) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verification Failed - Deltaspace</title>
                <style>
                    * { 
                        margin: 0; 
                        padding: 0; 
                        box-sizing: border-box; 
                        font-family: 'Courier New', Courier, monospace; 
                    }
                    body { 
                        min-height: 100vh; 
                        display: flex; 
                        align-items: center; 
                        justify-content: center; 
                        background: #0b0c10; 
                        color: #9ba1b6; 
                        padding: 20px; 
                    }
                    .card { 
                        background: #12141d; 
                        border: 1px solid #1f2438; 
                        border-radius: 12px; 
                        padding: 40px 32px; 
                        max-width: 440px; 
                        width: 100%; 
                        text-align: center; 
                    }
                    .icon { 
                        font-size: 32px; 
                        color: #d902ee; 
                        margin-bottom: 16px; 
                        font-weight: bold; 
                    }
                    h1 { 
                        font-size: 20px; 
                        font-weight: bold; 
                        letter-spacing: 1.5px; 
                        margin-bottom: 12px; 
                        color: #ffffff; 
                        text-transform: uppercase; 
                    }
                    p { 
                        font-size: 13px; 
                        line-height: 1.6; 
                        letter-spacing: 0.5px; 
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="icon">[ ✕ ]</div>
                    <h1>Verification Failed</h1>
                    <p>%s</p>
                </div>
            </body>
            </html>
            """.formatted(errorMessage != null ? errorMessage : "This link is invalid or has expired.");
    }
}