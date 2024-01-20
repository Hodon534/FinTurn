package com.webapp.FinTurn.listener;

import com.webapp.FinTurn.domain.UserPrincipal;
import com.webapp.FinTurn.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
