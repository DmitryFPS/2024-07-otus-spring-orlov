package ru.otus.hw.controllers;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.dto.ErrorDto;
import ru.otus.hw.exception.NotFoundException;

@RestControllerAdvice
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(ValidationException.class)
    public ModelAndView handleValidationException(final ValidationException ex) {
        LOGGER.error(ex.getMessage(), ex);
        final ModelAndView modelAndView = new ModelAndView("errorPages/error");
        modelAndView.addObject("error", getError(HttpStatus.BAD_REQUEST, ex.getMessage()));
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(final NotFoundException ex) {
        LOGGER.error(ex.getMessage(), ex);
        final ModelAndView modelAndView = new ModelAndView("errorPages/error");
        modelAndView.addObject("error", getError(HttpStatus.NOT_FOUND, ex.getMessage()));
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleServerError(final Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        final ModelAndView modelAndView = new ModelAndView("errorPages/error");
        modelAndView.addObject("error", getError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

    private ErrorDto getError(final HttpStatus status, final String message) {
        final ErrorDto error = new ErrorDto();
        error.setStatusCode(status);
        error.setMessage(message);

        return error;
    }
}
