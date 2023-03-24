package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.domain.repositories.LogbookEntryRepository;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LogbookEntryServiceImpl extends AbstractServiceImpl implements LogbookEntryService {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookEntryRepository logbookEntryRepository;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private ActivityService activityService;


    /**
     * Answer my logger
     * @return logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance of me with my injected parameters
     * @param logbookEntryRepository LogbookEntryRepository
     * @param activityService ActivityService
     */
    @Autowired
    public LogbookEntryServiceImpl(LogbookEntryRepository logbookEntryRepository,
                                   ActivityService activityService) {

        super();
        this.setLogbookEntryRepository(logbookEntryRepository);
        this.setActivityService(activityService);

    }

    /**
     * Answer a logbook entry for anId
     * @param anId Long
     * @return LogbookEntry
     */
    @Transactional
    @Override
    public LogbookEntry findLogbookEntryById(@NonNull Long anId) {

        Optional<LogbookEntry>  tempEntry;
        LogbookEntry            tempResult = null;

        tempEntry = this.getLogbookEntryRepository().findById(anId);
        if (tempEntry.isPresent()) {

            tempResult = tempEntry.get();
        }

        return tempResult;

    }


    /**
     * Answer logbook entries associated with aLogbook for a given page number and size
     * @param aLogbook Logbook
     * @param aPageNumber int
     * @param aPageSize int
     * @return List
     */
    @Override
    @Transactional
    public List<LogbookEntry> findLogbookEntriesForLogbook(@NonNull Logbook aLogbook,
                                                           int aPageNumber,
                                                           int aPageSize) {

        List<LogbookEntry> tempResults;
        Sort               tempDefaultSort = Sort.by(Sort.Direction.DESC,
                                         "activityDate");
        Pageable           tempPage;


        tempPage = PageRequest.of(aPageNumber, aPageSize, tempDefaultSort);
        tempResults = this.getLogbookEntryRepository()
                          .findByLogbook(aLogbook, tempPage);

        return tempResults;

    }

    /**
     * Create logbook entry from aLogbookEntryDTO
     * @param aLogbook Logbook
     * @param aLogbookEntryDTO
     * @return LogbookEntry
     */
    @Override
    @Transactional
    public LogbookEntry createLogbookEntry(@NonNull Logbook aLogbook,
                                           @NonNull LogbookEntryDTO aLogbookEntryDTO) {


        LogbookEntry tempEntry;

        tempEntry = new LogbookEntry();
        tempEntry.updateFrom(aLogbookEntryDTO);

        tempEntry.setLogbook(aLogbook);
        this.getLogbookEntryRepository().save(tempEntry);

        return tempEntry;

    }

    /**
     * Modify logbook entry from aLogbookEntryDTO
     * @param aLogbook Logbook
     * @param aLogbookEntryDTO
     * @return LogbookEntry
     */
    @Override
    @Transactional
    public LogbookEntry modifyLogbookEntry(@NonNull Logbook aLogbook,
                                           @NonNull LogbookEntryDTO aLogbookEntryDTO) {


        LogbookEntry tempEntry = null;

        this.validateId(getLogger(),
             "Invalid id encountered when modifying logbook entry",
                       aLogbookEntryDTO.getId());

        tempEntry = this.findLogbookEntryById(aLogbookEntryDTO.getId());
        if (tempEntry != null) {

            this.validate(aLogbook, tempEntry);

            tempEntry.updateFrom(aLogbookEntryDTO);
            this.getLogbookEntryRepository().save(tempEntry);

        }
        else {

            this.logErrorMessageAndThrowException(getLogger(),
                                                  "LogbookEntry not found for " + aLogbookEntryDTO.getId());
        }

        return tempEntry;

    }






    /**
     * Delete logbook entry for anId
     * @param anId Long
     */
    @Transactional
    @Override
    public void deleteLogbookEntry(@NonNull Long anId) {

        Optional<LogbookEntry> tempEntry;

        tempEntry = this.getLogbookEntryRepository().findById(anId);
        if (tempEntry.isPresent()) {

            this.getActivityService().delete(tempEntry.get().getActivities());
            this.getLogbookEntryRepository().deleteById(anId);

        }

    }




}
