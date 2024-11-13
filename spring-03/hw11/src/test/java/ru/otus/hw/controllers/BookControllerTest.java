package ru.otus.hw.controllers;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookServiceImpl;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {BookController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(BookServiceImpl.class)
@EnableAutoConfiguration
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookServiceImpl bookService;


    @Test
    public void testFindAll() {
        final List<BookDto> books = getBookDtos();
        BDDMockito.given(bookService.findAll()).willReturn(Flux.fromIterable(books));

        final Flux<BookDto> result = webTestClient.get().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        final StepVerifier.FirstStep<BookDto> step = StepVerifier.create(result);
        StepVerifier.Step<BookDto> stepResult = null;
        for (final BookDto book : books) {
            stepResult = step.expectNext(book);
        }

        assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    void testCreate() {
        final BookCreateDto createDto = new BookCreateDto("title", "1", Set.of("1"));
        final AuthorDto author = new AuthorDto("1", "Author_1");
        final BookDto book = new BookDto("1", "title", author, List.of(new GenreDto("1", "name_1")));

        BDDMockito.given(bookService.create(createDto)).willReturn(Mono.just(book));

        final WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(createDto), BookDto.class)
                .exchange();

        response.expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(book.getId())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.author.id").isEqualTo(book.getAuthor().getId())
                .jsonPath("$.author.fullName").isEqualTo(book.getAuthor().getFullName())
                .jsonPath("$.genres[0].id").isEqualTo(book.getGenres().get(0).getId())
                .jsonPath("$.genres[0].name").isEqualTo(book.getGenres().get(0).getName());
    }

    @Test
    void testUpdate() {
        final BookUpdateDto updateDto = new BookUpdateDto("1", "title_update", "1", Set.of("1"));
        final AuthorDto author = new AuthorDto("1", "Author_1");
        final BookDto book = new BookDto("1", "title_update", author, List.of(new GenreDto("1", "name_1")));

        BDDMockito.given(bookService.update(updateDto)).willReturn(Mono.just(book));

        final WebTestClient.ResponseSpec response = webTestClient.patch()
                .uri("/api/v1/book/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateDto), BookDto.class)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(book.getId())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.author.id").isEqualTo(book.getAuthor().getId())
                .jsonPath("$.author.fullName").isEqualTo(book.getAuthor().getFullName())
                .jsonPath("$.genres[0].id").isEqualTo(book.getGenres().get(0).getId())
                .jsonPath("$.genres[0].name").isEqualTo(book.getGenres().get(0).getName());
    }

    @Test
    void testDelete() {
        final WebTestClient.ResponseSpec response = webTestClient.delete()
                .uri("/api/v1/book/{id}", "1")
                .exchange();
        response.expectStatus().isNoContent();
    }

    @Test
    public void testClientException() {
        webTestClient.post()
                .uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(new BookCreateDto()), BookDto.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void testServerException() {
        BDDMockito.given(bookService.create(Mockito.any())).willThrow(RuntimeException.class);
        final BookCreateDto createDto = new BookCreateDto("title", "1", Set.of("1"));
        webTestClient.post()
                .uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(createDto), BookDto.class)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void testNotFound() {
        final List<BookDto> books = getBookDtos();
        BDDMockito.given(bookService.findAll()).willReturn(Flux.fromIterable(books));

        webTestClient.get().uri("/api/v1/other")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }


    private static @NotNull List<BookDto> getBookDtos() {
        final GenreDto genre_1 = new GenreDto("1", "name_1");
        final GenreDto genre_2 = new GenreDto("2", "name_2");
        final GenreDto genre_3 = new GenreDto("3", "name_3");
        final List<GenreDto> genres = List.of(genre_1, genre_2, genre_3);

        final AuthorDto author_1 = new AuthorDto("1", "Author_1");
        final AuthorDto author_2 = new AuthorDto("2", "Author_2");
        final BookDto book_1 = new BookDto("1", "title", author_1, genres);
        final BookDto book_2 = new BookDto("1", "title", author_2, genres);

        return List.of(book_1, book_2);
    }
}
