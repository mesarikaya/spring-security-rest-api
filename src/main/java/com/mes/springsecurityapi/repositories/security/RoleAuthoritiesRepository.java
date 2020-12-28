package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.RoleAuthorities;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mesar on 12/23/2020
 */
@Repository
public interface RoleAuthoritiesRepository extends ReactiveCrudRepository<RoleAuthorities, Integer>{

}
