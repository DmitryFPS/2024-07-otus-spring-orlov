package ru.otus.hw.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.mongo.CommentDto;
import ru.otus.hw.model.Comment;

public class CommentProcessor implements ItemProcessor<Comment, CommentDto> {

    @Override
    public CommentDto process(final Comment item) {
        return new CommentDto(item.getId(), item.getCommentText(), item.getBook().getId());
    }
}
