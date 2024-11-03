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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(classes = {GenreController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(GenreServiceImpl.class)
@EnableAutoConfiguration
class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenreServiceImpl genreService;


    @Test
    public void testFindAll() {
        final List<GenreDto> genres = List.of(
                new GenreDto("1", "name_1"),
                new GenreDto("2", "name_2"),
                new GenreDto("3", "name_3")
        );

        BDDMockito.given(genreService.findAll()).willReturn(Flux.fromIterable(genres));

        final Flux<GenreDto> result = webTestClient.get().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        final StepVerifier.FirstStep<GenreDto> step = StepVerifier.create(result);
        StepVerifier.Step<GenreDto> stepResult = null;
        for (final GenreDto genre : genres) {
            stepResult = step.expectNext(genre);
        }

        assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    public void testFindById() {
        final GenreDto genre = new GenreDto("1", "name_1");
        BDDMockito.given(genreService.findById(anyString())).willReturn(Mono.just(genre));

        final Flux<GenreDto> result = webTestClient.get().uri("/api/v1/genre/{id}", "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        final StepVerifier.FirstStep<GenreDto> step = StepVerifier.create(result);
        StepVerifier.Step<GenreDto> stepResult = step.expectNext(genre);

        assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    public void testInternalException() {
        BDDMockito.given(genreService.findById(anyString())).willThrow(RuntimeException.class);

        webTestClient.get()
                .uri("/api/v1/genre/{id}", "777")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void testNotFound() {
        BDDMockito.given(genreService.findAll()).willReturn(Flux.fromIterable(List.of()));

        webTestClient.get().uri("/api/v1/other")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
