package com.mjdsoftware.logbook.service;

import com.mjdsoftware.logbook.csv.ActivityWrapper;
import com.mjdsoftware.logbook.csv.ChunkableActivityCSVFileExporter;
import com.mjdsoftware.logbook.domain.entities.*;
import com.mjdsoftware.logbook.domain.repositories.ActivityRepository;
import com.mjdsoftware.logbook.domain.repositories.LogbookEntryRepository;
import com.mjdsoftware.logbook.dto.*;
import com.mjdsoftware.logbook.utils.FileUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivityServiceImpl extends AbstractServiceImpl implements ActivityService {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookEntryRepository logbookEntryRepository;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private ActivityRepository activityRepository;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private FileUtilities fileUtils;

    //Constants
    public static final int PAGE_SIZE = 10;


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
     * @param logbookEntryRepository LogbookEntryRepository
     * @param aFileUtils FileUtilities
     */
    @Autowired
    public ActivityServiceImpl(ActivityRepository aRepository,
                               LogbookEntryRepository logbookEntryRepository,
                               FileUtilities aFileUtils) {

        super();
        this.setActivityRepository(aRepository);
        this.setLogbookEntryRepository(logbookEntryRepository);
        this.setFileUtils(aFileUtils);

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
     * Answer count of all activities that exist for aLogbookId between aStartDate and anEndDate
     * @param aLogbookId Long
     * @param aStartDateEpoch Long
     * @param anEndDateEpoch Long
     * @return List
     */
    @Transactional
    @Override
    public Long getCountOfAllActivitiesBetweenDates(@NonNull Long aLogbookId,
                                                    @NonNull Long aStartDateEpoch,
                                                    @NonNull Long anEndDateEpoch,
                                                    ActivityType anActivityType) {

        Calendar        tempStartDate;
        Calendar        tempEndDate;
        Long            tempCount;

        this.validateId(getLogger(), "Invalid logbook id", aLogbookId);

        tempStartDate = Calendar.getInstance();
        tempStartDate.setTime(new Date(aStartDateEpoch));
        tempEndDate = Calendar.getInstance();
        tempEndDate.setTime(new Date(anEndDateEpoch));

        tempCount =
                this.basicGetCpuntOfActivitiesBetweenDatas(aLogbookId, anActivityType, tempStartDate, tempEndDate);

        return tempCount;

    }

    /**
     * Get count of activities for the arguments below
     * @param aLogbookId Long
     * @param anActivityType ActivityType
     * @param tempStartDate Calendar
     * @param tempEndDate Calendar
     * @return List
     */
    private Long basicGetCpuntOfActivitiesBetweenDatas(Long aLogbookId,
                                                       ActivityType anActivityType,
                                                       Calendar tempStartDate,
                                                       Calendar tempEndDate) {

        Long tempResult;

        if (anActivityType == null) {

            tempResult =
                    this.getActivityRepository()
                        .getCountByLogbookIdAndDates(aLogbookId,
                                                     tempStartDate,
                                                     tempEndDate);
        }
        else {

            tempResult =
                    this.getActivityRepository()
                        .getCountByLogbookIdAndDates(aLogbookId,
                                                     anActivityType.name(),
                                                     tempStartDate,
                                                     tempEndDate);

        }

        return tempResult;

    }


    /**
     * Find all activities that exist for aLogbookId between aStartDate and anEndDate
     * @param aLogbookId Long
     * @param aStartDateEpoch Long
     * @param anEndDateEpoch Long
     * @param aPageNumber int
     * @return List
     */
    private List<Activity> findAllActivitiesBetweenDates(@NonNull Long aLogbookId,
                                                        @NonNull Long aStartDateEpoch,
                                                        @NonNull Long anEndDateEpoch,
                                                        ActivityType anActivityType,
                                                        int aPageNumber) {

        Calendar        tempStartDate;
        Calendar        tempEndDate;
        List<Activity>  tempResults;

        this.validateId(getLogger(), "Invalid logbook id", aLogbookId);

        tempStartDate = Calendar.getInstance();
        tempStartDate.setTime(new Date(aStartDateEpoch));
        tempEndDate = Calendar.getInstance();
        tempEndDate.setTime(new Date(anEndDateEpoch));

        tempResults =
                this.basicFindActivitiesBetweenDates(aLogbookId,
                                                     anActivityType,
                                                     tempStartDate,
                                                     tempEndDate,
                                                     aPageNumber);

        return tempResults;

    }

    /**
     * Find all activities between dates as wrappers
     * @param aLogbookId Long
     * @param anActivityExportRequest ActivityExportRequest
     * @param aPageNumber
     * @return List
     */
    @Transactional
    @Override
    public List<ActivityWrapper> findAllActivitiesBetweenDatesAsWrappers(Long aLogbookId,
                                                                         ActivityExportRequest anActivityExportRequest,
                                                                         int aPageNumber) {

        List<Activity>          tempActivities;
        List<ActivityWrapper>   tempWrappers;

        tempActivities =
                this.findAllActivitiesBetweenDates(aLogbookId,
                                                    anActivityExportRequest.getStartTimeEpoch(),
                                                    anActivityExportRequest.getEndTimeEpoch(),
                                                    anActivityExportRequest.getActivityType(),
                                                    aPageNumber);
        tempWrappers = this.asActivityWrappers(tempActivities);

        return tempWrappers;

    }

    /**
     * Find activities for the arguments below
     * @param aLogbookId Long
     * @param anActivityType ActivityType
     * @param tempStartDate Calendar
     * @param tempEndDate Calendar
     * @param aPageNumber int
     * @return List
     */
    private List<Activity> basicFindActivitiesBetweenDates(Long aLogbookId,
                                                           ActivityType anActivityType,
                                                           Calendar tempStartDate,
                                                           Calendar tempEndDate,
                                                           int aPageNumber) {

        List<Activity> tempResults;
        Pageable       tempPageable;

        tempPageable = this.createPageable(aPageNumber);
        if (anActivityType == null) {

            tempResults =
                this.getActivityRepository()
                           .findAllActivities(tempStartDate,
                                              tempEndDate,
                                              aLogbookId,
                                              tempPageable);
        }
        else {

            tempResults =
                    this.getActivityRepository()
                            .findAllActivities(tempStartDate,
                                              tempEndDate,
                                              aLogbookId,
                                              tempPageable);

        }

        return tempResults;

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


    /**
     * Export activities to file asynchronously
     * @param aLogbookId Long
     * @param anActivityExportRequest ActivityExportRequest
     */
    @Override
    @Transactional
    public void exportActivitiesToFileAsynchronously(Long aLogbookId,
                                                     ActivityExportRequest anActivityExportRequest) {

        List<ActivityWrapper>   tempWrappers;
        Long                    tempNumberOfActivities;
        int                     tempNumberOfPages;


        tempNumberOfActivities =
                this.getCountOfAllActivitiesBetweenDates(aLogbookId,
                                                         anActivityExportRequest.getStartTimeEpoch(),
                                                         anActivityExportRequest.getEndTimeEpoch(),
                                                         anActivityExportRequest.getActivityType());

        if (tempNumberOfActivities > 0) {

            tempNumberOfPages = this.calculateNumberOfPages(tempNumberOfActivities);

            tempWrappers =
                    this.findAllActivitiesBetweenDatesAsWrappers(aLogbookId,
                                                                anActivityExportRequest,
                                                   0);

            //Export file asynchronously
            if (!tempWrappers.isEmpty()) {

                this.basicExportActivitiesToFileAsynchronously(anActivityExportRequest,
                                                               aLogbookId,
                                                               tempWrappers,
                                                               tempNumberOfPages);
            }

        }
        else {
            getLogger().info("No activities encountered to export for logbookId: {}",  aLogbookId);
        }

    }

    /**
     * Answer anActivities as wrapper objects for export
     * @param anActivities List
     * @return List
     */
    private List<ActivityWrapper> asActivityWrappers(List<Activity> anActivities) {

        return anActivities.stream()
                .map((anEntry)->this.asWrapperObject(anEntry))
                .collect(Collectors.toList());

    }

    /**
     * Answer anActivity as a wrapper object
     * @param anActivity Activity
     * @return ActivityWrapper
     */
    private ActivityWrapper asWrapperObject(Activity anActivity) {

        ActivityWrapper tempResult = null;

        if (anActivity!= null) {

            tempResult = new ActivityWrapper(anActivity);
        }

        return tempResult;

    }

    /**
     * Asynchronously export activities to file
     * @param anActivityExportRequest ActivityExportRequest
     * @param aLogbookId Long
     * @param aWrappers List
     * @param aNumberOfPages int
     */
    private void basicExportActivitiesToFileAsynchronously(ActivityExportRequest anActivityExportRequest,
                                                           Long aLogbookId,
                                                           List<ActivityWrapper> aWrappers,
                                                           int aNumberOfPages) {

        File                             tempFile;
        ChunkableActivityCSVFileExporter tempExporter;

        tempFile = this.createEmptyFile(anActivityExportRequest.getExportFilename());
        tempExporter = new ChunkableActivityCSVFileExporter(tempFile,
                                                            aLogbookId,
                                                            anActivityExportRequest,
                                                            aNumberOfPages,
                                               this,
                                                            this.getFileUtils());
        tempExporter.writeCsvFileAsynchronously(aWrappers.toArray(new ActivityWrapper[aWrappers.size()]));

    }

    /**
     * Create empty file
     * @param anAbsoluteFilepath String
     */
    private File createEmptyFile(String anAbsoluteFilepath) {

        File    tempResult = null;

        try {

            tempResult = new File(anAbsoluteFilepath);
            this.getFileUtils().createNewEmptyFile(tempResult);
            this.getFileUtils().validateFileExists(tempResult);
        }
        catch (IOException e) {

            getLogger().error("Cannot create file for " + anAbsoluteFilepath, e);
            throw new IllegalArgumentException("Cannot create file for " + anAbsoluteFilepath, e);

        }

        return tempResult;
    }


    /**
     * Calculate number of pages
     * @param totalNumberOfActivities Long
     * @return int
     */
    private int calculateNumberOfPages(Long totalNumberOfActivities) {

        return (int)(totalNumberOfActivities / PAGE_SIZE) + 1;
    }

    /**
     * Create pageable for aPageNumber
     * @param aPageNumber int
     */
    private Pageable createPageable(int aPageNumber) {

        return PageRequest.of(aPageNumber, PAGE_SIZE);
    }

}
