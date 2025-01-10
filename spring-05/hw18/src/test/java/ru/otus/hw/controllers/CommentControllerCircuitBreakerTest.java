package ru.otus.hw.controllers;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentControllerCircuitBreakerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentController commentController;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private CommentService commentService;


    @BeforeEach
    void setUp() {
        // Сбросить состояние RateLimiter и CircuitBreaker перед каждым тестом
        final RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("rateLimiter");
        rateLimiter.changeLimitForPeriod(2); // Лимит 2 запроса в секунду
        when(commentService.findByBookId(1L)).thenReturn(Collections.emptyList());

        final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuitBreaker");
        circuitBreaker.reset();
    }


    @Test
    void findAllSuccess() {
        final List<CommentDto> expectedComments = List.of(
                new CommentDto(1L, "comment1"), new CommentDto(2L, "comment2"));
        when(commentService.findByBookId(1L)).thenReturn(expectedComments);
        final List<CommentDto> result = commentController.findAll(1L);

        assertEquals(expectedComments, result);
        verify(commentService, times(1)).findByBookId(1L);
    }

    @Test
    void findAllCircuitBreakerFallback() {
        when(commentService.findByBookId(1L)).thenThrow(new RuntimeException("Service unavailable"));

        final List<CommentDto> result = commentController.findAll(1L);

        assertEquals(Collections.emptyList(), result);
        verify(commentService, times(1)).findByBookId(1L);
    }

    @Test
    void findAllRateLimiterFallback() throws Exception {
        // Имитируем превышение лимита запросов
        for (int i = 0; i < 3; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/{id}", 1L))
                        .andExpect(status().isOk());
            } catch (final ServletException e) {
                if (e.getRootCause() instanceof RequestNotPermitted) {
                    // Проверяем, что fallback-метод сработал и вернул исключение TooManyRequestsException
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/{id}", 1L))
                            .andExpect(status().isTooManyRequests())
                            .andExpect(MockMvcResultMatchers.content().string("Превышен лимит запросов. Попробуйте позже"));
                }
            }
        }
    }
}
