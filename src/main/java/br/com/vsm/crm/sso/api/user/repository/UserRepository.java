package br.com.vsm.crm.sso.api.user.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    @Query("select u from User u where u.name like %?1%")
    Iterable<User> findAllByName(String name);

    @Query("select u from User u where u.name like %?1% and u.admin = false")
    Iterable<User> findAllByNameExcludingAdmin(String name);

    User findByLogin(String login);

    @Query("select u from User u left join fetch u.accesses where u.id = ?1")
    User findOneByIdWithAccess(Long id);


    @Query("select u from User u left join fetch u.accesses where u.login = ?1")
    User findOneByLoginWithAccess(String login);
}
