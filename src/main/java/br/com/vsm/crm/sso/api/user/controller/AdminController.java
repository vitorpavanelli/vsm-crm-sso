package br.com.vsm.crm.sso.api.user.controller;

import br.com.vsm.crm.sso.api.HttpCustomResponse;
import br.com.vsm.crm.sso.api.HttpCustomResponseStatus;
import br.com.vsm.crm.sso.api.user.repository.User;
import br.com.vsm.crm.sso.api.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin/user", headers = "Accept=application/json")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private Iterable<User> findAll() {
        return service.findAll();
    }

    @RequestMapping(value = "/list/{name}", method = RequestMethod.GET)
    private Iterable<User> findAllByName(@PathVariable String name) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = service.findOneByLoginWithAccess((String) authentication.getPrincipal());
            if (user.isAdmin()) {
                return service.findAllByName(name);

            } else {
                return service.findAllByNameExcludingAdmin(name);
            }

        } catch (Exception e) {
            logger.error("User ID was not provided", e);
        }

        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private User findOneByIdWithAccess(@PathVariable Long id) {
        try {

            return service.findOneByIdWithAccess(id);

        } catch (Exception e) {
            logger.error("User ID was not provided", e);
        }

        return null;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    private ResponseEntity<HttpCustomResponse> save(@RequestBody User user) {
        HttpCustomResponse response = new HttpCustomResponse();

        try {
            Long userId = service.save(user);

            response.setStatus(HttpCustomResponseStatus.SUCCESS);
            response.setUserId(userId);
            response.setMessage("Resource Saved!");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("User ID was not provided", e);
        }

        response.setStatus(HttpCustomResponseStatus.ERROR);
        response.setMessage("User cannot be null");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    private void create(@RequestBody User user) {
        try {
            service.save(user);

        } catch (Exception e) {
            logger.error("User ID was not provided", e);
        }

    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    private ResponseEntity<HttpCustomResponse> delete(@PathVariable Long id) {

        // TODO

        HttpCustomResponse response = new HttpCustomResponse();
        response.setStatus(HttpCustomResponseStatus.SUCCESS);
        response.setMessage("Resource Removed!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/check/{login}", method = RequestMethod.GET)
    private HttpCustomResponse findOne(@PathVariable String login) {
        HttpCustomResponse response = new HttpCustomResponse();

        try {
            if (service.findOneByLogin(login) != null) {
                response.setMessage("OK");
                response.setStatus(HttpCustomResponseStatus.SUCCESS);

                return response;
            }

        } catch (Exception e) {
            logger.error("User ID cannot be empty", e);
        }

        response.setMessage("ERROR");
        response.setStatus(HttpCustomResponseStatus.ERROR);

        return response;
    }
}
