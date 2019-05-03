package br.com.vsm.crm.sso.config.security.utils;

public class SecurityConstraints {
    public static final String SECRET = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
    public static final long EXPIRATION_TIME_5_DAYS = 432_000_000; // 5 days
    public static final long EXPIRATION_TIME_1_DAY = 86_400_000; // 1 day
    public static final long EXPIRATION_TIME_2_DAYS = 172_800_000; // 2 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_AUTH_REFRESHED = "";
}
