package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import org.slf4j.Logger;

public abstract class AbstractServiceImpl {

    /**
     * Answer a default instance
     */
    public AbstractServiceImpl() {

    }


    /**
     * Validate aLogbook and aLogbookEntry
     * @param aLogbook Logbook
     * @param aLogbookEntry LogbookEntry
     */
    protected void validate(Logbook aLogbook, LogbookEntry aLogbookEntry) {

        if (!aLogbookEntry.getLogbook().equals(aLogbook)) {

            throw new IllegalArgumentException("Logbook and LogbookEntry do not correspond");
        }

    }

    /**
     * Validate aLogbook and aLogbookEntry
     * @param aLogbookEntry LogbookEntry
     * @param anActivity Activity
     */
    protected void validate(LogbookEntry aLogbookEntry, Activity anActivity) {

        if (!anActivity.getLogbookEntry().equals(aLogbookEntry)) {

            throw new IllegalArgumentException("LogbookEntry and Activity do not correspond");
        }

    }

    /**
     * Validate anId
     * @param logger Logger
     * @param aMessage String
     * @anId Long
     */
    protected void validateId(Logger logger,
                              String aMessage,
                              Long anId) {

        if (anId == null ||
                anId.longValue() <= 0) {

            this.logErrorMessageAndThrowException(logger, aMessage);

        }


    }


    /**
     * Log aMessage and throw Exception
     * @param aMessage String
     */
    protected void logErrorMessageAndThrowException(Logger logger,
                                                    String aMessage) {

        logger.error(aMessage);
        throw new IllegalArgumentException(aMessage);

    }


}
