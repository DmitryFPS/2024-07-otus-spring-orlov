package ru.otus.hw.repositories;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Comment;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@DataMongoTest
class CommentRepositoryImplTest {
    @Autowired
    private CommentRepository repository;

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    void testSaveNewComment() {
        final Book relatedBook = mongoOperations.findById("1", Book.class);
        final Comment commentToSave = new Comment(null, "Some comment", relatedBook);

        final Comment savedComment = repository.save(commentToSave);
        assertThat(savedComment).isNotNull()
                .matches(book -> book.getId() != null)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(commentToSave);

        final Comment foundComment = mongoOperations.findById(savedComment.getId(), Comment.class);
        assertThat(foundComment)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(savedComment);
    }

    @Test
    void testSaveUpdateComment() {
        final String updatedMessage = "New comment";
        final Comment existingComment = mongoOperations.findById("1", Comment.class);
        assertThat(existingComment).isNotNull();

        final String oldCommentText = existingComment.getCommentText();
        final Comment commentToUpdate = new Comment(existingComment.getId(), updatedMessage, existingComment.getBook());

        final Comment updated = repository.save(commentToUpdate);
        assertThat(updated).isNotNull()
                .matches(b -> b.getCommentText().equals(updatedMessage));

        final Comment comment = mongoOperations.findById(updated.getId(), Comment.class);

        assertThat(comment).isNotNull()
                .matches(b -> b.getCommentText().equals(updatedMessage))
                .matches(b -> !b.getCommentText().equals(oldCommentText));
    }

    @Test
    void testDeleteComment() {
        final Comment commentToDelete = mongoOperations.findById("2", Comment.class);
        assertThat(commentToDelete).isNotNull();
        repository.deleteById("2");
        assertThat(mongoOperations.findById("2", Comment.class)).isNull();
    }

    @Nested
    @DirtiesContext(classMode = BEFORE_CLASS)
    class TestCommentRepositoryFind {
        @Test
        void testReturnCorrectCommentById() {
            final Comment expected = mongoOperations.findById("1", Comment.class);
            final Optional<Comment> actual = repository.findById("1");
            assertThat(actual).isPresent();

            final Comment comment = actual.get();
            assertThat(comment)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFields("book")
                    .isEqualTo(expected);
        }

        @Test
        void testReturnEmptyCommentById() {
            final Optional<Comment> actual = repository.findById("100");
            assertThat(actual).isNotPresent();
        }

        @Test
        void testReturnCorrectCommentListByBookId() {
            final Comment comment4 = mongoOperations.findById("4", Comment.class);
            final Comment comment5 = mongoOperations.findById("5", Comment.class);

            final List<Comment> actual = repository.findAllByBookId("2");
            final List<Comment> expected = Arrays.asList(comment4, comment5);

            assertThat(actual)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFields("book")
                    .isEqualTo(expected);
        }
    }
}
