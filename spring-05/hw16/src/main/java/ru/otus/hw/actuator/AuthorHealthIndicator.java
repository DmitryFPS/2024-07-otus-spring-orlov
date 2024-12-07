package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.AuthorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorHealthIndicator implements HealthIndicator {
    private final AuthorRepository authorRepository;

    @Override
    public Health health() {
        try {
            final long count = authorRepository.count();
            if (count > 0) {
                return Health.up()
                        .withDetail("message", "Есть авторы")
                        .build();
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return Health.down(e).build();
        }

        return Health.down()
                .withDetail("message", "Нет авторов")
                .build();
    }
}
