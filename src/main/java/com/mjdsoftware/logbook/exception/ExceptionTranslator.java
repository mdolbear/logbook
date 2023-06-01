package com.mjdsoftware.logbook.exception;


import com.mjdsoftware.logbook.utils.LocalizationUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 *
 */
@Slf4j
@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    @Autowired
    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private LocalizationUtils localizationUtils;

    //Constants
    private static final String ERROR_CODE_PREFIX = "error.";

    /**
     * Answer my logger
     * @return Logger
     */
    private static Logger getLogger() {

        return log;
    }

    /**
     * Answer a default instance
     */
    public ExceptionTranslator() {
        super();
    }


    /**
     * Handle internal errors
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException anException,
                                                              WebRequest aRequest) {


        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX
                        + ErrorCode.USER_NOT_FOUND.name());
        getLogger().error("User not found error: " + tempMsg, anException);

        return this.handleError(anException, aRequest, HttpStatus.NOT_FOUND, tempMsg);

    }


    /**
     * Handle internal errors
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException anException,
                                                                   WebRequest aRequest) {

        Object[] tempArgs = {anException.getUsername()};

        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX
                        + ErrorCode.USER_ALREADY_EXISTS.name(), tempArgs);
        getLogger().error("User already exists error: " + tempMsg, anException);

        return this.handleError(anException, aRequest, HttpStatus.BAD_REQUEST, tempMsg);

    }


    /**
     * Handle internal errors
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException anException,
                                                      WebRequest aRequest) {

        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX
                                                      + ErrorCode.ACCESS_DENIED.name());
        getLogger().error("Access denied error: " + tempMsg, anException);

        return this.handleError(anException, aRequest, HttpStatus.FORBIDDEN, tempMsg);

    }


    /**
     * Handle user exceptions
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleArgumentException(RuntimeException anException,
                                                          WebRequest aRequest) {

        boolean illegalArg = IllegalArgumentException.class.isAssignableFrom(anException.getClass());
        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX +
                                                             (illegalArg ?
                                                              ErrorCode.ILLEGAL_ARGUMENT.name() :
                                                              ErrorCode.ILLEGAL_STATE.name()) );
        getLogger().error("Invalid "+((illegalArg)?"argument":"state") + ": " + tempMsg, anException);


        return this.handleError(anException,
                                aRequest,
                                HttpStatus.BAD_REQUEST,
                                tempMsg);
    }

    /**
     * Handle user exceptions
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<Object> handleArgumentException(HttpClientErrorException anException,
                                                          WebRequest aRequest) {

        HttpStatusCode tempStatusCode;

        tempStatusCode = anException.getStatusCode();

        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX +
                                                                            ErrorCode.ILLEGAL_ARGUMENT.name());
        getLogger().error("Invalid argument: " + tempMsg, anException);


        return this.handleError(anException,
                                aRequest,
                                (tempStatusCode != null) ? tempStatusCode: HttpStatus.BAD_REQUEST,
                                tempMsg);
    }

    /**
     * Handle user exceptions
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {LogbookNotFoundException.class})
    public ResponseEntity<Object> handleLogbookNotFoundException(LogbookNotFoundException anException,
                                                                 WebRequest aRequest) {

        Object[] tempArgs = {anException.getLogbookId()};
        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX + ErrorCode.LOGBOOK_NOT_FOUND.name(),
                                                      tempArgs);
        getLogger().error("Logbook not found exception: " + tempMsg,
                          anException);


        return this.handleError(anException,
                                aRequest,
                                HttpStatus.NOT_FOUND,
                                tempMsg);

    }


    /**
     * Handle user exceptions
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<Object> handleStateException(RuntimeException anException,
                                                       WebRequest aRequest) {

        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX + ErrorCode.ILLEGAL_STATE.name());
        getLogger().error("Invalid state: " + tempMsg, anException);


        return this.handleError(anException,
                                aRequest,
                                HttpStatus.BAD_REQUEST,
                                tempMsg);
    }

    /**
     * Handle internal errors
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<Object> handleInternalError(NullPointerException anException,
                                                      WebRequest aRequest) {

        String tempMsg =
            this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX + ErrorCode.NULL_POINTER.name());

        getLogger().error("Internal error: " + tempMsg, anException);

        return this.handleError(anException, aRequest, HttpStatus.INTERNAL_SERVER_ERROR, tempMsg);

    }

    /**
     * Handle internal errors
     * @param anException Exception
     * @param aRequest WebRequest
     * @return RequestEntity
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleInternalError(Exception anException,
                                                      WebRequest aRequest) {

        String tempMsg =
                this.getLocalizedMessageForExceptions(ERROR_CODE_PREFIX + ErrorCode.UNKNOWN_ERROR.name());
        getLogger().error("Internal error: " + tempMsg, anException);

        return this.handleError(anException, aRequest, HttpStatus.INTERNAL_SERVER_ERROR, tempMsg);

    }

    /**
     * Handle error
     * @param anException Exception
     * @param aRequest WebRequest
     * @param aStatus HttpStatus
     */
    private ResponseEntity<Object> handleError(Exception anException,
                                               WebRequest aRequest,
                                               HttpStatusCode aStatus,
                                               String aMessage) {

        return this.handleExceptionInternal(anException,
                                            new ErrorMessage(aStatus, aMessage),
                                            new HttpHeaders(),
                                            aStatus,
                                            aRequest);
    }


    /**
     * Answer a localized message based on the
     * anErrorCode
     * @param anErrorCodeKey String
     * @return String
     */
    private String getLocalizedMessageForExceptions(String anErrorCodeKey) {

        return this.getLocalizationUtils().getLocalizedMessage(anErrorCodeKey,
                                                               LocaleContextHolder.getLocale());
    }

    /**
     * Answer a localized message based on the
     * anErrorCode
     * @param anErrorCodeKey String
     * @return String
     */
    private String getLocalizedMessageForExceptions(String anErrorCodeKey, Object[] anArgs) {

        return this.getLocalizationUtils().getLocalizedMessage(anErrorCodeKey,
                                                               anArgs,
                                                               LocaleContextHolder.getLocale());
    }


}
