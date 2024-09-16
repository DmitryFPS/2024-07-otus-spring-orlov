package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Book;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    @PersistenceContext
    private final EntityManager entityManager;


    @Override
    public Optional<Book> findById(final Long id) {
        return ofNullable(entityManager.find(Book.class, id, getHintMap()));
    }

    @Override
    public List<Book> findAll() {
        final TypedQuery<Book> typedQuery = entityManager
                .createQuery("select book from Book as book", Book.class);
        typedQuery.setHint(GraphSemantic.FETCH.getJakartaHintName(),
                entityManager.getEntityGraph("author-entity-graph"));
        return typedQuery.getResultList();
    }

    @Override
    public Book save(final Book book) {
        if (isNull(book.getId())) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(final Long id) {
        final Book book = entityManager.find(Book.class, id);
        if (nonNull(book)) {
            entityManager.remove(book);
        }
    }

    private Map<String, Object> getHintMap() {
        return Collections.singletonMap(GraphSemantic.FETCH.getJakartaHintName(), obtainAuthorAndGenreEntityGraph());
    }

    private EntityGraph<?> obtainAuthorAndGenreEntityGraph() {
        return entityManager.getEntityGraph("author-genre-entity-graph");
    }
}
