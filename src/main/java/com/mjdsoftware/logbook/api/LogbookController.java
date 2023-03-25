package com.mjdsoftware.logbook.api;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.LogbookDTO;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import com.mjdsoftware.logbook.service.ActivityService;
import com.mjdsoftware.logbook.service.LogbookEntryService;
import com.mjdsoftware.logbook.service.LogbookService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/")
public class LogbookController {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookService logbookService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookEntryService logbookEntryService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private ActivityService activityService;


    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }


    /**
     * Answer an instance with muy dependencies
     * @param aLogbookService LogbookService
     * @param aLogbookEntryService LogbookEntryService
     * @param activityService ActivityService
     */
    @Autowired
    public LogbookController(LogbookService aLogbookService,
                             LogbookEntryService aLogbookEntryService,
                             ActivityService activityService) {

        super();
        this.setLogbookService(aLogbookService);
        this.setLogbookEntryService(aLogbookEntryService);
        this.setActivityService(activityService);

    }


    /**
     * Answer all logbooks
     * @return ResponseEntity
     */
    @GetMapping("logbooks")
    public ResponseEntity<List<LogbookDTO>> findAllLogbooks() {

        List<Logbook> tempLogbooks;

        tempLogbooks = this.getLogbookService().findAllLogbooks();

        return new ResponseEntity<>(this.asLogbookValueObjects(tempLogbooks),
                                    HttpStatus.OK);

    }

    /**
     * Find logbook by aName
     * @param name String
     * @return ResponseEntity
     */
    @GetMapping("logbook/{name}")
    public ResponseEntity<LogbookDTO> findLogbookByName(@PathVariable String name) {

        Logbook tempResult;

        tempResult = this.getLogbookService().findByName(name);

        return new ResponseEntity<>(this.asValueObject(tempResult),
                                    HttpStatus.OK);

    }


    /**
     * Create a logbook
     * @param aLogbookVO LogbookDTO
     * @return ResponseEntity
     */
    @PostMapping("/logbook")
    public ResponseEntity<LogbookDTO> createLogbook(@Valid @RequestBody LogbookDTO aLogbookVO) {

        Logbook tempResult;

        tempResult = this.getLogbookService().createLogbook(aLogbookVO);

        return new ResponseEntity<>(this.asValueObject(tempResult),
                                    HttpStatus.OK);
    }


    /**
     * Answer aLogbook as a value object
     * @param aLogbook Logbook
     * @return LogbookDTO
     */
    private LogbookDTO asValueObject(Logbook aLogbook) {

        LogbookDTO tempResult = null;

        if (aLogbook != null) {

            tempResult = aLogbook.asValueObject();
        }

        return tempResult;

    }


    /**
     * Answer aLogbook as value objects
     * @param aLogbooks List
     * @return List
     */
    private List<LogbookDTO> asLogbookValueObjects(List<Logbook> aLogbooks) {

        return aLogbooks.stream()
                        .map((aLogbook)->this.asValueObject(aLogbook))
                        .collect(Collectors.toList());

    }

    /**
     * Create a logbook entry
     * @param logbookId Long
     * @param aLogbookEntryVO LogbookEntryVO
     * @return ResponseEntity
     */
    @PostMapping("logbook/{logbookId}/entry")
    public ResponseEntity<LogbookEntryDTO> createLogbookEntry(@PathVariable Long logbookId,
                                                              @Valid @RequestBody LogbookEntryDTO aLogbookEntryVO) {

        LogbookEntry tempResult = null;
        Logbook      tempLogbook;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempResult =
                    this.getLogbookEntryService()
                        .createLogbookEntry(tempLogbook, aLogbookEntryVO);
        }

        //Log message if we didn't create anything -- TBD this should probably be an error
        if (tempLogbook == null) {

            getLogger().info("Logbook was not found to create a LogbookEntry");
        }

        return new ResponseEntity<>(this.asValueObject(tempResult),
                                    HttpStatus.OK);
    }


    /**
     * Answer aLogbookEntry as a value object
     * @param aLogbookEntry LogbookEntry
     * @return LogbookEntryDTO
     */
    private LogbookEntryDTO asValueObject(LogbookEntry aLogbookEntry) {

        LogbookEntryDTO tempResult = null;

        if (aLogbookEntry != null) {

            tempResult = aLogbookEntry.asValueObject();
        }

        return tempResult;

    }


    /**
     * Modify a logbook entry
     * @param logbookId Long
     * @param aLogbookEntryVO LogbookEntryVO
     * @return ResponseEntity
     */
    @PutMapping("logbook/{logbookId}/entry")
    public ResponseEntity<LogbookEntryDTO> modifyLogbookEntry(@PathVariable Long logbookId,
                                                              @Valid @RequestBody LogbookEntryDTO aLogbookEntryVO) {

        LogbookEntry tempResult = null;
        Logbook      tempLogbook;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempResult =
                    this.getLogbookEntryService()
                        .modifyLogbookEntry(tempLogbook, aLogbookEntryVO);
        }

        //Log message if we didn't modify anything -- TBD this should probably be an error
        if (tempLogbook == null) {

            getLogger().info("Logbook was not found to modify a LogbookEntry");
        }

        return new ResponseEntity<>(this.asValueObject(tempResult),
                                    HttpStatus.OK);

    }



    /**
     * Delete logbook entry for id
     * @param logbookEntryId Long
     */
    @DeleteMapping("logbook/entry/{logbookEntryId}")
    public void deleteLogbookEntry(Long logbookEntryId) {

        this.getLogbookEntryService().deleteLogbookEntry(logbookEntryId);

    }


    /**
     * Find all logbook entries for a logbook identified by id. Answer based on pageNumber
     * and pageSize
     * @param logbookId Long
     * @param pageNumber int
     * @param pageSize int
     * @return ResponseEntity
     */
    @GetMapping("logbook/{logbookId}/entries/{pageNumber}/{pageSize}")
    public ResponseEntity<List<LogbookEntryDTO>> findLogbookEntries(@PathVariable Long logbookId,
                                                                    @PathVariable int pageNumber,
                                                                    @PathVariable int pageSize) {

        List<LogbookEntry> tempEntries = new ArrayList<LogbookEntry>();
        Logbook            tempLogbook;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempEntries =
                    this.getLogbookEntryService().findLogbookEntriesForLogbook(tempLogbook,
                                                                               pageNumber,
                                                                               pageSize);
        }

        return new ResponseEntity<>(this.asLogbookEntryValueObjects(tempEntries),
                                    HttpStatus.OK);

    }

    /**
     * Answer aLogbookEntries as value objects
     * @param aLogbookEntries List
     * @return List
     */
    private List<LogbookEntryDTO> asLogbookEntryValueObjects(List<LogbookEntry> aLogbookEntries) {

        return aLogbookEntries.stream()
                              .map((anEntry)->this.asValueObject(anEntry))
                              .collect(Collectors.toList());

    }

    /**
     * Create an activity
     * @param logbookId Long
     * @param logbookEntryId Long
     * @param anActivityDTO ActivityDTO
     * @return ResponseEntity
     */
    @PostMapping("logbook/{logbookId}/entry/{logbookEntryId}/activity")
    public ResponseEntity<ActivityDTO> createLActivity(@PathVariable Long logbookId,
                                                       @PathVariable Long logbookEntryId,
                                                       @Valid @RequestBody ActivityDTO anActivityDTO) {

        Activity        tempResult = null;
        Logbook         tempLogbook = null;
        LogbookEntry    tempEntry = null;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempEntry = this.getLogbookEntryService().findLogbookEntryById(logbookEntryId);
            if (tempEntry != null) {

                tempResult =
                        this.getActivityService().createActivity(tempLogbook, tempEntry, anActivityDTO);


            }

        }

        //Log message if we didn't create anything -- TBD this should probably be an error
        if (tempLogbook == null ||
                tempEntry == null) {

            getLogger().info("Logbook or LogbookEntry were not found to create an activity");
        }

        return new ResponseEntity<>(this.asValueObject(tempResult),
                                    HttpStatus.OK);

    }


    /**
     * Create an activity
     * @param logbookId Long
     * @param logbookEntryId Long
     * @param anActivityDTO ActivityDTO
     * @return ResponseEntity
     */
    @PutMapping("logbook/{logbookId}/entry/{logbookEntryId}/activity")
    public ResponseEntity<ActivityDTO> modifyActivity(@PathVariable Long logbookId,
                                                      @PathVariable Long logbookEntryId,
                                                      @Valid @RequestBody ActivityDTO anActivityDTO) {

        Activity        tempResult = null;
        Logbook         tempLogbook = null;
        LogbookEntry    tempEntry = null;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempEntry = this.getLogbookEntryService().findLogbookEntryById(logbookEntryId);
            if (tempEntry != null) {

                tempResult =
                        this.getActivityService().modifyActivity(tempLogbook, tempEntry, anActivityDTO);


            }

        }

        //Log message if we didn't modify anything -- TBD this should probably be an error
        if (tempLogbook == null ||
                tempEntry == null) {

            getLogger().info("Logbook or LogbookEntry were not found to modify an activity");
        }

        return new ResponseEntity<>(this.asValueObject(tempResult),
                                    HttpStatus.OK);

    }


    /**
     * Create an activity
     * @param logbookId Long
     * @param logbookEntryId Long
     * @return ResponseEntity
     */
    @GetMapping("logbook/{logbookId}/entry/{logbookEntryId}/activities")
    public ResponseEntity<List<ActivityDTO>> findAllActivitiesForEntry(@PathVariable Long logbookId,
                                                                       @PathVariable Long logbookEntryId) {

        List<Activity>        tempResults = new ArrayList<Activity>();
        Logbook               tempLogbook = null;
        LogbookEntry          tempEntry = null;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempEntry = this.getLogbookEntryService().findLogbookEntryById(logbookEntryId);
            if (tempEntry != null) {

                tempResults =
                        this.getActivityService().findActivitiesForLogbookEntry(tempEntry);

            }

        }

        //Log message if we didn't create anything -- TBD this should probably be an error
        if (tempLogbook == null ||
                tempEntry == null) {

            getLogger().info("Logbook or LogbookEntry were not found to create an activity");
        }

        return new ResponseEntity<>(this.asActivityValueObjects(tempResults),
                                    HttpStatus.OK);

    }



    /**
     * Answer anActivities as value objects
     * @param anActivities List
     * @return List
     */
    private List<ActivityDTO> asActivityValueObjects(List<Activity> anActivities) {

        return anActivities.stream()
                           .map((anEntry)->this.asValueObject(anEntry))
                           .collect(Collectors.toList());

    }


    /**
     * Answer anActivity as a value object
     * @param anActivity Activity
     * @return ActivityDTO
     */
    private ActivityDTO asValueObject(Activity anActivity) {

        ActivityDTO tempResult = null;

        if (anActivity!= null) {

            tempResult = anActivity.asValueObject();
        }

        return tempResult;

    }

    /**
     * Find all logbook entries for a logbook identified by id.
     * @param logbookId Long
     * @return ResponseEntity
     */
    @GetMapping("logbook/{logbookId}/entries")
    public ResponseEntity<List<LogbookEntryDTO>> findLogbookEntries(@PathVariable Long logbookId) {

        List<LogbookEntryDTO> tempEntries = new ArrayList<>();
        Logbook            tempLogbook;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempEntries =
                    this.getLogbookEntryService().findAllLogbookEntriesForLogbook(tempLogbook);
        }

        return new ResponseEntity<>(tempEntries,
                                    HttpStatus.OK);

    }


    /**
     * Find count of logbook entries for a logbook identified by id.
     * @param logbookId Long
     * @return ResponseEntity
     */
    @GetMapping("logbook/{logbookId}/entriesCount")
    public ResponseEntity<Long> findLogbookEntriesCount(@PathVariable Long logbookId) {

        Long               tempCount = Long.valueOf(0l);
        Logbook            tempLogbook;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempCount =
                    this.getLogbookEntryService().findLogbookEntryCount(tempLogbook);
        }

        return new ResponseEntity<>(tempCount,
                                    HttpStatus.OK);

    }


}
