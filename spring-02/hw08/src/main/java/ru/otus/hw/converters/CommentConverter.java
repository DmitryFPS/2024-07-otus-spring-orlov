package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@RequiredArgsConstructor
@Component
public class CommentConverter {
    public String commentToString(final CommentDto comment) {
        return "Id: %s, title: %s".formatted(comment.getId(), comment.getCommentText());
    }
}
