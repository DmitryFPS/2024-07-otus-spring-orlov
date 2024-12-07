package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.AuthorRepository;

@Component
@RequiredArgsConstructor
public class AuthorHealthIndicator implements HealthIndicator {
    private final AuthorRepository authorRepository;

    @Override
    public Health health() {
        final long count = authorRepository.count();
        if (count > 0) {
            return Health.up()
                    .withDetail("message", "Есть авторы")
                    .build();
        }
        return Health.down()
                .withDetail("message", "Нет авторов")
                .build();
    }
}
