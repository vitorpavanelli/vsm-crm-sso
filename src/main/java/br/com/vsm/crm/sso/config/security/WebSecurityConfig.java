package br.com.vsm.crm.sso.config.security;

import br.com.vsm.crm.sso.api.user.repository.User;
import br.com.vsm.crm.sso.api.user.service.UserService;
import br.com.vsm.crm.sso.config.security.filter.JWTAuthenticationFilter;
import br.com.vsm.crm.sso.config.security.filter.JWTAuthorizationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

        @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors()

                .and()
                .csrf()
                    .ignoringAntMatchers("/api/admin/user/create")
                    .ignoringAntMatchers("/login")
                    .ignoringAntMatchers("/logout")
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .authorizeRequests()
                    .antMatchers("/api/admin/user/create").permitAll()
                    .anyRequest().authenticated()

                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .clearAuthentication(true)
                    .logoutSuccessHandler((request, response, authentication) -> {
                            Map<String, String> result = new HashMap<>();
                            result.put( "result", "success" );
                            response.setContentType("application/json");
                            response.getWriter().write( objectMapper.writeValueAsString( result ) );
                            response.setStatus(HttpServletResponse.SC_OK);
                    })

                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), userService))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(databaseAuthenticationProvider());
    }

    @Bean
    protected AuthenticationProvider databaseAuthenticationProvider() {
        return new AuthenticationProvider() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String login = (String) authentication.getPrincipal();
                String password = (String) authentication.getCredentials();
                if (!StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {
                    try {
                       User user = userService.findOneByLogin(login);

                        if (user != null) {
                            if (!user.isActive()) {
                                logger.info("User is not active");
                                return null;
                            }

                            if (user.isLocked()) {
                                logger.info("User is locked. Reason: " + user.getLockingReason());
                                return null;
                            }
                        }

                        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                            Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
                            return new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(), authorities);
                        }

                    } catch (Exception e) {
                        logger.error("Error persisting or finding user from Database", e);
                        return null;
                    }

                }

                logger.info("User credentials not correctly supplied");
                return null;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        };
    }
}
