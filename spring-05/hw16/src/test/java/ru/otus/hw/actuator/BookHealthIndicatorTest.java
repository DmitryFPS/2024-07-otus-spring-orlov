package ru.otus.hw.actuator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.BookRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@Import(BookHealthIndicator.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookHealthIndicatorTest {
    @Autowired
    private BookHealthIndicator bookHealthIndicator;

    @MockBean
    private BookRepository bookRepository;


    @Test
    void testHealthUp() {
        Mockito.when(bookRepository.count()).thenReturn(1L);

        final Health health = bookHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Есть книги", health.getDetails().get("message"));
    }

    @Test
    void testHealthDown() {
        Mockito.when(bookRepository.count()).thenReturn(0L);

        final Health health = bookHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Нет книг", health.getDetails().get("message"));
    }

    @Test
    void testHealthException() {
        Mockito.when(bookRepository.count()).thenThrow(RuntimeException.class);

        final Health health = bookHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("java.lang.RuntimeException: null", health.getDetails().get("error"));
    }
}
