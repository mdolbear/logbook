package com.mjdsoftware.logbook.security;

import com.mjdsoftware.logbook.config.JwtAuthConverterProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Getter(value= AccessLevel.PRIVATE)  @Setter(value= AccessLevel.PRIVATE)
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @Getter(value= AccessLevel.PRIVATE)  @Setter(value= AccessLevel.PRIVATE)
    private JwtAuthConverterProperties properties;

    /**
     * Answer an instance on properties
     * @param properties JwtAuthConverterProperties
     */
    @Autowired
    public JwtAuthConverter(JwtAuthConverterProperties properties) {

        this.setProperties(properties);
        this.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthoritiesConverter());

    }

    /**
     * Answer a token that contains the authorities pull from the "role" value in custom claims.
     * This code is somewhat delicate and based on the claims structure returned from Keycloak.l
     * @param jwt the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return AbstractAuthenticationToken
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> tempAuthorities =
                Stream.concat(this.getJwtGrantedAuthoritiesConverter().convert(jwt).stream(),
                              this.extractResourceRoles(jwt).stream()).collect(Collectors.toSet());

        //Add roles to authorities, creating new JwtAuthenticationToken
        return new JwtAuthenticationToken(jwt, tempAuthorities);
    }


    /**
     * Extract roles from custom claims
     * @param aJwt Jwt
     * @return Collection
     */
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt aJwt) {

        List<String> tempRoles;
        Collection<? extends GrantedAuthority> tempResult = Set.of();

        tempRoles = aJwt.getClaim(this.getProperties().getResourceId());

        if (tempRoles != null && !tempRoles.isEmpty()) {

            tempResult =
                    tempRoles.stream()
                             .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                             .collect(Collectors.toSet());

        }

        return tempResult;

    }

}
