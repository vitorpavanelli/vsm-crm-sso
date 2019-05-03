package br.com.vsm.crm.sso.config.security.filter;

import br.com.vsm.crm.sso.config.security.utils.ApplicationUser;
import br.com.vsm.crm.sso.config.security.utils.SecurityConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
        private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            ApplicationUser creds = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    creds.getUsername(),
                    creds.getPassword()
            ));

        } catch (IOException e) {
            logger.error("Error parsing credentials", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = (String) authResult.getPrincipal();

        LocalDate localDate = LocalDate.now().plusDays(45);
        if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            localDate = localDate.plusDays(2);

        } else if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            localDate = localDate.plusDays(1);
        }

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(java.sql.Date.valueOf(localDate))
                .setId(UUID.randomUUID().toString());

        SecretKey key = Keys.hmacShaKeyFor(SecurityConstraints.SECRET.getBytes("UTF-8"));
        String token = jwtBuilder.signWith(key, SignatureAlgorithm.HS256).compact();

        response.addHeader(SecurityConstraints.HEADER_AUTH, SecurityConstraints.TOKEN_PREFIX + token);
    }
}
