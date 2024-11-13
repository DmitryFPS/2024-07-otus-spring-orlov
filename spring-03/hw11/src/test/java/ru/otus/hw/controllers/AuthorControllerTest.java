package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
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
import ru.otus.hw.services.AuthorServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;


@SpringBootTest(classes = {AuthorController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AuthorServiceImpl.class)
@EnableAutoConfiguration
class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorServiceImpl authorService;


    @Test
    public void testFindAll() {
        final List<AuthorDto> authors = List.of(
                new AuthorDto("1", "Author_1"),
                new AuthorDto("2", "Author_2")
        );

        BDDMockito.given(authorService.findAll()).willReturn(Flux.fromIterable(authors));

        final Flux<AuthorDto> result = webTestClient.get().uri("/api/v1/author")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        final StepVerifier.FirstStep<AuthorDto> step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = null;
        for (final AuthorDto author : authors) {
            stepResult = step.expectNext(author);
        }

        assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    public void testFindById() {
        final AuthorDto author = new AuthorDto("1", "Author_1");
        BDDMockito.given(authorService.findById(anyString())).willReturn(Mono.just(author));

        final Flux<AuthorDto> result = webTestClient.get().uri("/api/v1/author/{id}", "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        final StepVerifier.FirstStep<AuthorDto> step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = step.expectNext(author);

        assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    public void testInternalException() {
        BDDMockito.given(authorService.findById(anyString())).willThrow(RuntimeException.class);

        webTestClient.get()
                .uri("/api/v1/author/{id}", "777")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void testNotFound() {
        BDDMockito.given(authorService.findAll()).willReturn(Flux.fromIterable(List.of()));

        webTestClient.get().uri("/api/v1/other")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
