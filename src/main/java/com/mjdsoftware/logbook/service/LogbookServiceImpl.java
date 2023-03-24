package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.repositories.LogbookRepository;
import com.mjdsoftware.logbook.dto.LogbookDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LogbookServiceImpl implements LogbookService {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookRepository logbookRepository;

    /**
     * Answer an instance with dependencies
     * @param aLogbookRepository LogbookRepository
     */
    @Autowired
    public LogbookServiceImpl(LogbookRepository aLogbookRepository) {

        super();
        this.setLogbookRepository(aLogbookRepository);
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
     * @param aVO LogbookDTO
     */
    @Override
    @Transactional
    public Logbook createLogbook(@NonNull LogbookDTO aVO) {

        Logbook tempBook;

        tempBook = new Logbook();
        tempBook.updateFrom(aVO);

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


}
