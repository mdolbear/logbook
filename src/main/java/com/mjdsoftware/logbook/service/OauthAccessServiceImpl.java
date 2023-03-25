package com.mjdsoftware.logbook.service;


import com.mjdsoftware.logbook.dto.oauth.OauthToken;
import com.mjdsoftware.logbook.dto.oauth.UserAuthDTO;
import com.mjdsoftware.logbook.utils.KeyCloakUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class OauthAccessServiceImpl implements OauthAccessService {

    @Autowired
    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PROTECTED) // exposed for unit tests
    private KeyCloakUtilities keycloakUtilities;

    @Getter(AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminUser}")
    private String authorizationServerAdminUser;

    @Getter(AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminPassword}")
    private String authorizationServerAdminPassword;

    @Getter(AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminClientId}")
    private String authorizationServerAdminClientId;

    @Autowired
    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PROTECTED) // exposed for unit tests
    private MessageSource messageSource;

    /**
     * Retrieve token
     *
     * @param username String
     * @param password String
     * @param clientId String
     * @return OauthToken
     */
    @Override
    public OauthToken getClientToken(String username, String password, String clientId) {
        return this.getKeycloakUtilities().retrieveToken(username, password, clientId);
    }

    /**
     * Refresh token
     * @param refreshToken Sting The refresh token
     * @param clientId String
     * @return OauthToken
     */
    public OauthToken refreshToken(String refreshToken, String clientId) {
        return this.getKeycloakUtilities().refreshToken(refreshToken, clientId);
    }



    /**
     * Retrieve User
     *
     * @param username String
     * @return OauthToken
     */
    @Override
    public UserAuthDTO retrieveUser(@NonNull String aUsername) {

        return this.getKeycloakUtilities().retrieveUser(this.retrieveAdminToken(), aUsername);

    }

    /**
     * Create user
     * @param username String
     * @param password String
     * @return UserAuthDTO
     */
    @Override
    public UserAuthDTO createUser(@NonNull String username,
                                  @NonNull String password) {


        this.getKeycloakUtilities().createUser(this.retrieveAdminToken(), username, password);
        return this.getKeycloakUtilities().retrieveUser(this.retrieveAdminToken(), username);

    }

    /**
     * Delete user
     * @param keycloakUserId String the user's id in keycloak system
     */
    @Override
    public void deleteUser(@NonNull String keycloakUserId) {

        this.getKeycloakUtilities().deleteUser(this.retrieveAdminToken(), keycloakUserId);
    }

    /**
     * Retrieve an admin token
     * @return OauthToken
     */
    private OauthToken retrieveAdminToken() {

        OauthToken tempResult;

        tempResult = this.getKeycloakUtilities()
                .retrieveTokenForAdmin(this.getAuthorizationServerAdminUser(),
                                       this.getAuthorizationServerAdminPassword(),
                                       this.getAuthorizationServerAdminClientId());

        return tempResult;

    }

}
