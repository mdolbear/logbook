package com.mjdsoftware.logbook.domain.repositories;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import org.springframework.data.domain.Pageable;
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
     * @param aPageable Pageable
     * @return List
     */
    @Query("select act from Activity act join act.logbookEntry lgbkent join lgbkent.logbook lgbk where lgbk.id = :logbookId and " +
            "lgbkent.activityDate between :startDate and :endDate  order by lgbkent.activityDate asc")
    public List<Activity> findAllActivities(@Param("startDate") Calendar startDate,
                                            @Param("endDate") Calendar endDate,
                                            @Param("logbookId") Long logbookId,
                                            Pageable aPageable);

    /**
     * Answer the count of activiies for logbookId between starDate and endDate
     * @param logbookId Long
     * @param startDate Calendar
     * @param endDate Calendar
     * @return Long
     */
    @Query(value="select count(*) FROM activities act inner join logbook_entries lgbkent on act.log_entry_id = lgbkent.id " +
            "inner join logbooks lgbk on lgbkent.logbook_id = lgbk.id where lgbk.id = ?1 and " +
            "lgbkent.activity_date >= ?2 and  lgbkent.activity_date <= ?3", nativeQuery=true)
    public Long getCountByLogbookIdAndDates(Long logbookId,
                                            Calendar startDate,
                                            Calendar endDate);

    /**
     * Find all activities within a date range for a given logbookId
     * @param startDate Date
     * @param endDate Date
     * @param logbookId Long
     * @param aPageable Pageable
     * @return List
     */
    @Query("select act from Activity act join act.logbookEntry lgbkent join lgbkent.logbook lgbk where lgbk.id = :logbookId and " +
            "act.activityType = :activityType and " +
            "lgbkent.activityDate between :startDate and :endDate  order by lgbkent.activityDate asc")
    public List<Activity> findAllActivities(@Param("startDate") Calendar startDate,
                                            @Param("endDate") Calendar endDate,
                                            @Param("logbookId") Long logbookId,
                                            ActivityType activityType,
                                            Pageable aPageable);

    /**
     * Answer the count of activiies for logbookId between starDate and endDate
     * @param logbookId Long
     * @param startDate Calendar
     * @param endDate Calendar
     * @return Long
     */
    @Query(value="select count(*) FROM activities act inner join logbook_entries lgbkent on act.log_entry_id = lgbkent.id " +
            "inner join logbooks lgbk on lgbkent.logbook_id = lgbk.id where lgbk.id = ?1 and " +
            "act.activity_type = ?2 and " +
            "lgbkent.activity_date >= ?3 and  lgbkent.activity_date <= ?4", nativeQuery=true)
    public Long getCountByLogbookIdAndDates(Long logbookId,
                                            String activityType,
                                            Calendar startDate,
                                            Calendar endDate);


}
