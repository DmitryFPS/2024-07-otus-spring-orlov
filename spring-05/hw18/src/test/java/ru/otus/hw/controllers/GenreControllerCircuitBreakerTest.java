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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

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
class GenreControllerCircuitBreakerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenreController genreController;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private GenreService genreService;


    @BeforeEach
    void setUp() {
        // Сбросить состояние RateLimiter и CircuitBreaker перед каждым тестом
        final RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("rateLimiter");
        rateLimiter.changeLimitForPeriod(2); // Лимит 2 запроса в секунду
        when(genreService.findAll()).thenReturn(Collections.emptyList());

        final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuitBreaker");
        circuitBreaker.reset();
    }


    @Test
    void findAllSuccess() {
        final List<GenreDto> expectedGenres = List.of(
                new GenreDto(1L, "Genre1"), new GenreDto(2L, "Genre2"));

        when(genreService.findAll()).thenReturn(expectedGenres);
        final List<GenreDto> result = genreController.findAll();

        assertEquals(expectedGenres, result);
        verify(genreService, times(1)).findAll();
    }

    @Test
    void findGenreSuccess() {
        final GenreDto expectedGenre = new GenreDto(1L, "Genre1");

        when(genreService.findById(1L)).thenReturn(expectedGenre);
        final GenreDto result = genreController.findById(1L);

        assertEquals(expectedGenre, result);
        verify(genreService, times(1)).findById(1L);
    }

    @Test
    void findAllCircuitBreakerFallback() {
        when(genreService.findAll()).thenThrow(new RuntimeException("Service unavailable"));

        final List<GenreDto> result = genreController.findAll();

        assertEquals(Collections.emptyList(), result);
        verify(genreService, times(1)).findAll();
    }

    @Test
    void findGenreCircuitBreakerFallback() {
        when(genreService.findById(1L)).thenThrow(new RuntimeException("Service unavailable"));

        final GenreDto result = genreController.findById(1L);

        assertNull(result);
        verify(genreService, times(1)).findById(1L);
    }

    @Test
    void findAllRateLimiterFallback() throws Exception {
        // Имитируем превышение лимита запросов
        for (int i = 0; i < 3; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre"))
                        .andExpect(status().isOk());
            } catch (final ServletException e) {
                if (e.getRootCause() instanceof RequestNotPermitted) {
                    // Проверяем, что fallback-метод сработал и вернул исключение TooManyRequestsException
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre"))
                            .andExpect(status().isTooManyRequests())
                            .andExpect(MockMvcResultMatchers.content().string("Превышен лимит запросов. Попробуйте позже"));
                }
            }
        }
    }

    @Test
    void findGenreRateLimiterFallback() throws Exception {
        // Имитируем превышение лимита запросов
        for (int i = 0; i < 3; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre/{id}", 1L))
                        .andExpect(status().isOk());
            } catch (final ServletException e) {
                if (e.getRootCause() instanceof RequestNotPermitted) {
                    // Проверяем, что fallback-метод сработал и вернул исключение TooManyRequestsException
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre/{id}", 1L))
                            .andExpect(status().isTooManyRequests())
                            .andExpect(MockMvcResultMatchers.content().string("Превышен лимит запросов. Попробуйте позже"));
                }
            }
        }
    }
}
