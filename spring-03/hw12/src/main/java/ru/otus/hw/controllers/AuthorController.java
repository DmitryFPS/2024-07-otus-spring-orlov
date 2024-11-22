package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/author")
    public String findAuthors(final Model model) {
        final List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authorPages/authors";
    }

    @GetMapping("/author/{id}")
    public String findAuthorById(@PathVariable("id") final long id,
                                 final Model model) {
        final AuthorDto author = authorService.findById(id);
        model.addAttribute("author", author);
        return "authorPages/author";
    }
}
