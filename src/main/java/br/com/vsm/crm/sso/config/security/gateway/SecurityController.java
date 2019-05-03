package br.com.vsm.crm.sso.config.security.gateway;

import br.com.vsm.crm.sso.api.user.repository.Access;
import br.com.vsm.crm.sso.api.user.repository.User;
import br.com.vsm.crm.sso.api.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/gateway")
public class SecurityController {
    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private UserService service;

    @RequestMapping(value = "/security-check", method = RequestMethod.GET)
    private ResponseEntity<AccessGrants> checkAuthentication() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = service.findOneByLoginWithAccess(authentication.getPrincipal().toString());
            AccessGrants response = new AccessGrants();
            response.setLogin(user.getLogin());
            response.setAdmin(user.isAdmin());

            Set<String> accesses = new HashSet<>();
            for (Access access : user.getAccesses()) {
                accesses.add("/api"+ access.getResource().getPath());
            }

            response.setAccesses(accesses);

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
           logger.error("Login was not provided", e);
        }

        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
