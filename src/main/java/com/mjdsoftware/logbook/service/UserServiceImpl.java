package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.User;
import com.mjdsoftware.logbook.domain.repositories.UserRepository;
import com.mjdsoftware.logbook.dto.UserDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private UserRepository userRepository;

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance of me for aUserRepository
     * @param aUserRepository UserRepository
     */
    @Autowired
    public UserServiceImpl(UserRepository aUserRepository) {

        super();
        this.setUserRepository(aUserRepository);
    }

    /**
     * Create user from aUserDTO
     * @param aUserDTO UserDTO
     */
    @Override
    public User createUser(@NonNull UserDTO aUserDTO) {

        User    tempUser;

        tempUser = new User();
        tempUser.updateFrom(aUserDTO);

        this.getUserRepository().save(tempUser);

        return tempUser;

    }

    /**
     * Answer a User for anId
     * @param anId Long
     * @return User
     */
    @Override
    public User findUserById(@NonNull Long anId) {

        Optional<User> tempOpt;
        User           tempResult = null;

        tempOpt = this.getUserRepository().findById(anId);
        if (tempOpt.isPresent()) {

            tempResult = tempOpt.get();
        }

        return tempResult;

    }

    /**
     * Answer a User by username
     * @param username String
     * @return User
     */
    @Override
    public User findUserByUsername(@NonNull String username) {

        return this.getUserRepository().findByUsername(username);

    }

    /**
     * Find all users
     * @return List
     */
    @Override
    public List<User> findAllUsers() {

        Sort tempDefaultSort = Sort.by(Sort.Direction.ASC,
                           "username");

        return this.getUserRepository().findAll(tempDefaultSort);

    }

    /**
     * Delete user by anId. Note that there is more work to do here to delete dependent, Logbooks, etc
     * @param anId Long
     */
    public void deleteUserById(@NotNull Long anId) {

        this.getUserRepository().deleteById(anId);
    }

}
