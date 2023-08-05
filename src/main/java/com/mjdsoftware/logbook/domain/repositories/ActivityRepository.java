package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Calendar;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {


    /**
     * Find all activities for logbookEntry
     * @param logbookEntry LogbookEntry
     * @return List
     */
    public List<Activity> findActivitiesByLogbookEntry(LogbookEntry logbookEntry);

    /**
     * Find all activities within a date range for a given logbookId
     * @param startDate Date
     * @param endDate Date
     * @param logbookId Long
     * @return List
     */
    @Query("select act from Activity act join act.logbookEntry lgbkent join lgbkent.logbook lgbk where lgbk.id = :logbookId and " +
            "lgbkent.activityDate between :startDate and :endDate  order by lgbkent.activityDate asc")
    public List<Activity> findAllActivities(@Param("startDate") Calendar startDate,
                                            @Param("endDate") Calendar endDate,
                                            @Param("logbookId") Long logbookId);

    /**
     * Find all activities within a date range for a given logbookId
     * @param startDate Date
     * @param endDate Date
     * @param logbookId Long
     * @return List
     */
    @Query("select act from Activity act join act.logbookEntry lgbkent join lgbkent.logbook lgbk where lgbk.id = :logbookId and " +
            "act.activityType = :activityType and " +
            "lgbkent.activityDate between :startDate and :endDate  order by lgbkent.activityDate asc")
    public List<Activity> findAllActivities(@Param("startDate") Calendar startDate,
                                            @Param("endDate") Calendar endDate,
                                            @Param("logbookId") Long logbookId,
                                            ActivityType activityType);


}
