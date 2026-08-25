package com.example.demo.aspect;

import com.example.demo.exception.AccountNotVerifiedException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RequireVerifiedAspect {

    private final UserService userService;

    @Before("@annotation(com.example.demo.annotation.RequireVerified) || @within(com.example.demo.annotation.RequireVerified)")
    public void ensureUserIsVerified() {
        User user = userService.getAuthenticatedUser();

        if (user == null) {
            throw new AccountNotVerifiedException("User is not authenticated.");
        }

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException("Action not permitted. Please verify your email first.");
        }
    }
}