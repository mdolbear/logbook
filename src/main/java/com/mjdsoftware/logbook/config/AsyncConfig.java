package com.mjdsoftware.logbook.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Getter(value= AccessLevel.PRIVATE)  @Setter(value= AccessLevel.PRIVATE)
    private LogbookApplicationProperties properties;

    /**
     * Answer an instance of me with properties
     * @param aProperties LogbookApplicationProperties
     */
    public AsyncConfig(LogbookApplicationProperties aProperties) {

        this.setProperties(aProperties);
    }

    @Bean
    public Executor taskExecutor() {

        ThreadPoolTaskExecutor tempExecutor = new ThreadPoolTaskExecutor();

        tempExecutor.setCorePoolSize(this.getProperties().getThreadPoolCorePoolSize());
        tempExecutor.setMaxPoolSize(this.getProperties().getThreadPoolMaxPoolSize());
        tempExecutor.setQueueCapacity(this.getProperties().getThreadPoolQueueCapacity());
        tempExecutor.setThreadNamePrefix("Logbook Main Threadpool-");
        tempExecutor.initialize();

        return new DelegatingSecurityContextAsyncTaskExecutor(tempExecutor);

    }


    /**
     * Setup SecurityContext so that the Authentication can be inherited by
     * Threads created via Springboot that use the @Async annotation.
     * Note that this could also be done via properties. @see spring.security.strategy
     *
     * @return InitializingBean
     */
//    @Bean
//    public InitializingBean initializingBean() {
// This currently causes org.springframework.security.authentication.AuthenticationCredentialsNotFoundException:
//  An Authentication object was not found in the SecurityContext , when I run my tests. Need to investigate further
//  The property in my yaml didn't blow up tests but not sure if it really worked. I will need to test this again
//  with a real @Async method in the future.
//        return () -> SecurityContextHolder.setStrategyName(
//                SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
//
//    }



}
