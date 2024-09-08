package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryImpl implements GenreRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        return entityManager.createQuery("select genre from Genre as genre", Genre.class).getResultList();
    }

    @Override
    public Optional<Genre> findById(final Long id) {
        return ofNullable(entityManager.find(Genre.class, id));
    }

    @Override
    public List<Genre> findAllByIds(final Set<Long> ids) {
        return entityManager.createQuery("select genre from Genre as genre where genre.id in :ids", Genre.class)
                .setParameter("ids", ids).getResultList();
    }
}
