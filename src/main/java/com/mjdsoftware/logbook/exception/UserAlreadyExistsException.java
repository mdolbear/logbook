package com.mjdsoftware.logbook.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class UserAlreadyExistsException  extends RuntimeException  {

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE)
    private String username;

    /**
     * Create exception for username
     * @param username String
     */
    public UserAlreadyExistsException(String username) {
        super();
        this.setUsername(username);
    }

}
