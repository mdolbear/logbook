package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.User;
import com.mjdsoftware.logbook.dto.UserDTO;

import java.util.List;

public interface UserService {

    /**
     * Create user from aUserDTO
     *
     * @param aUserDTO UserDTO
     */
    User createUser(UserDTO aUserDTO);

    /**
     * Answer a User for anId
     *
     * @param anId Long
     * @return User
     */
    User findUserById(Long anId);

    /**
     * Answer a User by username
     *
     * @param username String
     * @return User
     */
    User findUserByUsername(String username);

    /**
     * Find all users
     *
     * @return List
     */
    List<User> findAllUsers();

    /**
     * Delete user by anId. Note that there is more work to do here to delete dependent, Logbooks, etc
     * @param anId Long
     */
    public void deleteUserById(Long anId);

}
