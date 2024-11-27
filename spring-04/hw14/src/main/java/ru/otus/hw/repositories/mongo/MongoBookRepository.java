package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.model.Book;

import java.util.List;

public interface MongoBookRepository extends MongoRepository<Book, String> {
    @NonNull
    List<Book> findAll();
}
