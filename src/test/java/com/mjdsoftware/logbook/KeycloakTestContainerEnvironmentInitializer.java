package com.mjdsoftware.logbook;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;

public class KeycloakTestContainerEnvironmentInitializer {

    @Getter(value = AccessLevel.PUBLIC)
    @Setter(value = AccessLevel.PRIVATE)
    private KeycloakContainer keycloak;

    /**
     * Answer a default instance
     */
    public KeycloakTestContainerEnvironmentInitializer() {
        super();
        this.setKeycloak(new KeycloakContainer());
    }


    /**
     * Provide the functionality required to implement the
     * ApplicationContextInitializer<ConfigurableApplicationContext> interface. This basically provides us a way
     * to override springboot properties at runtime for our tests. Since we have test container ports that are generated
     * randomly, we need a way to do this.
     *
     * @param configurableApplicationContext ConfigurableApplicationContext
     */
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {


        if (!this.getKeycloak().isCreated()) {
            this.getKeycloak().start();
        }

        TestPropertyValues.of("keycloak.auth-server-url=" + this.getKeycloak().getAuthServerUrl())
                .applyTo(configurableApplicationContext.getEnvironment());

        TestPropertyValues.of("spring.security.oauth2.resourceserver.jwt.jwk-set-uri="
                + "http://" + this.getKeycloak().getHost() + ":" + this.getKeycloak().getHttpPort() + "/realms/logbook/protocol/openid-connect/certs")
                .applyTo(configurableApplicationContext.getEnvironment());

        TestPropertyValues.of("spring.security.oauth2.resourceserver.jwt.issuer-uri="
                + "http://" + this.getKeycloak().getHost() + ":" + this.getKeycloak().getHttpPort() + "/realms/logbook")
                .applyTo(configurableApplicationContext.getEnvironment());



    }

    /**
     * Perform test initialization
     */
    public void performInitialization() {

        this.getKeycloak().withRealmImportFiles("/master-realm.json", "/test-realm.json");
        setupKeyCloak(this.getKeycloak());
    }


    /**
     * Setup aKeycloakContainer
     * @param aKeycloakContainer KeycloakContainer
     */
    private void setupKeyCloak(KeycloakContainer aKeycloakContainer) {

        if (!aKeycloakContainer.isCreated()) {
            aKeycloakContainer.start();
        }

        System.setProperty("app.authorizationServerHostName", aKeycloakContainer.getHost() + ":" + aKeycloakContainer.getHttpPort());
        System.setProperty("app.authorizationServerAdminUser", aKeycloakContainer.getAdminUsername());
        System.setProperty("app.authorizationServerAdminPassword", aKeycloakContainer.getAdminPassword());

        System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                "http://" + aKeycloakContainer.getHost() + ":" + aKeycloakContainer.getHttpPort() + "/realms/logbook");

        System.setProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                "http://" + aKeycloakContainer.getHost() + ":" + aKeycloakContainer.getHttpPort() + "/realms/logbook/protocol/openid-connect/certs");

    }


}
