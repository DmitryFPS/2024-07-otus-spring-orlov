package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/api/v1/author")
    public List<AuthorDto> findAll() {
        return authorService.findAll();
    }

    @GetMapping("/api/v1/author/{id}")
    public AuthorDto findById(@PathVariable("id") final Long id) {
        return authorService.findById(id);
    }
}
