package com.mjdsoftware.logbook.utils;


import com.mjdsoftware.logbook.dto.oauth.CreateUserDTO;
import com.mjdsoftware.logbook.dto.oauth.CredentialsDTO;
import com.mjdsoftware.logbook.dto.oauth.OauthToken;
import com.mjdsoftware.logbook.dto.oauth.UserAuthDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
@Slf4j
public class KeyCloakUtilities {

    //Constants
    private static final String DEFAULT_TOKEN_CLIENT_ID = "newClient";

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PROTECTED)
    private RestUtils restUtils;

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PUBLIC)
    @Value("${app.authorizationServerHostName}")
    private String authorizationServerHostName;

    @Getter(value = AccessLevel.PRIVATE)
    @Value("${app.authorizationServerClientSecret}")
    private String authorizationServerClientSecret;

    @Getter(value=AccessLevel.PRIVATE)
    @Value("${app.authorizationServerRealmName}")
    private String authorizationServerRealmName;

    @Getter(value=AccessLevel.PRIVATE)
    @Value("${app.authorizationServerTokenUri}")
    private String authorizationServerTokenUri;

    @Getter(value=AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminTokenUri}")
    private String authorizationServerAdminTokenUri;

    @Getter(value=AccessLevel.PRIVATE)
    @Value("${app.authorizationServerUserUri}")
    private String authorizationServerUserUri;

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PUBLIC)
    @Value("${app.authorizationServerAdminUser}")
    private String authorizationServerAdminUser;

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PUBLIC)
    @Value("${app.authorizationServerAdminPassword}")
    private String authorizationServerAdminPassword;

    @Getter(value = AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminClientId}")
    private String authorizationServerAdminClientId;


    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PROTECTED) // exposed for unit tests
    private MessageSource messageSource;

    //Constants
    public static final String BEARER_TOKEN = "Bearer ";

    private static final String AUTHORIZATION = "Authorization";
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String GRANT_TYPE = "grant_type";
    private static final String SCOPE = "scope";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String REFRESH_TOKEN = "refresh_token";

    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String REFRESH_GRANT_TYPE = "refresh_token";


    private static final String TOKEN_URI_2 = "/protocol/openid-connect/token";
    private static final String FIND_USER_URI_2 = "/users?username=";
    private static final String PUT_USER_URI_2 = "/users/";
    private static final String CREATE_USER_URI_2 = "/users";
    private static final String DELETE_USER_URI_2 = "/users/";

    private String HTTP = "http://";


    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance of me for the following arguments:
     * @param aUtils RestUtils
     * @param aSource MessageSource
     */
    @Autowired
    public KeyCloakUtilities(RestUtils aUtils, MessageSource aSource) {

        this.setRestUtils(aUtils);
        this.setMessageSource(aSource);
    }


    /**
     * Retrieve user for aUsernameToFind
     * @param aToken OauthToken
     * @return OauthToken
     */
    public UserAuthDTO retrieveUser(@NonNull OauthToken aToken,
                                    @NonNull String aUserNameToFind)  {

        String                                      tempUrl;
        MultiValueMap<String, String>               tempCredentials;
        ResponseEntity<List<UserAuthDTO>>           tempUserRepresentations;

        //Parameters
        tempUrl = this.formulateFindUserUrl(aUserNameToFind);
        tempCredentials = this.getHeadersWithAdminCredentialsAndApplicationJsonMediaType(aToken.getAccessToken());

        //Make rest call
        tempUserRepresentations =
                this.getRestUtils()
                     .getForObjectList(new RestTemplate(),
                                       tempUrl,
                                       new ParameterizedTypeReference<List<UserAuthDTO>>(){},
                                       tempCredentials);


        return this.diagnoseAndReturnUserResult(tempUserRepresentations);

    }


    /**
     * Create a keycloak. To facilitate Multi-tenancy with Keycloak, there are two types of users:
     * 1) Org Owned Users - These are users that belong to an organization.
     * 2) Special Users - These types of users have access to all organizations.
     *
     * Sending a null anOrganizationId will create the special type of user. Otherwise, specify anOrganizationId
     * to create an org-owned user.
     *
     * @param aToken OauthToken
     * @param aUsername String
     * @param aPassword String
     */
    public void createUser(@NonNull OauthToken aToken,
                           @NonNull String aUsername,
                           @NonNull String aPassword) {

        CredentialsDTO tempCredentials;
        CreateUserDTO tempUser;

        tempCredentials = new CredentialsDTO(aPassword);
        tempUser = new CreateUserDTO(aUsername);
        tempUser.addCredential(tempCredentials);

        this.createUserViaPostInvocation(aToken, tempUser);

    }








    /**
     * Modify user for aUsernameToModify with aCustomClaimChanges
     * @param aToken OauthToken
     * @param aUsernameToModify String
     * @param aCustomClaimChanges Map
     */
    public void modifyUserCustomClaims(@NonNull OauthToken aToken,
                                       @NonNull String aUsernameToModify,
                                       @NonNull Map<String, Object> aCustomClaimChanges)  {


        UserAuthDTO                    tempUser;

        //Find user
        tempUser = this.retrieveUser(aToken,
                                     aUsernameToModify);
        this.validateReturnedUser(tempUser,
                                  aUsernameToModify);

        //Modify returned user
        this.modifyUserWithAttributes(tempUser, aCustomClaimChanges);

        //Modify claims
        this.modifyUserViaPutInvocation(aToken, tempUser);


    }


    /**
     * Modify user via put invocation
     * @param aToken OauthToken
     * @param aUser UserAuthDTO
     */
    private void modifyUserViaPutInvocation(OauthToken aToken,
                                            UserAuthDTO aUser) {

        String                                      tempUrl;
        MultiValueMap<String, String>               tempCredentials;

        //Parameters
        tempUrl = this.formulatePutUserUrl(aUser.getId());
        tempCredentials =
                this.getHeadersWithAdminCredentialsAndApplicationJsonMediaType(aToken.getAccessToken());

        //Make rest call
        this.getRestUtils()
            .put(new RestTemplate(),
                 tempUrl,
                 aUser,
                 tempCredentials);

    }

    /**
     * Delete user for aKeycloakUserId
     * @param aToken OauthToken
     * @param aKeycloakUserId String
     */
    public void deleteUser(@NonNull OauthToken aToken,
                           @NonNull String aKeycloakUserId)  {

        this.deleteUserInvocation(aToken,aKeycloakUserId);

    }


    /**
     * Delete user via put invocation
     * @param aToken OauthToken
     * @param aUserId String
     */
    private void deleteUserInvocation(OauthToken aToken,
                                      String aUserId) {

        String                                      tempUrl;
        MultiValueMap<String, String>               tempCredentials;

        //Parameters
        tempUrl = this.formulateDeleteUserUrl(aUserId);
        tempCredentials =
                this.getHeadersWithAdminCredentialsAndApplicationJsonMediaType(aToken.getAccessToken());

        //Make rest call
        this.getRestUtils()
            .performDeleteWithAuthentication(new RestTemplate(),
                                            tempUrl,
                                            UserAuthDTO.class,
                                            tempCredentials);

    }

    /**
     * Modify user via post invocation
     * @param aToken OauthToken
     * @param aUser UserAuthDTO
     */
    private void createUserViaPostInvocation(OauthToken aToken,
                                             CreateUserDTO aUser) {

        String                                      tempUrl;
        MultiValueMap<String, String>               tempCredentials;

        //Parameters
        tempUrl = this.formulateCreateUserUrl();
        tempCredentials =
                this.getHeadersWithAdminCredentialsAndApplicationJsonMediaType(aToken.getAccessToken());

        //Make rest call
        this.getRestUtils()
                .postForObject(new RestTemplate(),
                               tempUrl,
                               aUser,
                               CreateUserDTO.class,
                               new HashMap<>(), //Not used on this invocation
                               tempCredentials);

    }

    /**
     * Modify aUser with aCustomClaimChanges
     * @param aUser UserAuthDTO
     * @param aCustomClaimChanges Map
     */
    private void modifyUserWithAttributes(UserAuthDTO aUser,
                                          Map<String, Object> aCustomClaimChanges) {

        for (Map.Entry<String,Object> anEntry: aCustomClaimChanges.entrySet()) {

            //Write null if we actually have a null
            aUser.setSingleAttribute(anEntry.getKey(),
                                     ((anEntry.getValue()) == null) ? null: anEntry.getValue().toString());

        }


    }

    /**
     * Validate returned user
     * @param aUser UserAuthDTO
     */
    private void validateReturnedUser(UserAuthDTO aUser,
                                      String aUsername) {

        if (aUser == null || aUser.getUsername() == null || aUser.getId() == null) {

            throw new RuntimeException("Invalid user returned from REST call: " + aUsername
                    + " from user query");
        }

    }


    /**
     * Diagnose and return result
     * @param anEntity ResponseEntity
     * @return String
     */
    private UserAuthDTO
    diagnoseAndReturnUserResult(ResponseEntity<List<UserAuthDTO>> anEntity) {

        List<UserAuthDTO> tempRepresentation;


        tempRepresentation = anEntity.getBody();
        if (!anEntity.getStatusCode().equals(HttpStatus.OK) ||
                        tempRepresentation == null) {

            throw new RuntimeException("Error returned from REST call: " + anEntity.getStatusCode()
                    + " for token result invocation");
        }

        //Create return result
        return this.asUserAuthDTO(tempRepresentation);


    }

    /**
     * Answer a UserAuthDTO from a aRepresentations
     * @param aRepresentations List
     * @return UserAuthDTO
     */
    private UserAuthDTO asUserAuthDTO(List<UserAuthDTO> aRepresentations) {

        UserAuthDTO             tempResult = null;

        if (!aRepresentations.isEmpty()) {
            tempResult = aRepresentations.get(0);
        }

        return tempResult;

    }


    /**
     * Retrieve token
     * @param aUsername String
     * @param aPassword String
     * @param aClientId String
     * @return OauthToken
     */
    public OauthToken retrieveToken(@NonNull String aUsername,
                                    @NonNull String aPassword,
                                    String aClientId) {

        aClientId = this.parseClientIdAndAnswerDefaultIfNotDefined(aClientId);

        String tempUrl = this.formulateRegularTokenUrl();
        try {
            HttpEntity<MultiValueMap<String, Object>> formData = 
                    createFormDataForTokenInvocation(aUsername,
                                                     aPassword,
                                                     PASSWORD_GRANT_TYPE,
                                                     "write",
                                                     aClientId,
                                                     getAuthorizationServerClientSecret());
            ResponseEntity<OauthToken> tempResult =
                    getRestUtils().postForEntity(new RestTemplate(),
                                                 tempUrl,
                                                 formData,
                                                 OauthToken.class);

             return this.diagnoseAndReturnResult(tempResult);
        }
        catch (Exception exc) {

            String msg = "User {username=" + aUsername + "} is not retrievable from KeyCloak: " + 
                         exc.getMessage();
            throw new IllegalStateException(msg + " {username: " + aUsername + "}", exc);
        }

    }

    /**
     * Retrieve token for admin
     * @param aUsername String
     * @param aPassword String
     * @param aClientId String
     * @return OauthToken
     */
    public OauthToken retrieveTokenForAdmin(@NonNull String aUsername,
                                            @NonNull String aPassword,
                                            String aClientId) {

        aClientId = this.parseClientIdAndAnswerDefaultIfNotDefined(aClientId);
        
        String tempUrl = this.formulateAdminTokenUrl();       
        try {
            HttpEntity<MultiValueMap<String, Object>> formData = 
                    createFormDataForAdminTokenInvocation(aUsername,
                                                          aPassword,
                                                          PASSWORD_GRANT_TYPE,
                                                          aClientId);
            ResponseEntity<OauthToken> tempResult = getRestUtils()
                    .postForEntity(new RestTemplate(),
                                   tempUrl,
                                   formData,
                                   OauthToken.class);

            return this.diagnoseAndReturnResult(tempResult);       
        }
        catch(Exception exc) {
            String msg = "User {username=" + aUsername + "} is not retrievable from KeyCloak: " +
                         exc.getMessage();
            throw new IllegalStateException(msg + "{ username: " + aUsername + "}" + exc);
        }
    }

    /**
     * Refresh token and return a new token
     * @param aRefreshToken String
     * @param aClientId String
     * @return OauthToken
     */
    public OauthToken refreshToken(@NonNull String aRefreshToken,
                                   String aClientId) {

        aClientId = this.parseClientIdAndAnswerDefaultIfNotDefined(aClientId);

        String tempUrl = this.formulateRegularTokenUrl();
        try {
            HttpEntity<MultiValueMap<String, Object>> formData =
                    createFormDataForRefreshTokenInvocation(aRefreshToken,
                                                            REFRESH_GRANT_TYPE,
                                                    "write",
                                                            aClientId,
                                                            this.getAuthorizationServerClientSecret());
            ResponseEntity<OauthToken> tempResult =
                    getRestUtils().postForEntity(new RestTemplate(),
                                                tempUrl,
                                                formData,
                                                OauthToken.class);

            return this.diagnoseAndReturnResult(tempResult);
        }
        catch (Exception exc) {

            String msg = "Refresh Token failed for token {refreshToken=" + aRefreshToken + "} from KeyCloak: " +
                            exc.getMessage();

            throw new IllegalStateException (msg + " {refreshToken: " + aRefreshToken + "}", exc);
        }

    }

    /**
     * Diagnose and return result
     * @param anEntity ResponseEntity
     * @return String
     */
    private OauthToken diagnoseAndReturnResult(ResponseEntity<OauthToken> anEntity) {
        OauthToken tempResult = anEntity.getBody();
        if(!anEntity.getStatusCode().equals(HttpStatus.OK) || tempResult == null) {
            throw new RuntimeException("Error returned from REST call: " + anEntity.getStatusCode() +
                                       " for token result invocation");
        }
        return tempResult;
    }


    /**
     * Create form data for regular token
     * @param aUsername String
     * @param aPassword String
     * @param aGrantType String
     * @param aScope String
     * @param aClientId String
     * @param aClientSecret String
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, Object>> createFormDataForTokenInvocation(String aUsername,
                                                                                       String aPassword,
                                                                                       String aGrantType,
                                                                                       String aScope,
                                                                                       String aClientId,
                                                                                       String aClientSecret) {

        MultiValueMap<String, Object>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders                       tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tempParams.add(USER_NAME, aUsername);
        tempParams.add(PASSWORD, aPassword);
        tempParams.add(GRANT_TYPE, aGrantType);
        tempParams.add(SCOPE, aScope);
        tempParams.add(CLIENT_ID, aClientId);
        tempParams.add(CLIENT_SECRET, aClientSecret);

        return new HttpEntity<>(tempParams, tempHeaders);


    }

    /**
     * Create form data for refresh of a token
     * @param aRefreshTokenValue String
     * @param aGrantType String
     * @param aScope String
     * @param aClientId String
     * @param aClientSecret String
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, Object>> createFormDataForRefreshTokenInvocation(String aRefreshTokenValue,
                                                                                              String aGrantType,
                                                                                              String aScope,
                                                                                              String aClientId,
                                                                                              String aClientSecret) {

        MultiValueMap<String, Object>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders                       tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tempParams.add(REFRESH_TOKEN, aRefreshTokenValue);
        tempParams.add(GRANT_TYPE, aGrantType);
        tempParams.add(SCOPE, aScope);
        tempParams.add(CLIENT_ID, aClientId);
        tempParams.add(CLIENT_SECRET, aClientSecret);

        return new HttpEntity<>(tempParams, tempHeaders);


    }



    /**
     * Create form data for admin token
     * @param aUsername String
     * @param aPassword String
     * @param aGrantType String
     * @param aClientId String
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, Object>> createFormDataForAdminTokenInvocation(String aUsername,
                                                                                            String aPassword,
                                                                                            String aGrantType,
                                                                                            String aClientId) {

        MultiValueMap<String, Object>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders                       tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        tempParams.add(USER_NAME, aUsername);
        tempParams.add(PASSWORD, aPassword);
        tempParams.add(GRANT_TYPE, aGrantType);
        tempParams.add(CLIENT_ID, aClientId);

        return new HttpEntity<>(tempParams, tempHeaders);


    }


    /**
     * Answer headers with the token
     * @param aToken String
     * @return  MultiValueMap
     */
    private MultiValueMap<String, String>
                getHeadersWithAdminCredentialsAndApplicationJsonMediaType(String aToken) {

        HttpHeaders   tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_JSON);
        tempHeaders.add(AUTHORIZATION, BEARER_TOKEN + aToken);

        return tempHeaders;


    }

    /**
     * Formulate regular token url
     * @return String
     */
    private String formulateRegularTokenUrl() {

        return HTTP + this.getAuthorizationServerHostName()
                        + this.getAuthorizationServerTokenUri()
                            + this.getAuthorizationServerRealmName() + TOKEN_URI_2;
    }

    /**
     * Formulate token url
     * @return String
     */
    private String formulateAdminTokenUrl() {

        return HTTP + this.getAuthorizationServerHostName() +
                            this.getAuthorizationServerAdminTokenUri();
    }

    /**
     * Formulate find user token for aUsername
     * @param aUsername String
     * @return String
     */
    private String formulateFindUserUrl(String aUsername) {

        return HTTP + this.getAuthorizationServerHostName() +
                this.getAuthorizationServerUserUri() +
                this.getAuthorizationServerRealmName() + FIND_USER_URI_2 + aUsername;
    }

    /**
     * Formulate create user token for aUsername
     * @return String
     */
    private String formulateCreateUserUrl() {

        return HTTP + this.getAuthorizationServerHostName() +
                this.getAuthorizationServerUserUri() +
                this.getAuthorizationServerRealmName() + CREATE_USER_URI_2;
    }

    /**
     * Formulate put user for anId
     * @param anId String
     * @return String
     */
    private String formulatePutUserUrl(String anId) {

        return HTTP + this.getAuthorizationServerHostName() +
                      this.getAuthorizationServerUserUri() +
                        this.getAuthorizationServerRealmName() + PUT_USER_URI_2 + anId;
    }

    /**
     * Formulate put user for anId
     * @param anId String
     * @return String
     */
    private String formulateDeleteUserUrl(String anId) {

        return HTTP + this.getAuthorizationServerHostName() +
                this.getAuthorizationServerUserUri() +
                    this.getAuthorizationServerRealmName() + DELETE_USER_URI_2 + anId;
    }

    /**
     * Answer the default client id if clientId is not defined
     * @param clientId String
     * @return String
     */
    private String parseClientIdAndAnswerDefaultIfNotDefined(String clientId) {
        return !StringUtils.isEmpty(clientId) ? clientId : DEFAULT_TOKEN_CLIENT_ID;
    }

    /**
     * Retrieve an admin token
     * @return OauthToken
     */
    private OauthToken basicRetrieveAdminToken() {

        OauthToken tempResult;

        tempResult = this
                        .retrieveTokenForAdmin(this.getAuthorizationServerAdminUser(),
                                               this.getAuthorizationServerAdminPassword(),
                                               this.getAuthorizationServerAdminClientId());

        return tempResult;

    }

}
