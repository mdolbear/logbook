package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogbookEntryRepository extends JpaRepository<LogbookEntry, Long> {

    /**
     * Find the logbook entries for aLogbook. There could be many, so best to page through them.
     * @param aLogbook Logbook
     * @param pageable pageable
     * @return List
     */
    public List<LogbookEntry> findByLogbook(Logbook aLogbook,
                                            Pageable pageable);

}
