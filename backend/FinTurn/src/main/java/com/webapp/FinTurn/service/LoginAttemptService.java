package com.webapp.FinTurn.service;

import org.springframework.stereotype.Service;

public interface LoginAttemptService {

    void evictUserFromLoginAttemptCache(String username);

    void addUserToLoginAttemptCache(String username);

    boolean hasExceededMaxAttempts(String username);
}
