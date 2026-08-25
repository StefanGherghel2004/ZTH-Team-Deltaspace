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
                    * { margin: 0; padding: 0; box-sizing: border-box; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
                    body { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #0e1117; color: #e6edf3; padding: 20px; }
                    .card { background: #161b22; border: 1px solid #30363d; border-radius: 16px; padding: 48px 32px; max-width: 440px; width: 100%; text-align: center; box-shadow: 0 12px 32px rgba(0,0,0,0.4); }
                    .icon-badge { width: 72px; height: 72px; background: rgba(35, 134, 54, 0.15); border: 2px solid #238636; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 24px; color: #2ea043; font-size: 32px; }
                    h1 { font-size: 24px; font-weight: 700; margin-bottom: 12px; color: #ffffff; }
                    p { font-size: 14px; color: #8b949e; line-height: 1.6; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="icon-badge">✓</div>
                    <h1>Email Verified!</h1>
                    <p>Your account is now fully activated. You can close this tab and return to the application.</p>
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
                    * { margin: 0; padding: 0; box-sizing: border-box; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
                    body { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #0e1117; color: #e6edf3; padding: 20px; }
                    .card { background: #161b22; border: 1px solid #30363d; border-radius: 16px; padding: 48px 32px; max-width: 440px; width: 100%; text-align: center; box-shadow: 0 12px 32px rgba(0,0,0,0.4); }
                    .icon-badge { width: 72px; height: 72px; background: rgba(218, 54, 51, 0.15); border: 2px solid #da3633; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 24px; color: #f85149; font-size: 32px; }
                    h1 { font-size: 24px; font-weight: 700; margin-bottom: 12px; color: #ffffff; }
                    p { font-size: 14px; color: #8b949e; line-height: 1.6; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="icon-badge">✕</div>
                    <h1>Verification Failed</h1>
                    <p>%s</p>
                </div>
            </body>
            </html>
            """.formatted(errorMessage != null ? errorMessage : "This link is invalid or has already expired.");
    }
}