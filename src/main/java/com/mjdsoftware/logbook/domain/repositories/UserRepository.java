package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Answer a user by username
     * @param username String
     * @return User
     */
    public User findByUsername(String username);

}
