package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findAllByIdIn(final Set<Long> ids);
}
