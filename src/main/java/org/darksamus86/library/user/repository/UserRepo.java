package org.darksamus86.library.user.repository;

import org.darksamus86.library.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
