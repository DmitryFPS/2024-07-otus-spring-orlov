package ru.otus.hw.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.TooManyRequestsException;
import ru.otus.hw.services.CommentService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @RateLimiter(name = "rateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallback")
    @GetMapping("/api/v1/comment/{id}")
    public List<CommentDto> findAll(@PathVariable("id") final Long id) {
        return commentService.findByBookId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/comment")
    public CommentDto create(@RequestBody final CommentCreateDto commentCreateDto) {
        return commentService.create(commentCreateDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/v1/comment/{id}")
    public void delete(@PathVariable("id") final Long id) {
        commentService.deleteById(id);
    }


    private List<CommentDto> fallback(final Exception e) {
        log.error("Отработал Circuit Breaker во время получения всех комментариев: {}", e.getMessage());
        return Collections.emptyList();
    }

    private List<CommentDto> rateLimiterFallback(final RequestNotPermitted e) {
        log.warn("Превышен лимит запросов: {}", e.getMessage());
        throw new TooManyRequestsException("Превышен лимит запросов. Попробуйте позже");
    }
}
