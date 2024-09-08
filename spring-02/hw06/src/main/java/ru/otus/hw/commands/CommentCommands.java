package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find all comments by book id", key = "cbid")
    public String findAllCommentsByBook(final long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comment by id", key = "cid")
    public String findCommentById(final long id) {
        return commentConverter.commentToString(commentService.findById(id));
    }

    @ShellMethod(value = "Insert comment", key = "ic")
    public String insertComment(final String content, final long bookId) {
        final CommentDto savedComment = commentService.create(content, bookId);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Update comment", key = "uc")
    public String updateComment(final long id, final String content) {
        final CommentDto savedComment = commentService.update(id, content);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Delete comment by id", key = "dcid")
    public void deleteComment(final long id) {
        commentService.deleteById(id);
    }
}
