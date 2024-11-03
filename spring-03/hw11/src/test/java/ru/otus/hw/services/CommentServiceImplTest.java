package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.CommentMapperImpl;
import ru.otus.hw.model.Comment;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({CommentServiceImpl.class, CommentMapperImpl.class})
class CommentServiceImplTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private CommentMapperImpl commentMapper;

    private static final String BOOK_ID = "g527f740b378982e3e241144";

    private static final String COMMENT_ID = "n727v790c378982e3e74ee44";


    @Test
    void testFindById() {
        final Mono<CommentDto> actualComment = commentService.findById(COMMENT_ID);
        final CommentDto expectedComment = commentMapper
                .commentToCommentDto(mongoOperations.findById(COMMENT_ID, Comment.class));

        assertEquals(expectedComment, actualComment.block());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testCreateComment() {
        final Flux<CommentDto> comments = commentService.findByBookId(BOOK_ID);
        assertTrue(Objects.requireNonNull(comments.collectList().block()).stream()
                .map(CommentDto::getCommentText)
                .noneMatch("content_expected"::equals));

        final CommentCreateDto commentCreateDto = new CommentCreateDto(BOOK_ID, "content_expected");
        final Mono<CommentDto> commentDtoMono = commentService.create(commentCreateDto);
        assertEquals("content_expected", Objects.requireNonNull(commentDtoMono.block()).getCommentText());

        final Flux<CommentDto> expectedComments = commentService.findByBookId(BOOK_ID);
        assertTrue(Objects.requireNonNull(expectedComments.collectList().block()).stream()
                .map(CommentDto::getCommentText)
                .anyMatch("content_expected"::equals));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDeleteComment() {
        final Mono<CommentDto> comment = commentService.findById(COMMENT_ID);
        assertThat(comment.block()).isNotNull();

        final Mono<Void> delete = commentService.deleteById(COMMENT_ID);
        StepVerifier.create(delete)
                .expectComplete()
                .verify();

        assertThatThrownBy(() -> commentService.findById(COMMENT_ID).block())
                .isInstanceOf(RuntimeException.class);
    }
}
