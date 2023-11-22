package com.mjdsoftware.logbook.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@SpringBootTest
public class JwtAuthConverterTest {

    private static final String TEST_JWT = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJnZUZ"
            + "mXzBvVXpvRk15ZWhGMml0T204ZmR1NjdJWVY5T3A0b3JYbjZOeDNZIn0.eyJleHAiOjE3MDA3NzM1MT"
            + "ksImlhdCI6MTcwMDY4NzExOSwianRpIjoiZjdkNDA2YWItMGNiYS00MDIxLTlkNTUtYzlkNzkyNTZkZDViI"
            + "iwiaXNzIjoiaHR0cDovL2tleWNsb2FrLWhvc3QvcmVhbG1zL2xvZ2Jvb2siLCJzdWIiOiJhNzU1NTViMy01ZjQ"
            + "yLTRlYTQtYTc5OC0wOTcwMDJkYzgwMTIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJuZXdDbGllbnQiLCJzZXNza"
            + "W9uX3N0YXRlIjoiNjEyNmY4ZDYtMzVkNC00OTk3LWEzOWUtMmYzZDdiNTYzYjNhIiwic2NvcGUiOiJsb2dib29"
            + "rLXJvbGVzIHByb2ZpbGUgd3JpdGUiLCJzaWQiOiI2MTI2ZjhkNi0zNWQ0LTQ5OTctYTM5ZS0yZjNkN2I1NjNi"
            + "M2EiLCJyb2xlIjpbImFwcF9hZG1pbiJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsb2dib29rLWFkbWluaXN"
            + "0cmF0b3IifQ.PL-q4hp-ZrO-JoiiFmSGZc6z_UNnSLneR-aSDWrjRF1fjAsPSNpOZbZu6yWUaDJJiN"
            + "yxPky5R6x9Jath0SXC4GnIEkUfVzSG1W7pc6QA83FLsWlo2UAWnqBABjEa9XLxYZo4dL3e3p-n7"
            + "UAmae8x5otFXBVciLHak9xOlWBi2AITgpN7YLK15-JBvNySwhovWqPaQT2E03bYlAfL8ur7iqG6hdYS"
            + "KDI-dArVQMiLS73_3_zpohpNUm4eecOQtX2EGxM4ELN82__RD9dG_ThTHkUt6GKTxaalBPZAeapCPDI"
            + "WFzir6uGB8FF1UPPg6RQJeFm1BXmOPQxgO3On75ER7g";

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    @Autowired
    private JwtAuthConverter jwtAuthConverter;


    /**
     * Perform main test
     */
    @Test
    public void testJwtConverter() {

        Jwt                         tempJwt;
        AbstractAuthenticationToken tempResult;
        List<String>                tempRole;

        tempJwt = new Jwt(TEST_JWT,
                Instant.now(),
                Instant.now().plusSeconds(3600l),
                this.createHeaders(),
                this.createClaims());

        tempResult = this.getJwtAuthConverter().convert(tempJwt);
        tempRole = (List)((JwtAuthenticationToken)tempResult).getTokenAttributes().get("role");

        Assertions.assertTrue(tempRole.contains("app_admin"));

    }

    /**
     * Create test headers for Jwt
     * @return Map
     */
    private Map<String, Object> createHeaders() {

        Map<String, Object> tempHeaders = new HashMap<String, Object>();

        tempHeaders.put("kid", "geFf_0oUzoFMyehF2itOm8fdu67IYV9Op4orXn6Nx3Y");
        tempHeaders.put("typ", "JWT");
        tempHeaders.put("alg", "RS256");

        return tempHeaders;
    }

    /**
     * Create test claims for jwt
     * @return Map
     */
    private Map<String, Object> createClaims() {

        List<String> tempList = new ArrayList<>();
        tempList.add("app_admin");

        Map<String, Object> tempClaims = new HashMap<String, Object>();
        tempClaims.put("sub" , "a75555b3-5f42-4ea4-a798-097002dc8012");
        tempClaims.put("role", tempList);
        tempClaims.put("iss" , "http://keycloak-host/realms/logbook");
        tempClaims.put("typ" , "Bearer");
        tempClaims.put("preferred_username" , "logbook-administrator");
        tempClaims.put("sid" , "6126f8d6-35d4-4997-a39e-2f3d7b563b3a");
        tempClaims.put("azp" , "newClient");
        tempClaims.put("scope" , "logbook-roles profile write");
        tempClaims.put("exp" , "2023-11-23T21:05:19Z");
        tempClaims.put("session_state" , "6126f8d6-35d4-4997-a39e-2f3d7b563b3a");
        tempClaims.put("iat" , "2023-11-22T21:05:19Z");
        tempClaims.put("jti" , "f7d406ab-0cba-4021-9d55-c9d79256dd5b");

        return tempClaims;
    }

}
