package com.mjdsoftware.logbook.api;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.User;
import com.mjdsoftware.logbook.dto.UserDTO;
import com.mjdsoftware.logbook.dto.oauth.UserAuthDTO;
import com.mjdsoftware.logbook.exception.UserAlreadyExistsException;
import com.mjdsoftware.logbook.service.LogbookService;
import com.mjdsoftware.logbook.service.OauthAccessService;
import com.mjdsoftware.logbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name="UserController",
        description="This REST controller provides an interface for managing users" +
                " for the application.")
@Slf4j
@RestController
@RequestMapping("/api/user/")
public class UserController {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private UserService userService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PROTECTED)
    private OauthAccessService oauthAccessService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookService logbookService;

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance of me on the following arguments
     * @param anOauthAccessService OauthAccessService
     * @param aUserService UserService
     */
    @Autowired
    public UserController(OauthAccessService anOauthAccessService,
                          UserService aUserService) {

        super();
        this.setOauthAccessService(anOauthAccessService);
        this.setUserService(aUserService);
    }

    /**
     * Create user. This also creates the corresponding user in keycloak if it does not exist
     * @param username String
     * @param password String
     * @return ResponseEntity
     */
    @Operation(summary = "Creates a user in the system as well as its corresponding user in Keycloak.",
            description = "Creates a user in the system as well as its corresponding user in Keycloak." +
                    "Note that the user must be in a role of app_admin to have access. It the Keycloak" +
                    " user exists, the user must be deleted and recreated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PreAuthorize("hasAuthority('ROLE_app_admin')")
    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestParam String username,
                                              @RequestParam String password) {

        ResponseEntity<UserDTO>            tempResult = null;
        User                               tempUser;


        tempUser = this.getUserService().findUserByUsername(username);
        if (tempUser == null) {

            tempUser = this.basicCreateUser(username);
            this.handleKeycloakUserCreation(username, password);

            tempResult = new ResponseEntity<>(this.asValueObject(tempUser), HttpStatus.OK);
        }
        else {
            this.logAndThrowUserAlreadyExistsException(username);
        }

        return tempResult;

    }

    /**
     * Handle keuycloak user creation
     * @param username String
     * @param password String
     */
    private void handleKeycloakUserCreation(String username, String password) {

        List<UserAuthDTO>                  tempKeycloakUsers;
        UserAuthDTO                        tempKeycloakUser;

        tempKeycloakUsers = this.findKeycloakUsers(username);
        if (tempKeycloakUsers.isEmpty()) {

            tempKeycloakUser =
                    this.getOauthAccessService().createUser(username, password);
            getLogger().info("Keycloak user successful created {}",
                             tempKeycloakUser.getId());
        }
        else {

            getLogger().info("Keycloak user already exists for usernamne {}. Not created",
                    username);
        }

    }

    /**
     * Create and persist user for username
     * @param username String
     * @return User
     */
    private User basicCreateUser(String username) {

        UserDTO tempResult;

        //Create dto
        tempResult = new UserDTO(username);

        //Create and persist user
        return this.getUserService().createUser(tempResult);

    }


    /**
     * Find keycloak user
     * @param aUsername String
     * @return List
     */
    private List<UserAuthDTO> findKeycloakUsers(String aUsername) {

        return this.getOauthAccessService().retrieveUser(aUsername);
    }


    /**
     * Find user by username.
     * @param username String
     * @return ResponseEntity
     */
    @GetMapping("name/{username}")
    @Operation(summary = "Find user by name.",
            description = "Find user by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PreAuthorize("hasAuthority('ROLE_app_admin')")
    public ResponseEntity<UserDTO> findUserByName(@PathVariable  String username) {

        User     tempUser;

        tempUser = this.getUserService().findUserByUsername(username);

        return new ResponseEntity<>(this.asValueObject(tempUser),
                                    HttpStatus.OK);
    }

    /**
     * Find user by id.
     * @param id Long
     * @return ResponseEntity
     */
    @GetMapping("id/{id}")
    @Operation(summary = "Find user by id.",
            description = "Find user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @PreAuthorize("hasAuthority('ROLE_app_admin')")
    public ResponseEntity<UserDTO> findUserById(@PathVariable  Long id) {

        User     tempUser;

        tempUser = this.getUserService().findUserById(id);

        return new ResponseEntity<>(this.asValueObject(tempUser), HttpStatus.OK);
    }

    /**
     * Delete user by id
     * @param id Long
     */
    @Operation(summary = "Delete user by id.",
            description = "Delete user by id, as well as its corresponding Keycloak user(s).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error")
    })
    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ROLE_app_admin')")
    public void deleteUserById(@PathVariable  Long id) {

        User                tempUser;

        tempUser = this.getUserService().findUserById(id);
        if (tempUser != null) {

            this.deleteLogbooksForUser(tempUser);
            this.deleteKeyCloakUsers(tempUser);
            this.getUserService().deleteUserById(id);

        }

    }

    /**
     * Delete logbooks for aUser
     * @param aUser User
     */
    private void deleteLogbooksForUser(User aUser) {

        for (Logbook aLogbook: aUser.getLogbooks()) {

            this.getLogbookService().deleteLogbook(aLogbook.getId());

        }

    }


    /**
     * Delete all keycloak users that correspond to aUser
     * @param aUser User
     */
    private void deleteKeyCloakUsers(User aUser) {

        List<UserAuthDTO>   tempKeycloakUsers;

        tempKeycloakUsers = this.findKeycloakUsers(aUser.getUsername());
        for (UserAuthDTO aKeycloakUser: tempKeycloakUsers) {

            this.getOauthAccessService().deleteUser(aKeycloakUser.getId());
        }

    }


    /**
     * Answer aUsers as value objects
     * @param aUsers List
     * @return List
     */
    private List<UserDTO> asLogbookValueObjects(List<User> aUsers) {

        return aUsers.stream()
                     .map((aUser)->this.asValueObject(aUser))
                     .collect(Collectors.toList());

    }


    /**
     * Answer aUser as a value object
     * @param aUser User
     * @return UserDTO
     */
    private UserDTO asValueObject(User aUser) {

        UserDTO tempResult = null;

        if (aUser != null) {

            tempResult = aUser.asValueObject();
        }

        return tempResult;

    }

    /**
     * Log and throw illegal argument exception
     * @param aMessage String
     */
    private void logAndThrowIllegalArgumentException(String aMessage) {

        getLogger().error(aMessage);

        throw new IllegalArgumentException(aMessage);


    }

    /**
     * Log and throw user already exists argument exception
     * @param aUsername String
     */
    private void logAndThrowUserAlreadyExistsException(String aUsername) {

        getLogger().error(aUsername);

        throw new UserAlreadyExistsException(aUsername);


    }

}
