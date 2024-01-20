package com.webapp.FinTurn.constant;

public class CorsConstant {
    public static final boolean ALLOW_CREDENTIALS = true;
    public static final String ALLOWED_ORIGIN = "http://localhost:4200";
    public static final String[] ALLOWED_HEADERS_LIST = {
            "Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept",
            "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
            "Access-Control-Request-Method", "Access-Control-Request-Headers" };
    public static final String[] EXPOSED_HEADERS_LIST = {
            "Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials" };
    public static final String[] ALLOWED_METHODS_LIST = {
            "GET", "POST", "PUT", "DELETE", "OPTIONS" };
    public static final String PATTERN_ALL = "/**";
}
