package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.dto.LogbookDTO;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LogbookService {
    /**
     * Answer all logbooks
     *
     * @return List
     */
    List<Logbook> findAllLogbooks();

    /**
     * Create logbook
     * @param aVO LogbookDTO
     */
    public Logbook createLogbook(LogbookDTO aVO);

    /**
     * Find logbook by name
     * @param name String
     * @return Logbook
     */
    public Logbook findByName(String name);

    /**
     * Answer a logbook for anId
     * @param anId Long
     * @return Logbook
     */
    public Logbook findLogbookById(Long anId);


}
