package com.mjdsoftware.logbook.config;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
@ConfigurationProperties("app")
@Data
public class LogbookApplicationProperties {

    @Getter @Setter
    private String applicationName;

    @Getter @Setter
    private String applicationDescription;

    @Getter @Setter
    private String authorizationServerHostName;

    @Getter @Setter
    private String authorizationServerRealmName;

    @Getter @Setter
    private String authorizationServerClientId;

    @Getter @Setter
    private String authorizationServerClientSecret;

    @Getter @Setter
    private String authorizationServerAdminUser;

    @Getter @Setter
    private String authorizationServerAdminPassword;

    @Getter @Setter
    private String authorizationServerAdminClientId;

    @Getter @Setter
    private String authorizationServerTokenUri;

    @Getter @Setter
    private String authorizationServerUserUri;

    @Getter @Setter
    private Integer threadPoolCorePoolSize;

    @Getter @Setter
    private Integer threadPoolMaxPoolSize;

    @Getter @Setter
    private Integer threadPoolQueueCapacity;

}
