package com.mjdsoftware.logbook.security;

import com.mjdsoftware.logbook.domain.entities.Logbook;
import com.mjdsoftware.logbook.domain.entities.User;
import com.mjdsoftware.logbook.service.LogbookService;
import com.mjdsoftware.logbook.service.UserService;
import com.mjdsoftware.logbook.utils.KeyCloakUtilities;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MethodSecurityService {

    public static final String ROLE_APP_ADMIN = "ROLE_app_admin";
    @Autowired
    @Setter(AccessLevel.PRIVATE)
    private MessageSource messageSource;

    @Autowired
    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private KeyCloakUtilities keycloakUtilities;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private UserService userService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LogbookService logbookService;

    //Constants
    protected static final String USER_NAME = "preferred_username";


    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer a default instance
     */
    @Autowired
    public MethodSecurityService(MessageSource aMessageSource,
                                 KeyCloakUtilities aUtilities,
                                 UserService userService,
                                 LogbookService aLogbookService) {

        this.setMessageSource(aMessageSource);
        this.setKeycloakUtilities(aUtilities);
        this.setUserService(userService);
        this.setLogbookService(aLogbookService);

    }


    /**
     * Allow access if not anonymous user. This is just a placeholder to provide
     * hooks for method level security
     * @param anAuthentication Authentication
     * @param aRequest HttpServletRequest
     * @param aJwt Jwt
     * @return boolean
     */
    public boolean isAccessAllowed(Authentication anAuthentication,
                                   HttpServletRequest aRequest,
                                   Jwt aJwt) {

        return
                this.isNotAnonymousAndHasUserClaim(anAuthentication,
                                                   aRequest,
                                                   aJwt);
    }

    /**
     * Check for logbook access. Don't like double query TODO
     * @param anAuthentication Authentication
     * @param aRequest HttpServletRequest
     * @param aJwt Jwt
     * @param logbookId Long
     * @return boolean
     */
    public boolean isAccessAllowedForLogbook(Authentication anAuthentication,
                                             HttpServletRequest aRequest,
                                             Jwt aJwt,
                                             Long logbookId) {

        boolean tempResult;
        boolean tempIsAdminUser;
        Logbook tempLogbook;

        tempResult =
                this.isNotAnonymousAndHasUserClaim(anAuthentication,
                                                   aRequest,
                                                   aJwt);
        if (tempResult) {

            tempIsAdminUser = anAuthentication.getAuthorities()
                                              .stream()
                                              .filter(ga -> ga.getAuthority().equals(ROLE_APP_ADMIN))
                                              .findFirst()
                                              .isPresent();
            if (!tempIsAdminUser) {

                //If not admin, then the logbook needs to belong to the user taking the action
                tempLogbook = this.getLogbookService().findLogbookById(logbookId);
                tempResult = tempLogbook != null &&
                                    tempLogbook.getUser() != null &&
                                        tempLogbook.getUser().getUsername().equals(aJwt.getClaim(USER_NAME));
            }

        }

        return tempResult;

    }

    /**
     * Allow access if either admin user or if userId is the same as the accessing
     * user.
     * @param anAuthentication Authentication
     * @param aRequest HttpServletRequest
     * @param aJwt Jwt
     * @param userId Long
     * @return boolean
     */
    public boolean isAccessAllowed(Authentication anAuthentication,
                                   HttpServletRequest aRequest,
                                   Jwt aJwt,
                                   Long userId) {

        boolean tempResult;
        User    tempUser;
        boolean tempIsAdminUser;

        tempResult =
                this.isNotAnonymousAndHasUserClaim(anAuthentication,
                                                   aRequest,
                                                    aJwt);
        if (tempResult) {

            tempIsAdminUser = anAuthentication.getAuthorities()
                                              .stream()
                                              .filter(ga->ga.getAuthority().equals(ROLE_APP_ADMIN))
                                              .findFirst()
                                              .isPresent();
            if (!tempIsAdminUser) {

                tempUser = this.getUserService().findUserById(userId);
                tempResult = tempUser != null &&
                                this.isUserSameAsClaimUser(tempUser, aJwt);

            }
            else {
                tempResult = true; //Admin always has access
            }

        }

        return tempResult;

    }

    /**
     * Answer whether aUser is the same as the claim user
     * @param aUser User
     * @param aJwt Jwt
     */
    private boolean isUserSameAsClaimUser(User aUser, Jwt aJwt) {

       return aUser.getUsername().equals(aJwt.getClaim(USER_NAME));

    }

    /**
     * Answer true if aAuthentication does not represent an anonymous user and the
     * user custom claim is present
     * @param anAuthentication Authentication
     * @param aRequest HttpServletRequest
     * @param aJwt Jwt
     * @return boolean
     */
    private boolean isNotAnonymousAndHasUserClaim(Authentication anAuthentication,
                                                  HttpServletRequest aRequest,
                                                  Jwt aJwt) {

        return !(!anAuthentication.isAuthenticated() ||
                    anAuthentication.getPrincipal() == null ||
                        StringUtils.equals(anAuthentication.getPrincipal().toString(),
                                           "anonymousUser") ||
                            aRequest == null ||
                                aJwt == null ||
                                    !this.hasAllRequiredClaims(aJwt));

    }

    /**
     * Answer whether I have all required claims
     * @param aJwt Jwt
     * @boolean
     */
    private boolean hasAllRequiredClaims(Jwt aJwt) {
        return aJwt.hasClaim(USER_NAME);
    }

}
