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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthorControllerCircuitBreakerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorController authorController;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private AuthorService authorService;


    @BeforeEach
    void setUp() {
        // Сбросить состояние RateLimiter и CircuitBreaker перед каждым тестом
        final RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("rateLimiter");
        rateLimiter.changeLimitForPeriod(2); // Лимит 2 запроса в секунду
        when(authorService.findAll()).thenReturn(Collections.emptyList());

        final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuitBreaker");
        circuitBreaker.reset();
    }


    @Test
    void findAllSuccess() {
        final List<AuthorDto> expectedAuthors = List.of(
                new AuthorDto(1L, "Name1"), new AuthorDto(2L, "Name2"));

        when(authorService.findAll()).thenReturn(expectedAuthors);
        final List<AuthorDto> result = authorController.findAll();

        assertEquals(expectedAuthors, result);
        verify(authorService, times(1)).findAll();
    }

    @Test
    void findAuthorSuccess() {
        final AuthorDto expectedAuthor = new AuthorDto(1L, "Name1");

        when(authorService.findById(1L)).thenReturn(expectedAuthor);
        final AuthorDto result = authorController.findById(1L);

        assertEquals(expectedAuthor, result);
        verify(authorService, times(1)).findById(1L);
    }

    @Test
    void findAllCircuitBreakerFallback() {
        when(authorService.findAll()).thenThrow(new RuntimeException("Service unavailable"));

        final List<AuthorDto> result = authorController.findAll();

        assertEquals(Collections.emptyList(), result);
        verify(authorService, times(1)).findAll();
    }

    @Test
    void findAuthorCircuitBreakerFallback() {
        when(authorService.findById(1L)).thenThrow(new RuntimeException("Service unavailable"));

        final AuthorDto result = authorController.findById(1L);

        assertNull(result);
        verify(authorService, times(1)).findById(1L);
    }

    @Test
    void findAllRateLimiterFallback() throws Exception {
        // Имитируем превышение лимита запросов
        for (int i = 0; i < 3; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author"))
                        .andExpect(status().isOk());
            } catch (final ServletException e) {
                if (e.getRootCause() instanceof RequestNotPermitted) {
                    // Проверяем, что fallback-метод сработал и вернул исключение TooManyRequestsException
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author"))
                            .andExpect(status().isTooManyRequests())
                            .andExpect(MockMvcResultMatchers.content().string("Превышен лимит запросов. Попробуйте позже"));
                }
            }
        }
    }

    @Test
    void findAuthorRateLimiterFallback() throws Exception {
        // Имитируем превышение лимита запросов
        for (int i = 0; i < 3; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author/{id}", 1L))
                        .andExpect(status().isOk());
            } catch (final ServletException e) {
                if (e.getRootCause() instanceof RequestNotPermitted) {
                    // Проверяем, что fallback-метод сработал и вернул исключение TooManyRequestsException
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author/{id}", 1L))
                            .andExpect(status().isTooManyRequests())
                            .andExpect(MockMvcResultMatchers.content().string("Превышен лимит запросов. Попробуйте позже"));
                }
            }
        }
    }
}
