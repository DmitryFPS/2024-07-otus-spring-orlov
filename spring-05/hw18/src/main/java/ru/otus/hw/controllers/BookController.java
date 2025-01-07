package ru.otus.hw.controllers;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.TooManyRequestsException;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;


    @RateLimiter(name = "rateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallback")
    @GetMapping("/api/v1/book")
    public List<BookDto> findAll() {
        return bookService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/book")
    public BookDto create(@RequestBody final BookCreateDto bookCreateDto) {
        return bookService.create(bookCreateDto);
    }

    @PatchMapping("/api/v1/book/{id}")
    public BookDto update(@PathVariable("id") final Long id,
                          @RequestBody final BookUpdateDto bookUpdateDto) {
        bookUpdateDto.setId(id);
        return bookService.update(bookUpdateDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/v1/book/{id}")
    public void delete(@PathVariable("id") final Long id) {
        bookService.deleteById(id);
    }

    private List<BookDto> fallback(final Exception e) {
        log.error("Отработал Circuit Breaker во время получения всех книг: {}", e.getMessage());
        return Collections.emptyList();
    }

    private List<BookDto> rateLimiterFallback(final RequestNotPermitted e) {
        log.warn("Превышен лимит запросов: {}", e.getMessage());
        throw new TooManyRequestsException("Превышен лимит запросов. Попробуйте позже");
    }
}
