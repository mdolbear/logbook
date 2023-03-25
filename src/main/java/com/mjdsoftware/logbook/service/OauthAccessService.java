package com.mjdsoftware.logbook.service;


import com.mjdsoftware.logbook.dto.oauth.OauthToken;
import com.mjdsoftware.logbook.dto.oauth.UserAuthDTO;

public interface OauthAccessService {

    /**
     * Retrieve token
     *
     * @param username String
     * @param password String
     * @param clientId String
     * @return OauthToken
     */
    public OauthToken getClientToken(String username, String password, String clientId);

    /**
     * Refresh token
     * @param refreshToken Sting The refresh token
     * @param clientId String
     * @return OauthToken
     */
    public OauthToken refreshToken(String refreshToken, String clientId);
    
    /**
     * Retrieve User
     *
     * @param username String
     * @return OauthToken
     */
    public UserAuthDTO retrieveUser(String username);

    /**
     * Create user
     * @param username String
     * @param password String
     * @return UserAuthDTO
     */
    public UserAuthDTO createUser(String username, String password);

    /**
     * Delete user
     * @param keycloakUserId String the user's id in keycloak system
     */
    public void deleteUser(String keycloakUserId);


}
