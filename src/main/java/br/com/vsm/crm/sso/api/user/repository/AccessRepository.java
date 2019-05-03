package br.com.vsm.crm.sso.api.user.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AccessRepository extends CrudRepository<Access, Long> {

    @Query("from Access a where a.user = ?1 order by a.resource.level, a.resource.placement")
    Set<Access> findByUser(User user);
}
