package com.mjdsoftware.logbook.utils;

import com.mjdsoftware.logbook.KeycloakTestContainerEnvironmentInitializer;
import com.mjdsoftware.logbook.LogbookApplication;
import com.mjdsoftware.logbook.dto.oauth.OauthToken;
import com.mjdsoftware.logbook.dto.oauth.UserAuthDTO;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 *
 * @author michaeldolbear
 */
@SpringBootTest(classes = LogbookApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { KeyCloakUtilitiesTest.PropertyOverrideContextInitializer.class })
@ActiveProfiles("test")
public class KeyCloakUtilitiesTest {

    @Getter(value = AccessLevel.PRIVATE)
    private static KeycloakTestContainerEnvironmentInitializer keycloakTestEnvironmentInitializer;

    @BeforeAll
    public static void setup() {

        keycloakTestEnvironmentInitializer = new KeycloakTestContainerEnvironmentInitializer();
        keycloakTestEnvironmentInitializer.performInitialization();
    }

    static class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            getKeycloakTestEnvironmentInitializer().initialize(configurableApplicationContext);

        }

    }

    /**
     * Since we are using keycloak utilities directly we need to substitute the
     * properties directly into this class.
     */
    @BeforeEach
    public void setupKeycloakProperties() {

        this.getKeycloakUtilities()
                .setAuthorizationServerHostName(getKeycloakTestEnvironmentInitializer().getKeycloak().getHost()
                        + ":" + getKeycloakTestEnvironmentInitializer().getKeycloak().getHttpPort());
        this.getKeycloakUtilities()
                .setAuthorizationServerAdminUser(getKeycloakTestEnvironmentInitializer().getKeycloak().getAdminUsername());
        this.getKeycloakUtilities()
                .setAuthorizationServerAdminPassword( getKeycloakTestEnvironmentInitializer().getKeycloak().getAdminPassword());


    }




    @Getter(value = AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminUser}")
    private String authorizationServerAdminUser;

    @Getter(value = AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminPassword}")
    private String authorizationServerAdminPassword;

    @Getter(value = AccessLevel.PRIVATE)
    @Value("${app.authorizationServerAdminClientId}")
    private String authorizationServerAdminClientId;


    @Getter(value = AccessLevel.PRIVATE)
    @Autowired
    private KeyCloakUtilities keycloakUtilities;

    @Getter(value = AccessLevel.PRIVATE)
    @Autowired
    private ApplicationEventPublisher publisher;

    @Getter(value = AccessLevel.PRIVATE)
    @Autowired
    private ApplicationContext context;


    //Constants
    public static final String DONOTDELETE_USER = "athelete";
    public static final String DONOTDELETE_PASSWORD = "athelete";
    public static final String NEW_CLIENT = "newClient";

    public static final String NEW_USER_TO_CREATE = "newusertocreate";
    public static final String NEW_USER_TO_CREATE_PW = "newuserpw1";





    /**
     * Retrieve the regular token
     */
    @Test
    public void retrieveRegularTokenTest() {

        OauthToken tempResult;

        tempResult = this.getKeycloakUtilities()
                         .retrieveToken(null,
                                        DONOTDELETE_USER,
                                        DONOTDELETE_PASSWORD,
                                        NEW_CLIENT);

        assertTrue("Failed to retrieve normal token", tempResult != null
                                                        && tempResult.getAccessToken() != null);
    }


    /**
     * Retrieve the admin token
     */
    @Test
    public void retrieveAdminTokenTest() {

        try {
            OauthToken tempResult = this.basicRetrieveAdminToken();

            assertTrue("Failed to retrieve admin token", tempResult != null
                        && tempResult.getAccessToken() != null);
        }
        catch(ResourceAccessException exc) {
            fail("Ensure that the oauth-authorization-server application, found in the pd-refact-auth-server project, is running when you execute this test.");
        }

    }

    /**
     * Retrieve a user
     */
    @Test
    public void retrieveUserTest() {

        UserAuthDTO tempUser =
                this.basicRetrieveUser(DONOTDELETE_USER);

        assertTrue("Failed to retrieve user",
                tempUser != null &&
                        tempUser.getId() != null);

    }

    /**
     * Retrieve user
     * @return UserAuthDTO
     */
    private UserAuthDTO basicRetrieveUser(String aUsername) {

        OauthToken  tempToken;
        UserAuthDTO     tempUser;

        tempToken = this.basicRetrieveAdminToken();

        assertTrue("Failed to retrieve admin token",
                tempToken != null
                             && tempToken.getAccessToken() != null);

        tempUser =
                this.getKeycloakUtilities().retrieveUser(tempToken,
                                                         aUsername);

        return tempUser;
    }

    /**
     * Refresh a regular token
     */
    @Test
    public void refreshRegularTokenTest() {

        OauthToken tempResult;

        tempResult = this.getKeycloakUtilities()
                         .retrieveToken(null,
                                        DONOTDELETE_USER,
                                        DONOTDELETE_PASSWORD,
                                        NEW_CLIENT);

        assertTrue("Failed to retrieve normal token",
                    tempResult != null
                                    && tempResult.getAccessToken() != null);

        tempResult = this.getKeycloakUtilities()
                         .refreshToken(tempResult.getRefreshToken(), NEW_CLIENT);

        assertTrue("Failed to refresh normal token",
                tempResult != null
                            && tempResult.getAccessToken() != null);
    }



    /**
     * Create user test
     */
    @Test
    public void createUserTest() {

        UserAuthDTO         tempExistingUserToDelete;
        OauthToken          tempToken;

        //Find user to test
        tempExistingUserToDelete = this.basicRetrieveUser(NEW_USER_TO_CREATE);

        //Delete user if it exists
        if (tempExistingUserToDelete != null) {

            tempToken = this.basicRetrieveAdminToken();
            this.getKeycloakUtilities().deleteUser(tempToken, tempExistingUserToDelete.getId());

        }

        //Create user again from scratch
        tempToken = this.basicRetrieveAdminToken();
        this.getKeycloakUtilities()
            .createUser(tempToken,
                        NEW_USER_TO_CREATE,
                        NEW_USER_TO_CREATE_PW);

        //Find user again
        tempExistingUserToDelete = this.basicRetrieveUser(NEW_USER_TO_CREATE);
        assertTrue("User does not exist",
                    tempExistingUserToDelete != null);
        assertEquals(NEW_USER_TO_CREATE, tempExistingUserToDelete.getUsername());

        //Generate token for new user
        tempToken = this.getKeycloakUtilities()
                        .retrieveToken(NEW_USER_TO_CREATE,
                                       NEW_USER_TO_CREATE_PW,
                                       NEW_CLIENT);

        assertTrue("Failed to retrieve normal token",
                    tempToken != null
                                && tempToken.getAccessToken() != null);

        //Delete newly created user
        tempToken = this.basicRetrieveAdminToken();
        this.getKeycloakUtilities().deleteUser(tempToken, tempExistingUserToDelete.getId());

    }


    /**
     * Retrieve an admin token
     * @return OauthToken
     */
    private OauthToken basicRetrieveAdminToken() {

        OauthToken tempResult;

        tempResult = this.getKeycloakUtilities()
                         .retrieveTokenForAdmin(this.getAuthorizationServerAdminUser(),
                                                this.getAuthorizationServerAdminPassword(),
                                                this.getAuthorizationServerAdminClientId());

        return tempResult;

    }


}
