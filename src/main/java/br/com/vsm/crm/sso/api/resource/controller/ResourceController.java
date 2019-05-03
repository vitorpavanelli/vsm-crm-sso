package br.com.vsm.crm.sso.api.resource.controller;

import br.com.vsm.crm.sso.api.HttpCustomResponse;
import br.com.vsm.crm.sso.api.HttpCustomResponseStatus;
import br.com.vsm.crm.sso.api.resource.repository.Resource;
import br.com.vsm.crm.sso.api.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin/resource")
public class ResourceController {

    @Autowired
    private ResourceService service;

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    private Iterable<Resource> findAll() {
        return service.findAll();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    private ResponseEntity<HttpCustomResponse> save(@RequestBody Resource resource) {
        service.save(resource);

        HttpCustomResponse response = new HttpCustomResponse();
        response.setStatus(HttpCustomResponseStatus.SUCCESS);
        response.setMessage("Resource Saved");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    private ResponseEntity<HttpCustomResponse> delete(@PathVariable Long id) {
        service.delete(id);

        HttpCustomResponse response = new HttpCustomResponse();
        response.setStatus(HttpCustomResponseStatus.SUCCESS);
        response.setMessage("Resource Removed!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
