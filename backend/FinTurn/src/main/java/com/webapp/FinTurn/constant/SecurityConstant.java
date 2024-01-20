package com.webapp.FinTurn.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_000; // 5 days expressed in milliseconds
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER = "Jwt_Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String COMPANY_NAME = "FinTurn";
    public static final String COMPANY_NAME_ADMINISTRATION = "FinTurn Administration";
    public static final String AUTHORITIES = "authorities"; // ??
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "**"
    };
    public static final String[] USER_URLS = {

    };
    public static final String[] MANAGER_URLS = {

    };
    public static final String[] ADMIN_URLS = {

    };
    public static final String[] SUPER_ADMIN_URLS = {

    };
}
