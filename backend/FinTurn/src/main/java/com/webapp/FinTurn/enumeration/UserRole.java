package com.webapp.FinTurn.enumeration;

import static com.webapp.FinTurn.constant.AuthoritiesConstant.*;

public enum UserRole {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;

    UserRole(String ... authorities) {
        this.authorities = authorities;
    }
    public String[] getAuthorities() {
        return authorities;
    }
}
