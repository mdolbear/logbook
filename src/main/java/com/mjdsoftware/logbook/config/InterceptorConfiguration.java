package com.mjdsoftware.logbook.config;

import com.mjdsoftware.logbook.security.ApiHandlerInterceptor;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Getter(AccessLevel.PRIVATE)
    @Autowired
    private ApiHandlerInterceptor interceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(this.getInterceptor());
    }

}
