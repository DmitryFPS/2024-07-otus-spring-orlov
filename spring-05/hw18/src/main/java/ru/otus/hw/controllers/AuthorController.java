package ru.otus.hw.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.TooManyRequestsException;
import ru.otus.hw.services.AuthorService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthorController {
    private final AuthorService authorService;

    @RateLimiter(name = "rateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallbacks")
    @GetMapping("/api/v1/author")
    public List<AuthorDto> findAll() {
        return authorService.findAll();
    }

    @RateLimiter(name = "rateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallback")
    @GetMapping("/api/v1/author/{id}")
    public AuthorDto findById(@PathVariable("id") final Long id) {
        return authorService.findById(id);
    }

    private List<AuthorDto> fallbacks(final Exception e) {
        log.error("Отработал Circuit Breaker во время получения всех авторов: {}", e.getMessage());
        return Collections.emptyList();
    }

    private AuthorDto fallback(final Exception e) {
        log.error("Отработал Circuit Breaker во время получения одного автора: {}", e.getMessage());
        return null;
    }

    private List<AuthorDto> ratesLimiterFallback(final RequestNotPermitted e) {
        log.warn("Превышен лимит запросов: {}", e.getMessage());
        throw new TooManyRequestsException("Превышен лимит запросов. Попробуйте позже");
    }
}
