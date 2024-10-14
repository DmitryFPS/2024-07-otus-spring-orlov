package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    @NonNull
    Optional<Book> findById(@NonNull final String id);

    @NonNull
    List<Book> findAll();
}
