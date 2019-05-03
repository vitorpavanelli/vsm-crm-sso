package br.com.vsm.crm.sso.api.resource.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Set;

import static org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {

    @Query("select distinct r from Resource r left join fetch r.resources where r.parent = null order by r.level, r.placement")
    @QueryHints(value = @QueryHint(name = HINT_PASS_DISTINCT_THROUGH, value = "false"))
    Iterable<Resource> findAll();

    @Query("select distinct r from Resource r left join fetch r.resources rc where rc.id in (:ids) order by r.level, r.placement")
    @QueryHints(value = @QueryHint(name = HINT_PASS_DISTINCT_THROUGH, value = "false"))
    Iterable<Resource> findAll(Set<Long> ids);
}
