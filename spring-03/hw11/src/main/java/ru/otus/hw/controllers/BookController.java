package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.BookService;

@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;


    @GetMapping("/api/v1/book")
    public Flux<BookDto> findAll() {
        return bookService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/book")
    public Mono<BookDto> create(@Valid @RequestBody final BookCreateDto bookCreateDto) {
        return bookService.create(bookCreateDto);
    }

    @PatchMapping("/api/v1/book/{id}")
    public Mono<BookDto> update(@PathVariable("id") final String id,
                                @Valid @RequestBody final BookUpdateDto bookUpdateDto) {
        bookUpdateDto.setId(id);
        return bookService.update(bookUpdateDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/v1/book/{id}")
    public Mono<Void> delete(@PathVariable("id") final String id) {
        return bookService.deleteById(id);
    }
}
