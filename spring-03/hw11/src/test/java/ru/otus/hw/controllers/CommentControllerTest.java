package ru.otus.hw.controllers;

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
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(classes = {CommentController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(CommentServiceImpl.class)
@EnableAutoConfiguration
class CommentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CommentServiceImpl commentService;


    @Test
    void testFindAll() {
        final List<CommentDto> comments = List.of(
                new CommentDto("1", "Comment_1"),
                new CommentDto("2", "Comment_2"),
                new CommentDto("3", "Comment_3"));
        BDDMockito.given(commentService.findByBookId(anyString())).willReturn(Flux.fromIterable(comments));

        final Flux<CommentDto> result = webTestClient.get().uri("/api/v1/comment/{id}", "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(CommentDto.class)
                .getResponseBody();

        final StepVerifier.FirstStep<CommentDto> step = StepVerifier.create(result);
        StepVerifier.Step<CommentDto> stepResult = null;
        for (final CommentDto comment : comments) {
            stepResult = step.expectNext(comment);
        }

        assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    void testCreate() {
        final CommentCreateDto createDto = new CommentCreateDto("1", "content");
        final CommentDto commentDto = new CommentDto("1", "content");

        BDDMockito.given(commentService.create(createDto)).willReturn(Mono.just(commentDto));

        final WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(createDto), CommentDto.class)
                .exchange();

        response.expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(commentDto.getId())
                .jsonPath("$.commentText").isEqualTo(commentDto.getCommentText());
    }

    @Test
    void testDelete() {
        final WebTestClient.ResponseSpec response = webTestClient.delete()
                .uri("/api/v1/comment/{id}", "1")
                .exchange();
        response.expectStatus().isNoContent();
    }

    @Test
    public void testInternalException() {
        BDDMockito.given(commentService.create(Mockito.any())).willThrow(RuntimeException.class);

        webTestClient.post()
                .uri("/api/v1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(new CommentDto()), CommentDto.class)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void testNotFound() {
        BDDMockito.given(commentService.findByBookId(anyString())).willReturn(Flux.fromIterable(List.of()));

        webTestClient.get().uri("/api/v1/other")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
