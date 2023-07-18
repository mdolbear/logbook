package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.domain.entities.*;
import com.mjdsoftware.logbook.domain.repositories.ActivityRepository;
import com.mjdsoftware.logbook.domain.repositories.LogbookEntryRepository;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.MonitoredAerobicActivityDTO;
import com.mjdsoftware.logbook.dto.StrengthTrainingActivityDTO;
import com.mjdsoftware.logbook.dto.UnMonitoredAerobicActivityDTO;
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
public class ActivityServiceImpl extends AbstractServiceImpl implements ActivityService {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookEntryRepository logbookEntryRepository;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private ActivityRepository activityRepository;


    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance of me on aRepository
     * @param aRepository ActivityRepository
     */
    @Autowired
    public ActivityServiceImpl(ActivityRepository aRepository,
                               LogbookEntryRepository logbookEntryRepository) {

        super();
        this.setActivityRepository(aRepository);
        this.setLogbookEntryRepository(logbookEntryRepository);

    }


    /**
     * Create activity
     * @param aLogbook Logbook
     * @param anEntry LogbookEntry
     * @param anActivityDTO ActivityDTO
     * @return Activity
     */
    @Transactional
    @Override
    public Activity createActivity(@NonNull Logbook aLogbook,
                                   @NonNull LogbookEntry anEntry,
                                   @NonNull ActivityDTO anActivityDTO) {

        Activity tempActivity;

        //Validation
        this.validate(aLogbook, anEntry);

        //Create and save activity
        tempActivity = this.createActivityBasedOn(anActivityDTO);
        tempActivity.updateFrom(anActivityDTO);
        anEntry.addActivity(tempActivity);
        this.getActivityRepository().save(tempActivity);

        return tempActivity;

    }

    /**
     * Create activity based on dto type
     * @param anActivityDTO ActivityDTO
     * @return Activity
     */
    private Activity createActivityBasedOn(ActivityDTO anActivityDTO) {

        Activity tempResult;

        if (anActivityDTO instanceof StrengthTrainingActivityDTO) {
            tempResult = new StrengthTrainingActivity();
        }
        else if (anActivityDTO instanceof UnMonitoredAerobicActivityDTO) {
            tempResult = new UnMonitoredAerobicActivity();
        }
        else if (anActivityDTO instanceof MonitoredAerobicActivityDTO) {
            tempResult = new MonitoredAerobicActivity();
        }
        else {
            throw new IllegalArgumentException("Unrecognized activity type encountered");
        }

        return tempResult;

    }

    /**
     * Modify activity
     * @param aLogbook Logbook
     * @param anEntry LogbookEntry
     * @param anActivityDTO ActivityDTO
     * @return Activity
     */
    @Transactional
    @Override
    public Activity modifyActivity(@NonNull Logbook aLogbook,
                                   @NonNull LogbookEntry anEntry,
                                   @NonNull ActivityDTO anActivityDTO) {

        Optional<Activity> tempActivity;
        Activity           tempResult = null;

        //Validation
        this.validate(aLogbook, anEntry);

        //Modify and save activity
        this.validateId(getLogger(),
             "Invalid Id found when modifying Activity",
                        anActivityDTO.getId());

        tempActivity = this.getActivityRepository().findById(anActivityDTO.getId());
        if (tempActivity.isPresent()) {

            tempResult = tempActivity.get();
            this.validate(anEntry, tempResult);

            tempResult.updateFrom(anActivityDTO);

            this.getActivityRepository().save(tempResult);

        }
        else {

            this.logErrorMessageAndThrowException(getLogger(),
                    "Activity not found for " + anActivityDTO.getId());

        }

        return tempResult;

    }

    /**
     * Find activities for anEntry
     * @param anEntry LogbookEntry
     * @return List
     */
    @Override
    @Transactional
    public List<Activity> findActivitiesForLogbookEntry(@NonNull LogbookEntry anEntry) {

        return this.getActivityRepository().findActivitiesByLogbookEntry(anEntry);

    }



    /**
     * Delete anActivities
     * @param anActivities List
     */
    @Override
    @Transactional
    public void delete(@NonNull List<Activity> anActivities) {

        getLogger().info("Deleting {}", anActivities);
        this.getActivityRepository().deleteAll(anActivities);
        getLogger().info("Deleted Activities Complete");

    }


}
