package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.Role;
import com.amazonwh.wms.model.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRoleEnum name);
}
