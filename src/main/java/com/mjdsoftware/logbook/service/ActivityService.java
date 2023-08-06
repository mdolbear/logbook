package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.csv.ActivityWrapper;
import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.ActivityType;
import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.ActivityExportRequest;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ActivityService {

    /**
     * Create activity
     * @param aLogbook Logbook
     * @param anEntry LogbookEntry
     * @param anActivityDTO ActivityDTO
     * @return Activity
     */
    public Activity createActivity(Logbook aLogbook,
                                   LogbookEntry anEntry,
                                   ActivityDTO anActivityDTO);

    /**
     * Modify activity
     * @param aLogbook Logbook
     * @param anEntry LogbookEntry
     * @param anActivityDTO ActivityDTO
     * @return Activity
     */
    public Activity modifyActivity(Logbook aLogbook,
                                   LogbookEntry anEntry,
                                   ActivityDTO anActivityDTO);

    /**
     * Delete anActivities
     *
     * @param anActivities List
     */
    void delete(List<Activity> anActivities);

    /**
     * Find activities for anEntry
     * @param anEntry LogbookEntry
     * @return List
     */
    public List<Activity> findActivitiesForLogbookEntry(LogbookEntry anEntry);


    /**
     * Find all activities between dates as wrappers
     * @param aLogbookId Long
     * @param anActivityExportRequest ActivityExportRequest
     * @param aPageNumber
     * @return List
     */
    public List<ActivityWrapper> findAllActivitiesBetweenDatesAsWrappers(Long aLogbookId,
                                                                         ActivityExportRequest anActivityExportRequest,
                                                                         int aPageNumber);

    /**
     * Export activities to file asynchronously
     * @param aLogbookId Long
     * @param anActivityExportRequest ActivityExportRequest
     */
    public void exportActivitiesToFileAsynchronously(Long aLogbookId,
                                                     ActivityExportRequest anActivityExportRequest);

    /**
     * Answer count of all activities that exist for aLogbookId between aStartDate and anEndDate
     * @param aLogbookId Long
     * @param aStartDateEpoch Long
     * @param anEndDateEpoch Long
     * @return Long
     */
    public Long getCountOfAllActivitiesBetweenDates(Long aLogbookId,
                                                    Long aStartDateEpoch,
                                                    Long anEndDateEpoch,
                                                    ActivityType anActivityType);

}
