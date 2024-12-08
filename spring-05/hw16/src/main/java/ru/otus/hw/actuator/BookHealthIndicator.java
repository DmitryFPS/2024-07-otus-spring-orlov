package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class BookHealthIndicator implements HealthIndicator {
    private final BookRepository bookRepository;

    @Override
    public Health health() {
        final long count = bookRepository.count();
        if (count > 0) {
            return Health.up()
                    .withDetail("message", "Есть книги")
                    .build();
        }
        return Health.down()
                .withDetail("message", "Нет книг")
                .build();
    }
}
