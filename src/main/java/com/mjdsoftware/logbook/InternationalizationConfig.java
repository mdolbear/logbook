package com.mjdsoftware.logbook;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * We can access a locale in a thread-safe manner FROM ANYWHERE by going to
 * org.springframework.context.i18n.LocaleContextHolder.getLocale()
 * This will pick up the user established locale setup via spring for the user.
 * Note that LocaleContext is created on every request via DispatcherServlet 
 * (handled in spring code).
 * @author mdolbear
 */
@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {


    /**
     * This bean will handle language input via "accept-language" header parameter
     * in the HttpRequest.
     * @return LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {

        AcceptHeaderLocaleResolver tempResolver;

        tempResolver = new AcceptHeaderLocaleResolver();
        tempResolver.setDefaultLocale(Locale.US);

        return tempResolver;
    }


    /**
     * Create a message source. If we want to build a MessageSource that uses the
     * database, this is the point that we would need to Inject an implementation of
     * MessageSource which does that.
     * @return MessageSource
     */
    @Bean("messageSource")
    public MessageSource messageSource() {

        ResourceBundleMessageSource tempSrc;

        tempSrc = new ResourceBundleMessageSource();

        //Directory plus base file name for all internationalized messages
        //TODO - Make it a property in the application.yml file: spring.messages.basename
        tempSrc.setBasename("language/messages");

        tempSrc.setDefaultEncoding("UTF-8");

        return tempSrc;

    }


    /**
     * Completely open up cors access until we have more requirements here, as well as
     * configuration
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

            registry.addMapping("/api/**")
                    .allowedOrigins("*")
//                    .maxAge(maxAge)
                    .allowedMethods("*");

    }


}
