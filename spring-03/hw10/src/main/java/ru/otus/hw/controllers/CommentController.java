package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/v1/comment/{id}")
    public List<CommentDto> findAll(@PathVariable("id") final Long id) {
        return commentService.findByBookId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/comment")
    public CommentDto create(@RequestBody final CommentCreateDto commentCreateDto) {
        return commentService.create(commentCreateDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/v1/comment/{id}")
    public void delete(@PathVariable("id") final Long id) {
        commentService.deleteById(id);
    }
}
