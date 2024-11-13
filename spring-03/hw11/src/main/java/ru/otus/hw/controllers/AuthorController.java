package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@RequiredArgsConstructor
@RestController
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/api/v1/author")
    public Flux<AuthorDto> findAll() {
        return authorService.findAll();
    }

    @GetMapping("/api/v1/author/{id}")
    public Mono<AuthorDto> findById(@PathVariable("id") final String id) {
        return authorService.findById(id);
    }
}
