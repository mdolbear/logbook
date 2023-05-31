package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LogbookEntryService {


    /**
     * Answer logbook entries associated with aLogbook for a given page number and size
     *
     * @param aLogbook    Logbook
     * @param aPageNumber int
     * @param aPageSize   int
     * @return List
     */
    List<LogbookEntry> findLogbookEntriesForLogbook(Logbook aLogbook,
                                                    int aPageNumber,
                                                    int aPageSize);

    /**
     * Create logbook entry from aLogbookEntryDTO
     *
     * @param aLogbook         Logbook
     * @param aLogbookEntryDTO
     * @return LogbookEntry
     */
    LogbookEntry createLogbookEntry(Logbook aLogbook,
                                    LogbookEntryDTO aLogbookEntryDTO);

    /**
     * Delete logbook entry for anId
     * @param anId Long
     */
    public void deleteLogbookEntry(Long anId);

    /**
     * Delete all logbook entries for aLogbookId
     * @param aLogbookId Long
     */
    public void deleteAllLogbookEntries(@NonNull Long aLogbookId);


    /**
     * Modify logbook entry from aLogbookEntryDTO
     * @param aLogbook Logbook
     * @param aLogbookEntryDTO
     * @return LogbookEntry
     */
    public LogbookEntry modifyLogbookEntry(Logbook aLogbook,
                                           LogbookEntryDTO aLogbookEntryDTO);


    /**
     * Answer a logbook entry for anId
     * @param anId Long
     * @return LogbookEntry
     */
    public LogbookEntry findLogbookEntryById(Long anId);

    /**
     * Answer logbook entries associated with aLogbook (in dto form))
     * @param aLogbook Logbook
     * @return List
     */
    public List<LogbookEntryDTO> findAllLogbookEntriesForLogbook(Logbook aLogbook);

    /**
     * Find logbook entry count for aLogbook
     * @param aLogbook Logbook
     * @return Long
     */
    public Long findLogbookEntryCount(Logbook aLogbook);

    /**
     * Answer all logbook entries associated with aLogbook for a given page number and size
     * @param aLogbook Logbook
     * @param aPageNumber int
     * @param aPageSize int
     * @return List
     */
    public List<LogbookEntryDTO> findAllLogbookEntriesForLogbook(@NonNull Logbook aLogbook,
                                                                 int aPageNumber,
                                                                 int aPageSize);
}
