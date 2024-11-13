package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.model.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({AuthorServiceImpl.class, AuthorMapperImpl.class})
class AuthorServiceImplTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private AuthorServiceImpl authorService;

    @Autowired
    private AuthorMapperImpl authorMapper;

    private static final String AUTHOR_ID = "1747f790b378982e1e783344";


    @Test
    void testFindAll() {
        final Flux<AuthorDto> actualAuthor = authorService.findAll();
        final List<AuthorDto> expectedAuthor = authorMapper.authorsToAuthorsDto(mongoOperations.findAll(Author.class));

        assertThat(actualAuthor.collectList().block()).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @Test
    void testFindById() {
        final Mono<AuthorDto> actualAuthor = authorService.findById(AUTHOR_ID);
        final AuthorDto expectedAuthor = authorMapper.authorToAuthorDto(mongoOperations.findById(AUTHOR_ID, Author.class));

        assertEquals(expectedAuthor, actualAuthor.block());
    }
}
