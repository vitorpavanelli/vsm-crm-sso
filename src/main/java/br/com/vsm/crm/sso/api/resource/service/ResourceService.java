package br.com.vsm.crm.sso.api.resource.service;

import br.com.vsm.crm.sso.api.resource.repository.Resource;
import br.com.vsm.crm.sso.api.resource.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository repository;

    public Iterable<Resource> findAll() {
        return repository.findAll();
    }

    public Iterable<Resource> findAll(Set<Long> ids) {
        return repository.findAll(ids);
    }

    public void save(Resource resource) {
        if (resource.getParent() != null) {
            resource.setParent(repository.findById(resource.getParent().getId()).get());
        }

        repository.save(resource);
    }

    public Optional<Resource> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.delete(repository.findById(id).get());
    }
}
