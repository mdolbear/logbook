package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LogbookRepository extends JpaRepository<Logbook, Long> {

    /**
     * Find logbook by aName
     * @param aName String
     * @return Logbook
     */
    public Logbook findByName(String aName);

}
