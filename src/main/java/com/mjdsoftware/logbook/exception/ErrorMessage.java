package com.mjdsoftware.logbook.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ErrorMessage {

    private HttpStatusCode status;
    private String message;

}
