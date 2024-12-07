package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookHealthIndicator implements HealthIndicator {
    private final BookRepository bookRepository;

    @Override
    public Health health() {
        try {
            final long count = bookRepository.count();
            if (count > 0) {
                return Health.up()
                        .withDetail("message", "Есть книги")
                        .build();
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return Health.down(e).build();
        }

        return Health.down()
                .withDetail("message", "Нет книг")
                .build();
    }
}
