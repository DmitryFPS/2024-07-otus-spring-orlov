package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapperImpl;
import ru.otus.hw.model.Comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({CommentServiceImpl.class, CommentMapperImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl serviceTest;

    @Autowired
    private CommentMapperImpl commentMapper;

    @Autowired
    private MongoOperations mongoOperations;

    @ParameterizedTest
    @MethodSource("GetCommentByIdTest")
    void testGetCommentById(final String id) {
        final CommentDto actualComment = serviceTest.findById(id);
        final CommentDto expectedComment = commentMapper.commentToCommentDto(mongoOperations.findById(id, Comment.class));

        assertThat(actualComment).isEqualTo(expectedComment);
    }

    private static Object[] GetCommentByIdTest() {
        return new Object[]{
                new Object[]{"1"},
                new Object[]{"2"},
                new Object[]{"3"},
                new Object[]{"4"},
                new Object[]{"5"},
                new Object[]{"6"}
        };
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testSaveBookComment() {
        final CommentDto expectedComment = new CommentDto(null, "CommentNew");
        final CommentDto returnedComment = serviceTest.create(expectedComment.getCommentText(), "1");
        assertThat(returnedComment)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedComment);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdatedBookComment() {
        final CommentDto expectedComment = new CommentDto("1", "CommentUpdate");
        final CommentDto returnedComment =
                serviceTest.update(expectedComment.getId(), expectedComment.getCommentText());

        assertThat(returnedComment)
                .isNotNull()
                .isEqualTo(expectedComment);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDelete() {
        final CommentDto comment = serviceTest.findById("1");
        serviceTest.deleteById(comment.getId());

        assertThatThrownBy(() -> serviceTest.findById("1"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Не удалось получить комментарий по Id: 1");
    }
}
