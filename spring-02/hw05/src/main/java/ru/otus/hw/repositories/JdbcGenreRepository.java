package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Genre> findAll() {
        return jdbc.query("select id, name from genres", new GenreRowMapper());
    }

    @Override
    public Optional<Genre> findById(final long id) {
        final Map<String, Object> params = Collections.singletonMap("id", id);
        return jdbc.query("select id, name from genres where id = :id", params, new GenreRowMapper()).stream()
                .findFirst();
    }

    @Override
    public List<Genre> findAllByIds(final Set<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return jdbc.query("select id, name from genres where id in (:ids)", Collections.singletonMap("ids", ids),
                new GenreRowMapper());
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(final ResultSet rs, final int i) throws SQLException {
            final long id = rs.getLong("id");
            final String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
