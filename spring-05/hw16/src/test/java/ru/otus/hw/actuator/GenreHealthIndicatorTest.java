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
import ru.otus.hw.repositories.GenreRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@Import(GenreHealthIndicator.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class GenreHealthIndicatorTest {
    @Autowired
    private GenreHealthIndicator genreHealthIndicator;

    @MockBean
    private GenreRepository genreRepository;


    @Test
    void testHealthUp() {
        Mockito.when(genreRepository.count()).thenReturn(1L);

        final Health health = genreHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Есть жанры", health.getDetails().get("message"));
    }

    @Test
    void testHealthDown() {
        Mockito.when(genreRepository.count()).thenReturn(0L);

        final Health health = genreHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Нет жанров", health.getDetails().get("message"));
    }

    @Test
    void testHealthException() {
        Mockito.when(genreRepository.count()).thenThrow(RuntimeException.class);

        final Health health = genreHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("java.lang.RuntimeException: null", health.getDetails().get("error"));
    }
}
