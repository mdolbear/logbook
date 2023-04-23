package com.mjdsoftware.logbook.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,
                            securedEnabled = true,
                            jsr250Enabled = true)
public class SecurityConfig {

    @Value("${springdoc.api-docs.path}")
    private String restApiDocPath;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf()
            .disable()
            .cors()
            .and()
            .authorizeHttpRequests(authorize->
                {
                    try {
                        authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers(String.format("%s/**", restApiDocPath)).permitAll()
                        .requestMatchers(String.format("%s/**", swaggerPath)).permitAll()

                        //Login and token access
                        .requestMatchers("/api/oauth/clientToken").permitAll()  //All external users can get to access a key
                        .requestMatchers("/api/oauth/refreshToken").permitAll()  //All external users can refresh an existing token

                        //Expose actuator and health indicator - Note that since we are running inside kubernetes,
                        //we will not surface port 8081 outside the Ingress. So only processes running inside of
                        //kubernetes will be able to get to /actuator since the url will not be publicly accessible
                        .requestMatchers("/actuator/**").permitAll()

                        //Rest end point authorizations
                        .requestMatchers(HttpMethod.GET, "/api/**")
                        .hasAnyAuthority("SCOPE_read", "SCOPE_write")
                        .anyRequest()
                        .authenticated()
                        .and()
                        .oauth2ResourceServer()
                        .jwt();


                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            );


        return http.build();

    }


    
}
