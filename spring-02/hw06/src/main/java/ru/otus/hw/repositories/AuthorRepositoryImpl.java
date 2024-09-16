package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Author;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class AuthorRepositoryImpl implements AuthorRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Author> findAll() {
        return entityManager.createQuery("select author from Author as author", Author.class).getResultList();
    }

    @Override
    public Optional<Author> findById(final Long id) {
        return ofNullable(entityManager.find(Author.class, id));
    }
}
