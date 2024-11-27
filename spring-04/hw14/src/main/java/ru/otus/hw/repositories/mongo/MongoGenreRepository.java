package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.model.Genre;

public interface MongoGenreRepository extends MongoRepository<Genre, String> {
}
