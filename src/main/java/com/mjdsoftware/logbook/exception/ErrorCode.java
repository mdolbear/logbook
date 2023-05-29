package com.mjdsoftware.logbook.exception;


/**
 * If you add a new ErrorCode value, be sure to add a corresponding message to messages.properties and all
 * language-specific messages_*.properties.
 */
public enum ErrorCode {

    // Unknown errors
    UNKNOWN_ERROR,
    RUNTIME,

    // Standard Java exceptions
    ILLEGAL_ARGUMENT,
    ILLEGAL_STATE,
    NULL_POINTER,
    ACCESS_DENIED,

    //Application specific exceptions
    LOGBOOK_NOT_FOUND,
    USER_ALREADY_EXISTS

}
