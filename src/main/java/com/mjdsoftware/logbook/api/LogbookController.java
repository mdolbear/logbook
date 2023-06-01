package com.mjdsoftware.logbook.api;

import com.mjdsoftware.logbook.domain.entities.Activity;
import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.LogbookEntry;
import com.mjdsoftware.logbook.domain.entities.User;
import com.mjdsoftware.logbook.dto.ActivityDTO;
import com.mjdsoftware.logbook.dto.LogbookDTO;
import com.mjdsoftware.logbook.dto.LogbookEntryDTO;
import com.mjdsoftware.logbook.exception.LogbookNotFoundException;
import com.mjdsoftware.logbook.exception.UserNotFoundException;
import com.mjdsoftware.logbook.service.ActivityService;
import com.mjdsoftware.logbook.service.LogbookEntryService;
import com.mjdsoftware.logbook.service.LogbookService;
import com.mjdsoftware.logbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name="LogbookController", description="This REST controller provides an interface for managing" +
        " a logbook.")
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

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private UserService userService;


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
     * @param anActivityService ActivityService
     * @param aUserService UserService
     */
    @Autowired
    public LogbookController(LogbookService aLogbookService,
                             LogbookEntryService aLogbookEntryService,
                             ActivityService anActivityService,
                             UserService aUserService) {

        super();
        this.setLogbookService(aLogbookService);
        this.setLogbookEntryService(aLogbookEntryService);
        this.setActivityService(anActivityService);
        this.setUserService(aUserService);

    }


    /**
     * Answer all logbooks
     * @return ResponseEntity
     */
    @Operation(summary = "Find all logbooks",
            description = "Answers all logbooks in the system. This is a privileged operation" +
                    " that takes admin level privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @GetMapping("logbooks")
    @PreAuthorize("hasAuthority('ROLE_app_admin')")
    public ResponseEntity<List<LogbookDTO>> findAllLogbooks(Authentication authentication,
                                                            HttpServletRequest servletRequest,
                                                            @AuthenticationPrincipal Jwt token) {

        List<Logbook> tempLogbooks;

        tempLogbooks = this.getLogbookService().findAllLogbooks();

        return new ResponseEntity<>(this.asLogbookValueObjects(tempLogbooks),
                                    HttpStatus.OK);

    }

    /**
     * Find logbook by name
     * @param name String
     * @return ResponseEntity
     */
    @Operation(summary = "Find logbook by its unique name",
            description = "Answers all logbooks in the system. This is a privileged operation" +
                    " that takes admin level privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PreAuthorize("hasAuthority('ROLE_app_admin')")
    @GetMapping("logbook/{name}")
    public ResponseEntity<LogbookDTO> findLogbookByName(Authentication authentication,
                                                        HttpServletRequest servletRequest,
                                                        @AuthenticationPrincipal Jwt token,
                                                        @PathVariable String name) {

        Logbook tempResult;

        tempResult = this.getLogbookService().findByName(name);

        return new ResponseEntity<>(this.asValueObject(tempResult),
                                    HttpStatus.OK);

    }

    /**
     * Delete logbook by id
     * @param id Long
     * @return ResponseEntity
     */
    @Operation(summary = "Delete logbook for id",
            description = "Delete logbook by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #id)")
    @DeleteMapping("logbook/{id}")
    public void deleteLogbook(Authentication authentication,
                              HttpServletRequest servletRequest,
                              @AuthenticationPrincipal Jwt token,
                              @PathVariable Long id) {

        this.getLogbookService().deleteLogbook(id);

    }


    /**
     * Create a logbook
     * @param aLogbookVO LogbookDTO
     * @return ResponseEntity
     */
    @Operation(summary = "Creates a logbook",
            description = "Creates a logbook.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PostMapping("user/{userId}/logbook")
    @PreAuthorize("@methodSecurityService.isAccessAllowed(#authentication, #servletRequest, #token, #userId)")
    public ResponseEntity<LogbookDTO> createLogbook(Authentication authentication,
                                                    HttpServletRequest servletRequest,
                                                    @AuthenticationPrincipal Jwt token,
                                                    @PathVariable Long userId,
                                                    @Valid @RequestBody LogbookDTO aLogbookVO) {

        Logbook tempResult = null;
        User    tempUser;

        tempUser = this.getUserService().findUserById(userId);
        if (tempUser != null) {

            tempResult = this.getLogbookService().createLogbook(tempUser,
                                                                aLogbookVO);
        }
        else {
            throw new UserNotFoundException("User not found when creating logbook");
        }

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
    @Operation(summary = "Creates a logbook entry for a logbook",
            description = "Creates a logbook entry for a logbook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PostMapping("logbook/{logbookId}/entry")
    @PreAuthorize("@methodSecurityService.isAccessAllowed(#authentication, #servletRequest, #token)")
    public ResponseEntity<LogbookEntryDTO> createLogbookEntry(Authentication authentication,
                                                              HttpServletRequest servletRequest,
                                                              @AuthenticationPrincipal Jwt token,
                                                              @PathVariable Long logbookId,
                                                              @Valid @RequestBody LogbookEntryDTO aLogbookEntryVO) {

        LogbookEntry tempResult = null;
        Logbook      tempLogbook;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempResult =
                    this.getLogbookEntryService()
                        .createLogbookEntry(tempLogbook, aLogbookEntryVO);
        }
        else {

            getLogger().info("Logbook was not found to create a LogbookEntry");
            throw new LogbookNotFoundException(logbookId);

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
    @Operation(summary = "Modify a logbook entry for a logbook",
            description = "Modify a logbook entry for a logbook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PutMapping("logbook/{logbookId}/entry")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<LogbookEntryDTO> modifyLogbookEntry(Authentication authentication,
                                                              HttpServletRequest servletRequest,
                                                              @AuthenticationPrincipal Jwt token,
                                                              @PathVariable Long logbookId,
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
    @Operation(summary = "Delete a logbook entry for a logbook",
            description = "Delete a logbook entry for a logbook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @DeleteMapping("logbook/{logbookId}/entry/{logbookEntryId}")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public void deleteLogbookEntry(Authentication authentication,
                                   HttpServletRequest servletRequest,
                                   @AuthenticationPrincipal Jwt token,
                                   @PathVariable Long logbookId,
                                   Long logbookEntryId) {

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
    @Operation(summary = "Find all logbook entries for a logbook",
            description = "Find all logbook entries for a logbook. Note that this method will bring back" +
                    " all logbook entries, including their comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @GetMapping("logbook/{logbookId}/entries/{pageNumber}/{pageSize}")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<List<LogbookEntryDTO>> findLogbookEntries(Authentication authentication,
                                                                    HttpServletRequest servletRequest,
                                                                    @AuthenticationPrincipal Jwt token,
                                                                    @PathVariable Long logbookId,
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
     * Find all logbook entries for a logbook identified by id. Answer based on pageNumber
     * and pageSize
     * @param logbookId Long
     * @param pageNumber int
     * @param pageSize int
     * @return ResponseEntity
     */
    @Operation(summary = "Find all logbook entries for a logbook.",
            description = "Find all logbook entries for a logbook. Note that this method will only bring back" +
                    " shallow copies of logbook entries, leaving out their comments. Its a paged interface.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @GetMapping("logbook/{logbookId}/all-entries/{pageNumber}/{pageSize}")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<List<LogbookEntryDTO>> findAllLogbookEntries(Authentication authentication,
                                                                       HttpServletRequest servletRequest,
                                                                       @AuthenticationPrincipal Jwt token,
                                                                       @PathVariable Long logbookId,
                                                                       @PathVariable int pageNumber,
                                                                       @PathVariable int pageSize) {

        List<LogbookEntryDTO> tempEntries = new ArrayList<LogbookEntryDTO>();
        Logbook               tempLogbook;

        tempLogbook = this.getLogbookService().findLogbookById(logbookId);
        if (tempLogbook != null) {

            tempEntries =
                    this.getLogbookEntryService().findAllLogbookEntriesForLogbook(tempLogbook,
                            pageNumber,
                            pageSize);
        }

        return new ResponseEntity<>(tempEntries,
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
    @Operation(summary = "Creates a activity for a logbook entry",
            description = "Creates a activity for a logbook entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PostMapping("logbook/{logbookId}/entry/{logbookEntryId}/activity")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<ActivityDTO> createLActivity(Authentication authentication,
                                                       HttpServletRequest servletRequest,
                                                       @AuthenticationPrincipal Jwt token,
                                                       @PathVariable Long logbookId,
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
    @Operation(summary = "Modifies a activity for a logbook entry",
            description = "Modifies a activity for a logbook entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PutMapping("logbook/{logbookId}/entry/{logbookEntryId}/activity")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<ActivityDTO> modifyActivity(Authentication authentication,
                                                      HttpServletRequest servletRequest,
                                                      @AuthenticationPrincipal Jwt token,
                                                      @PathVariable Long logbookId,
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
     * Find all activities for a logbook entry
     * @param logbookId Long
     * @param logbookEntryId Long
     * @return ResponseEntity
     */
    @Operation(summary = "Find all activities for a logbook entry",
            description = "Find all activities for a logbook entry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @GetMapping("logbook/{logbookId}/entry/{logbookEntryId}/activities")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<List<ActivityDTO>> findAllActivitiesForEntry(Authentication authentication,
                                                                       HttpServletRequest servletRequest,
                                                                       @AuthenticationPrincipal Jwt token,
                                                                       @PathVariable Long logbookId,
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
    @Operation(summary = "Find all logbook entries for a logbook",
            description = "Find all logbook entries for a logbook. Note that this method will only bring back" +
                    " shallow copies of logbook entries, leaving out their comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @GetMapping("logbook/{logbookId}/entries")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<List<LogbookEntryDTO>> findLogbookEntries(Authentication authentication,
                                                                    HttpServletRequest servletRequest,
                                                                    @AuthenticationPrincipal Jwt token,
                                                                    @PathVariable Long logbookId) {

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
    @Operation(summary = "Get count of logbook entries for a logbook",
            description = "Get count of logbook entries for a logbook.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @GetMapping("logbook/{logbookId}/entriesCount")
    @PreAuthorize("@methodSecurityService.isAccessAllowedForLogbook(#authentication, #servletRequest, #token, #logbookId)")
    public ResponseEntity<Long> findLogbookEntriesCount(Authentication authentication,
                                                        HttpServletRequest servletRequest,
                                                        @AuthenticationPrincipal Jwt token,
                                                        @PathVariable Long logbookId) {

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
