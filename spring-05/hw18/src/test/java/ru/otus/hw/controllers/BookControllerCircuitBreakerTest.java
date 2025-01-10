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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookControllerCircuitBreakerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookController bookController;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private BookService bookService;


    @BeforeEach
    void setUp() {
        // Сбросить состояние RateLimiter и CircuitBreaker перед каждым тестом
        final RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("rateLimiter");
        rateLimiter.changeLimitForPeriod(2); // Лимит 2 запроса в секунду
        when(bookService.findAll()).thenReturn(Collections.emptyList());

        final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuitBreaker");
        circuitBreaker.reset();
    }

    @Test
    void findAllSuccess() {
        final List<BookDto> expectedBooks = expectedBooks();
        when(bookService.findAll()).thenReturn(expectedBooks);
        final List<BookDto> result = bookController.findAll();

        assertEquals(expectedBooks, result);
        verify(bookService, times(1)).findAll();
    }

    @Test
    void findAllCircuitBreakerFallback() {
        when(bookService.findAll()).thenThrow(new RuntimeException("Service unavailable"));

        final List<BookDto> result = bookController.findAll();

        assertEquals(Collections.emptyList(), result);
        verify(bookService, times(1)).findAll();
    }

    @Test
    void findAllRateLimiterFallback() throws Exception {
        // Имитируем превышение лимита запросов
        for (int i = 0; i < 3; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/book"))
                        .andExpect(status().isOk());
            } catch (final ServletException e) {
                if (e.getRootCause() instanceof RequestNotPermitted) {
                    // Проверяем, что fallback-метод сработал и вернул исключение TooManyRequestsException
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/book"))
                            .andExpect(status().isTooManyRequests())
                            .andExpect(MockMvcResultMatchers.content().string("Превышен лимит запросов. Попробуйте позже"));
                }
            }
        }
    }

    private List<BookDto> expectedBooks() {
        final AuthorDto authorDto1 = new AuthorDto(1L, "Book 1");
        final AuthorDto authorDto2 = new AuthorDto(2L, "Book 2");

        return List.of(
                new BookDto(1L, "Book 1", authorDto1, Collections.emptyList()),
                new BookDto(2L, "Book 2", authorDto2, Collections.emptyList())
        );
    }
}
