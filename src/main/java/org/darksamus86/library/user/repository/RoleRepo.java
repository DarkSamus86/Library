package org.darksamus86.library.user.repository;

import org.darksamus86.library.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query("SELECT r.name, COUNT(ur) FROM Role r LEFT JOIN r.userRoles ur GROUP BY r.name")
    List<Object[]> countUsersByRole();
}