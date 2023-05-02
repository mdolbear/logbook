package com.mjdsoftware.logbook.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class LogbookNotFoundException extends RuntimeException {

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE)
    private Long logbookId;

    /**
     * Answer an exception when not finding a logbook for aLodbookId
     * @param aLogbookId Long
     */
    public LogbookNotFoundException(Long aLogbookId) {

        super();
        this.setLogbookId(aLogbookId);

    }

}
