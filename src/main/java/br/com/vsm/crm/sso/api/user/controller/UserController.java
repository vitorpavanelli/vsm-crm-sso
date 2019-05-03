package br.com.vsm.crm.sso.api.user.controller;

import br.com.vsm.crm.sso.api.HttpAuthenticatedUserResponse;
import br.com.vsm.crm.sso.api.resource.repository.Resource;
import br.com.vsm.crm.sso.api.resource.service.ResourceService;
import br.com.vsm.crm.sso.api.user.repository.Access;
import br.com.vsm.crm.sso.api.user.repository.User;
import br.com.vsm.crm.sso.api.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/user", headers = "Accept=application/json")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService service;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/authenticated", method = RequestMethod.GET)
    private HttpAuthenticatedUserResponse findOneByIdWithAccess() {
        try {
            HttpAuthenticatedUserResponse response = new HttpAuthenticatedUserResponse();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            User user = service.findOneByLoginWithAccess((String) authentication.getPrincipal());
            response.setUser(user);

            Set<Long> accesses = new HashSet<>();

            if (user.isAdmin()) {
                response.setResources(resourceService.findAll());

            } else {
                for (Access access : user.getAccesses()) {
                    accesses.add(access.getResource().getId());
                }

                // TODO - Figure out why children is not being filtered
                if (accesses.size() > 0) {
                    Iterable<Resource> resources = new ArrayList<>();
                    for (Resource resource : resourceService.findAll(accesses)) {
                        if (resource.getParent() == null) {
                            ((ArrayList<Resource>) resources).add(resource);
                        }
                    }

                    response.setResources(resources);
                }
            }

            return response;

        } catch (Exception e) {
            logger.error("User ID cannot be empty", e);
        }

        return null;
    }
}
