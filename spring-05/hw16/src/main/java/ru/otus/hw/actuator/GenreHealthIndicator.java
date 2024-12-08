package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.GenreRepository;

@Component
@RequiredArgsConstructor
public class GenreHealthIndicator implements HealthIndicator {
    private final GenreRepository genreRepository;

    @Override
    public Health health() {
        final long count = genreRepository.count();
        if (count > 0) {
            return Health.up()
                    .withDetail("message", "Есть жанры")
                    .build();
        }
        return Health.down()
                .withDetail("message", "Нет жанров")
                .build();
    }
}
