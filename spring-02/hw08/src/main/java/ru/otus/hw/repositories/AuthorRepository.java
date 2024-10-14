package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.model.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
