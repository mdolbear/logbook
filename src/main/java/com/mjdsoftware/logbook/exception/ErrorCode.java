package com.mjdsoftware.logbook.exception;


/**
 * If you add a new ErrorCode value, be sure to add a corresponding message to messages.properties and all
 * language-specific messages_*.properties.
 */
public enum ErrorCode {

    // Generic error if no specific error code matches
    UNKNOWN_ERROR,
    RUNTIME,
    // Standard Java exceptions
    ILLEGAL_ARGUMENT,
    ILLEGAL_STATE,
    NULL_POINTER,
    ACCESS_DENIED

}
