package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.entity.Book;

import java.util.List;

public interface JpaBookRepository extends JpaRepository<Book, Long> {
    @NonNull
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "author-genre-entity-graph")
    List<Book> findAll();
}
