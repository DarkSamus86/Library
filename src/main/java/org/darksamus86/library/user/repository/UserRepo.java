package org.darksamus86.library.user.repository;

import org.darksamus86.library.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // метод для spring security (для проверки роли)
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userRoles ur " +
            "LEFT JOIN FETCH ur.role r " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
}
