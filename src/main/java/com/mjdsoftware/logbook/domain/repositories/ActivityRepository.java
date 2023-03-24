package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {


    /**
     * Find all activities for logbookEntry
     * @param logbookEntry LogbookEntry
     * @return List
     */
    public List<Activity> findActivitiesByLogbookEntry(LogbookEntry logbookEntry);

}
