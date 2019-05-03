package br.com.vsm.crm.sso.config.security.filter;

import br.com.vsm.crm.sso.api.user.repository.Access;
import br.com.vsm.crm.sso.api.user.repository.User;
import br.com.vsm.crm.sso.api.user.service.UserService;
import br.com.vsm.crm.sso.config.security.utils.SecurityConstraints;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    private UserService service;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, UserService service) {
        super(authenticationManager);
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(SecurityConstraints.HEADER_AUTH);
        if (token == null || !token.startsWith(SecurityConstraints.TOKEN_PREFIX)) {
            response.setHeader(SecurityConstraints.HEADER_AUTH, "");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            SecurityContextHolder.getContext().setAuthentication(null);
            chain.doFilter(request, response);
            return;
        }

        try {
            token = token.replace(SecurityConstraints.TOKEN_PREFIX, "").trim();
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstraints.SECRET.getBytes("UTF-8"));
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            String login = claims.getSubject();
            LocalDate tokenDate = new Date(claims.getExpiration().getTime()).toLocalDate();
            LocalDate currentLocalDate = LocalDate.now();
            if (currentLocalDate.isAfter(tokenDate)) {
                response.setHeader(SecurityConstraints.HEADER_AUTH, "");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                SecurityContextHolder.getContext().setAuthentication(null);
                chain.doFilter(request, response);
                return;
            }


            if (Duration.between(currentLocalDate.atStartOfDay(), tokenDate.atStartOfDay()).toDays() <= 15) {
                LocalDate localDate = LocalDate.now().plusDays(45);
                if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    localDate = localDate.plusDays(2);

                } else if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    localDate = localDate.plusDays(1);
                }

                JwtBuilder jwtBuilder = Jwts.builder()
                        .setSubject(login)
                        .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                        .setExpiration(java.sql.Date.valueOf(localDate))
                        .setId(UUID.randomUUID().toString());

                token = jwtBuilder.signWith(key, SignatureAlgorithm.HS256).compact();
            }

            if (login != null) {
                User authenticatedUser = service.findOneByLoginWithAccess(login);
                if (authenticatedUser != null) {
                    String role = authenticatedUser.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";
                    Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role));
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login, null, authorities));
                    if (!hasAccess(authenticatedUser, request)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    }
                    response.setHeader(SecurityConstraints.HEADER_AUTH, SecurityConstraints.TOKEN_PREFIX + token);
                    chain.doFilter(request, response);
                    return;
                }
            }

        } catch (JwtException | UnsupportedEncodingException e) {
            logger.error("Token provided does not match", e);

        } catch (Exception e) {
            logger.error("Login was not provided", e);
        }

        SecurityContextHolder.getContext().setAuthentication(null);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader(SecurityConstraints.HEADER_AUTH, "");
        chain.doFilter(request, response);
    }

    private boolean hasAccess(User authenticatedUser, HttpServletRequest request) {
        String _URI = request.getRequestURI();
        if (authenticatedUser.isLocked() || !authenticatedUser.isActive()) {
            return false;
        }

        if (_URI.equals("/api/gateway/security-check")) {
            return true;
        }

        if (_URI.equals("/api/user/authenticated")) {
            return true;
        }

        if (_URI.contains("admin")) {
            if (authenticatedUser.isAdmin()) {
                return true;
            }

            final Access access = authenticatedUser.getAccesses().stream()
                    .filter(item -> "/admin/access".equals(item.getResource().getPath()))
                    .findAny()
                    .orElse(null);

            if (access != null) {
                switch (request.getMethod()) {
                    case "POST":
                    case "DELETE":
                        if (access.isCanWrite()) {
                            return true;
                        }
                    case "GET":
                        return true;

                }
            }
        }

        return false;
    }
}
