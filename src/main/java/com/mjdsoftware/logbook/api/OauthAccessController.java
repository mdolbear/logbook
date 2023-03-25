package com.mjdsoftware.logbook.api;


import com.mjdsoftware.logbook.dto.oauth.KeycloakUserDTO;
import com.mjdsoftware.logbook.dto.oauth.OauthToken;
import com.mjdsoftware.logbook.dto.oauth.UserAuthDTO;
import com.mjdsoftware.logbook.service.OauthAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
public class OauthAccessController {

    @Autowired
    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PROTECTED)
    private OauthAccessService oauthAccessService;

    /**
     * Answer a default instance
     */
    public OauthAccessController() {

        super();
    }

    /**
     * Answer a normal oauth token given username, password, and clientId.
     * If no aClientId is provided, use the default one
     *
     * @param username String
     * @param password String
     * @param clientId String
     * @return OauthToken
     */
    @Operation(summary = "Gets an Oauth token for a user",
            description = "Gets an Oauth token for the user with the given username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error (check the server's logs for more info)")
    })
    @PostMapping("/clientToken")
    public ResponseEntity<OauthToken> getClientToken(@RequestParam String username,
                                                     @RequestParam String password,
                                                     @RequestParam(required = false) String clientId) {

        OauthToken tempResult =
                this.getOauthAccessService().getClientToken(username,
                                                            password,
                                                            clientId);
        return new ResponseEntity<>(tempResult, HttpStatus.OK);
    }

    /**
     * Answer a normal oauth token given the value of aRefreshToken
     * @param aRefreshToken String
     * @param clientId String
     * @return OauthToken
     */
    @Operation(summary = "Gets an Oauth token given a refresh token",
            description = "Gets a new Oauth token for a user given a valid refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error (check the server's logs for more info)")
    })
    @PostMapping("/refreshToken")
    public ResponseEntity<OauthToken> refreshToken(@RequestParam String aRefreshToken,
                                                   @RequestParam(required = false) String clientId) {

        OauthToken tempResult = getOauthAccessService().refreshToken(aRefreshToken, clientId);
        return new ResponseEntity<>(tempResult, HttpStatus.OK);
    }


    @Operation(summary = "Gets a Keycloak user",
            description = "Gets a user from Keycloak.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error (check the server's logs for more info)")
    })
    @GetMapping("/user/retrieve")
    public ResponseEntity<KeycloakUserDTO>
                retrieveUser(Authentication authentication,
                             @RequestParam(required = false) String organizationId,
                             @RequestParam String username) {

        UserAuthDTO tempResult = this.getOauthAccessService().retrieveUser(username);
        return this.createSuccessfulResponse(tempResult);
    }


    /**
     * Create user for the following parameters
     * @param username String
     * @param password String
     */
    @Operation(summary = "Creates a Keycloak user",
            description = "Creates a user in Keycloak.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error (check the server's logs for more info)")
    })
    //TODO - need validation at the user admin level to perform this operation
    ///TODO - Will need to validate that Authentication has principal with role of Admin
    @PostMapping("/user/create")
    public ResponseEntity<KeycloakUserDTO>
                createUser(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String aSupplierId) {

        UserAuthDTO tempResult =
                this.getOauthAccessService().createUser(username, password);

        return this.createSuccessfulResponse(tempResult);
    }

    /**
     * Delete user for a keycloakUserId
     * @param keycloakUserId String
     */
    @Operation(summary = "Deletes a Keycloak user",
            description = "Deletes a user from Keycloak.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success"),
            @ApiResponse(responseCode = "400",
                    description = "General client error"),
            @ApiResponse(responseCode = "500",
                    description = "General server error (check the server's logs for more info)")
    })
    //TODO - need validation at the user admin level to perform this operation
    //TODO - Will need to validate that Authentication has principal with role of Admin
    @DeleteMapping("/user")
    public ResponseEntity<KeycloakUserDTO>
    deleteUser(@RequestParam String keycloakUserId) {

        this.getOauthAccessService().deleteUser(keycloakUserId);
        return this.createSuccessfulResponse();
    }




    /**
     * Create response with success status
     * @return ResponseEntity
     */
    private ResponseEntity<KeycloakUserDTO> createSuccessfulResponse() {

        return new ResponseEntity<>(null, HttpStatus.OK);

    }

    /**
     * Create response with success status
     * @param aUser UserAuthDTO
     * @return ResponseEntity
     */
    private ResponseEntity<KeycloakUserDTO> createSuccessfulResponse(UserAuthDTO aUser) {

        KeycloakUserDTO     tempResult;

        tempResult = this.createFrom(aUser);

        return new ResponseEntity<>(tempResult, HttpStatus.OK);

    }

    /**
     * Create dto from aUserAuthDTO. UserAuthDTO transfers data to/from keycloak, so cannot
     * subclass AbstractDTO
     * @param aUserAuthDTO UserAuthDTO
     * @return KeycloakUserDTO
     */
    private KeycloakUserDTO createFrom(UserAuthDTO aUserAuthDTO) {

        KeycloakUserDTO tempResult = null;

        if (aUserAuthDTO != null) {

            tempResult =
            new KeycloakUserDTO(aUserAuthDTO.getId(),
                                aUserAuthDTO.getCreatedTimestamp(),
                                aUserAuthDTO.getUsername(),
                                aUserAuthDTO.isEnabled(),
                                aUserAuthDTO.isEmailVerified(),
                                aUserAuthDTO.getAttributes());

        }

        return tempResult;

    }

}
