package com.webapp.FinTurn.listener;

import com.webapp.FinTurn.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AuthenticationFailureListener {
    private LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
