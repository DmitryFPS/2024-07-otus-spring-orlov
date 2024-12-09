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
import ru.otus.hw.repositories.AuthorRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@Import(AuthorHealthIndicator.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AuthorHealthIndicatorTest {
    @Autowired
    private AuthorHealthIndicator authorHealthIndicator;

    @MockBean
    private AuthorRepository authorRepository;


    @Test
    void testHealthUp() {
        Mockito.when(authorRepository.count()).thenReturn(1L);

        final Health health = authorHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Есть авторы", health.getDetails().get("message"));
    }

    @Test
    void testHealthDown() {
        Mockito.when(authorRepository.count()).thenReturn(0L);

        final Health health = authorHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Нет авторов", health.getDetails().get("message"));
    }

    @Test
    void testHealthException() {
        Mockito.when(authorRepository.count()).thenThrow(RuntimeException.class);

        final Health health = authorHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("java.lang.RuntimeException: null", health.getDetails().get("error"));
    }
}
