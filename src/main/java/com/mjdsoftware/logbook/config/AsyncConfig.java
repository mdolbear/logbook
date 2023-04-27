package com.mjdsoftware.logbook.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

}
