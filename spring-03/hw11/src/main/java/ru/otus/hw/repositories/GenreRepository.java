package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw.model.Genre;

import java.util.Set;

@Repository
public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {
    Flux<Genre> findAllByIdIn(final Set<String> ids);
}
