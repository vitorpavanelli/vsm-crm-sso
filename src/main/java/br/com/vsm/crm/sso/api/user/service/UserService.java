package br.com.vsm.crm.sso.api.user.service;

import br.com.vsm.crm.sso.api.resource.service.ResourceService;
import br.com.vsm.crm.sso.api.user.repository.Access;
import br.com.vsm.crm.sso.api.user.repository.AccessRepository;
import br.com.vsm.crm.sso.api.user.repository.User;
import br.com.vsm.crm.sso.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private ResourceService resourceService;

    public Iterable<User> findAll() {
        return repository.findAll();
    }

    public Iterable<User> findAllByName(String name) {
        return repository.findAllByName(name);
    }

    public Iterable<User> findAllByNameExcludingAdmin(String name) {
        return repository.findAllByNameExcludingAdmin(name);
    }

    public User findOneByIdWithAccess(Long id) throws Exception {
        if (id == null) {
            throw new Exception("ID cannot be null");
        }

        return repository.findOneByIdWithAccess(id);
    }

    public User findOneByLogin(String login) throws Exception {
        if (login == null) {
            throw new Exception("Login cannot be null");
        }

        return repository.findByLogin(login);
    }

    public Long save(User user) throws Exception {
        boolean hasId = (user.getId() != null);
        if (hasId) {
            User userAux = findOneByIdWithAccess(user.getId());
            user.setPassword(userAux.getPassword());

            // removes all of the accesses not set
            Iterator<Access> iterator = userAux.getAccesses().iterator();
            while (iterator.hasNext()) {
                Access access = iterator.next();
                if (!user.getAccesses().contains(access)) {
                    accessRepository.delete(access);
                }
            }

        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        // retrieves dependecies for accesses
        if (user.getAccesses() != null && user.getAccesses().size() > 0) {
            Set<Access> accesses = new HashSet();
            Iterator<Access> iterator = user.getAccesses().iterator();
            while (iterator.hasNext()) {
                Access access = iterator.next();
                access.setUser(user);
                access.setResource(resourceService.findById(access.getResource().getId()).get());

                accesses.add(access);
            }

            user.setAccesses(accesses);
        }

        final User savedUser = repository.save(user);
        if (!hasId) {
            return savedUser.getId();
        }

        return null;
    }

    public User findOneByLoginWithAccess(String login) throws Exception {
        if (login == null) {
            throw new Exception("Login cannot be null");
        }

        return repository.findOneByLoginWithAccess(login);
    }

    public void delete(Long id) {
        repository.delete(repository.findById(id).get());
    }
}
