package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({GenreServiceImpl.class, GenreMapperImpl.class})
class GenreServiceImplTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private GenreServiceImpl genreService;

    @Autowired
    private GenreMapper genreMapper;

    private static final String GENRE_ID = "6727f890b378982e3e747744";


    @Test
    void testFindAll() {
        final Flux<GenreDto> actualGenres = genreService.findAll();
        final List<GenreDto> expectedGenres = genreMapper.genresToGenresDto(mongoOperations.findAll(Genre.class));

        assertThat(actualGenres.collectList().block()).usingRecursiveComparison().isEqualTo(expectedGenres);
    }

    @Test
    void testFindById() {
        final Mono<GenreDto> actualAuthor = genreService.findById(GENRE_ID);
        final GenreDto expectedAuthor = genreMapper.genreToGenreDto(mongoOperations.findById(GENRE_ID, Genre.class));

        assertEquals(expectedAuthor, actualAuthor.block());
    }
}
