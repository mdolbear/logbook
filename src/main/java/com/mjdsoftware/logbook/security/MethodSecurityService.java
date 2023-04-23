package com.mjdsoftware.logbook.security;

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

    @Autowired
    @Setter(AccessLevel.PRIVATE)
    private MessageSource messageSource;

    @Autowired
    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private KeyCloakUtilities keycloakUtilities;

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
                                 KeyCloakUtilities aUtilities) {

        this.setMessageSource(aMessageSource);
        this.setKeycloakUtilities(aUtilities);

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
