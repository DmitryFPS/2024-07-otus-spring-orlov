package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@RestController
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/api/v1/genre")
    public Flux<GenreDto> findAll() {
        return genreService.findAll();
    }

    @GetMapping("/api/v1/genre/{id}")
    public Mono<GenreDto> findById(@PathVariable("id") final String id) {
        return genreService.findById(id);
    }
}
