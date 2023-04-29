package com.mjdsoftware.logbook.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ApiHandlerInterceptor implements HandlerInterceptor {

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }


    /**
     * Handle result monitoring after rest invocation completion
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     * execution, for type and/or instance examination
     * @param ex any exception thrown on handler execution, if any; this does not
     * include exceptions that have been handled through an exception resolver
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                @Nullable Exception ex) throws Exception {


        this.logRequest(request);

    }

    /**
     * Log request information
     * @param request HttpServletRequest
     */
    private void logRequest(HttpServletRequest request) {

        Map<String, String[]> tempParameters = new HashMap<>(request.getParameterMap());

        getLogger().info("afterCompletion-request: "
                            + request
                            + " method: " + request.getMethod() +
                            " uri: " + request.getRequestURI());
        this.logParameterMap(request.getParameterMap());
        this.logHeaders(request);

    }

    /**
     * Log parameters
     * @param aMap Map
     */
    private void logParameterMap(Map<String, String[]> aMap) {

        String      tempValue;

        for (Map.Entry<String, String[]> anEntry: aMap.entrySet()) {

            tempValue = Arrays.stream(anEntry.getValue()).collect(Collectors.joining(","));
            getLogger().info("parameter-key: " + anEntry.getKey() + " parameter-value: "+ tempValue);

        }

    }

    /**
     * Log headers
     * @param aRequest HttpServletRequest
     */
    private void logHeaders(HttpServletRequest aRequest) {

        Enumeration<String> tempHeaderNames;
        String              tempHeaderName;

        tempHeaderNames = aRequest.getHeaderNames();
        while (tempHeaderNames.hasMoreElements()) {

            tempHeaderName = tempHeaderNames.nextElement();
            getLogger().info("header-key: " + tempHeaderName
                             + " parameter-value: "+ aRequest.getHeader(tempHeaderName));
        }

    }

}
