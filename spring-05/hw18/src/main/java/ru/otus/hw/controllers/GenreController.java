package ru.otus.hw.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.TooManyRequestsException;
import ru.otus.hw.services.GenreService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class GenreController {
    private final GenreService genreService;


    @RateLimiter(name = "rateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallbacks")
    @GetMapping("/api/v1/genre")
    public List<GenreDto> findAll() {
        return genreService.findAll();
    }

    @RateLimiter(name = "rateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallback")
    @GetMapping("/api/v1/genre/{id}")
    public GenreDto findById(@PathVariable("id") final Long id) {
        return genreService.findById(id);
    }


    private List<GenreDto> fallbacks(final Exception e) {
        log.error("Отработал Circuit Breaker во время получения всех жанров: {}", e.getMessage());
        return Collections.emptyList();
    }

    private GenreDto fallback(final Exception e) {
        log.error("Отработал Circuit Breaker во время получения одного жанра: {}", e.getMessage());
        return null;
    }

    private List<GenreDto> ratesLimiterFallback(final RequestNotPermitted e) {
        log.warn("Превышен лимит запросов: {}", e.getMessage());
        throw new TooManyRequestsException("Превышен лимит запросов. Попробуйте позже");
    }
}
