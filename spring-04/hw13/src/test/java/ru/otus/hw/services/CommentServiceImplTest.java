package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exception.NotFoundException;
import ru.otus.hw.mapper.CommentMapperImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Service для работы с Comments")
@DataJpaTest
@Import({CommentServiceImpl.class, CommentMapperImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceImplTest {
    @Autowired
    private CommentServiceImpl serviceTest;


    @Test
    void testGetCommentById() {
        final CommentDto expectedComment = getComments().get(0);
        final CommentDto actualComment = serviceTest.findById(1L);
        assertThat(actualComment).isEqualTo(expectedComment);
    }

    @Test
    void testGetCommentsByBook() {
        final List<CommentDto> expectedComments = getComments();
        final List<CommentDto> actualComments = serviceTest.findByBookId(1L);
        Assertions.assertThat(actualComments).containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testSaveBookComment() {
        final CommentDto expectedComment = new CommentDto(null, "CommentNew");
        final CommentDto returnedComment = serviceTest.create(expectedComment.getCommentText(), 1L);
        assertThat(returnedComment)
                .isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedComment);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDelete() {
        final CommentDto comment = serviceTest.findById(1L);
        serviceTest.deleteById(comment.getId());

        assertThatThrownBy(() -> serviceTest.findById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Не удалось получить комментарий по Id: 1");
    }

    private List<CommentDto> getComments() {
        final CommentDto expectedComment_1 =
                new CommentDto(1L, "Эпический шедевр, который охватывает всю панораму человеческой " +
                        "жизни в эпоху наполеоновских войн. Толстой с мастерством рисует незабываемых персонажей, " +
                        "исследует темы войны, мира, любви и религии.");
        final CommentDto expectedComment_2 =
                new CommentDto(2L, "Масштабная и сложная работа, но ее стоит прочитать за глубину " +
                        "психологических портретов, захватывающие батальные сцены и философские " +
                        "размышления о смысле жизни.");
        final CommentDto expectedComment_3 =
                new CommentDto(3L, "Толстой не просто описывает исторические события, " +
                        "а создает живую и дышащую картину России XIX века, с ее дворянством, " +
                        "крестьянством и военной жизнью.");
        final CommentDto expectedComment_4 =
                new CommentDto(4L, "\"Война и мир\" - это не только исторический роман, но и " +
                        "глубокое исследование человеческой души, раскрывающее сложность человеческой природы.");
        final CommentDto expectedComment_5 =
                new CommentDto(5L, "Классика мировой литературы, которая остается актуальной и сегодня," +
                        " поднимая вечные вопросы о добре и зле, войне и мире, любви и судьбе.");

        return List.of(expectedComment_1, expectedComment_2, expectedComment_3, expectedComment_4, expectedComment_5);
    }
}
