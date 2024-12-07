package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.GenreRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreHealthIndicator implements HealthIndicator {
    private final GenreRepository genreRepository;

    @Override
    public Health health() {
        try {
            final long count = genreRepository.count();
            if (count > 0) {
                return Health.up()
                        .withDetail("message", "Есть жанры")
                        .build();
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return Health.down(e).build();
        }

        return Health.down()
                .withDetail("message", "Нет жанров")
                .build();
    }
}
