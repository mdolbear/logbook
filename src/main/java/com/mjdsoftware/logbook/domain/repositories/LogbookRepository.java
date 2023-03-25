package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogbookRepository extends JpaRepository<Logbook, Long> {

    /**
     * Find logbook by aName
     * @param aName String
     * @return Logbook
     */
    public Logbook findByName(String aName);

}
