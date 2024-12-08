package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comment/{id}")
    public String findComments(@PathVariable("id") final long id,
                               final Model model) {
        final List<CommentDto> comments = commentService.findByBookId(id);
        model.addAttribute("comments", comments);
        model.addAttribute("bookId", id);
        return "commentPages/comments";
    }

    @GetMapping(value = "/comment/form/{id}")
    public String createCommentForm(@PathVariable("id") final long bookId,
                                    final Model model) {
        model.addAttribute("bookId", bookId);
        model.addAttribute("text", null);
        return "commentPages/createComment";
    }

    @PostMapping("/comment/{id}")
    public String createComment(@PathVariable("id") final long bookId,
                                @ModelAttribute("text") final String text) {
        commentService.create(text, bookId);
        return String.format("redirect:/comment/%s", bookId);
    }

    @DeleteMapping(value = "/comment/{id}", params = "bookId")
    public String deleteComment(@PathVariable("id") final long id,
                                @RequestParam final long bookId) {
        commentService.deleteById(id);
        return String.format("redirect:/comment/%s", bookId);
    }
}
