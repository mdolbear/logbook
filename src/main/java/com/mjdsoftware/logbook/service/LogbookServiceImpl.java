package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.User;
import com.mjdsoftware.logbook.domain.repositories.LogbookRepository;
import com.mjdsoftware.logbook.dto.LogbookDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LogbookServiceImpl implements LogbookService {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookRepository logbookRepository;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookEntryService logbookEntryService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private UserService userService;

    /**
     * Answer my logger
     * @return logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance with dependencies
     * @param aService LogbookEntryService
     * @param aLogbookRepository LogbookRepository
     * @param aUserService UserService
     */
    @Autowired
    public LogbookServiceImpl(LogbookEntryService aService,
                              LogbookRepository aLogbookRepository,
                              UserService aUserService) {

        super();
        this.setLogbookEntryService(aService);
        this.setLogbookRepository(aLogbookRepository);
        this.setUserService(aUserService);

    }

    /**
     * Answer a logbook for anId
     * @param anId Long
     * @return Logbook
     */
    @Transactional
    public Logbook findLogbookById(@NonNull Long anId) {

        Optional<Logbook> tempOpt;
        Logbook           tempResult = null;

        tempOpt = this.getLogbookRepository().findById(anId);
        if (tempOpt.isPresent()) {

            tempResult = tempOpt.get();
        }

        return tempResult;

    }


    /**
     * Answer all logbooks
     * @return List
     */
    @Override
    @Transactional
    public List<Logbook> findAllLogbooks() {

        return this.getLogbookRepository().findAll();
    }

    /**
     * Create logbook
     * @param aUser User
     * @param aVO LogbookDTO
     * @return Logbook
     */
    @Override
    @Transactional
    public Logbook createLogbook(@NonNull User aUser,
                                 @NonNull LogbookDTO aVO) {

        Logbook tempBook;

        tempBook = new Logbook();
        tempBook.updateFrom(aVO);
        tempBook.setUser(aUser);

        this.getLogbookRepository().save(tempBook);

        return tempBook;

    }

    /**
     * Find logbook by name
     * @param name String
     * @return Logbook
     */
    @Transactional
    @Override
    public Logbook findByName(@NonNull String name) {

        return this.getLogbookRepository().findByName(name);
    }

    /**
     * Delete logbook for aLogbookId
     * @param aLogbookId Long
     */
    @Transactional
    @Override
    public void deleteLogbook(@NonNull Long aLogbookId) {

        Optional<Logbook> tempLogbook;

        tempLogbook = this.getLogbookRepository().findById(aLogbookId);
        if (tempLogbook.isPresent()) {

            //First delete all entries for Logbook
            this.getLogbookEntryService().deleteAllLogbookEntries(aLogbookId);

            //Now delete the logbook
            this.getLogbookRepository().deleteById(aLogbookId);

            getLogger().info("Logbook successfully delete for id {}", aLogbookId);

        }
        else {

            getLogger().info("Logbook not found for id {}. Delete silently ignored.", aLogbookId);

        }

    }


}
