package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "author-genre-entity-graph")
    @NonNull
    Optional<Book> findById(@NonNull final Long id);

    @NonNull
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "author-entity-graph")
    List<Book> findAll();
}
