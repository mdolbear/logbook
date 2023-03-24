package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import lombok.NonNull;
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

}
