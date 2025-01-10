package ru.otus.hw.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.dto.ErrorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.exceptions.TooManyRequestsException;

@RestControllerAdvice
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorDto handleNotFound(final NotFoundException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return getError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(TooManyRequestsException.class)
    public ErrorDto handleNotFound(final TooManyRequestsException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return getError(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleServerError(final Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return getError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ErrorDto getError(final HttpStatus status, final String message) {
        final ErrorDto error = new ErrorDto();
        error.setStatusCode(status);
        error.setMessage(message);
        return error;
    }
}
