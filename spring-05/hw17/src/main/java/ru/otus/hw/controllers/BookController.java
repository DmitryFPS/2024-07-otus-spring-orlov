package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;


    @GetMapping("/book")
    public String findBooks(final Model model) {
        model.addAttribute("books", bookService.findAll());
        return "bookPages/books";
    }

    @GetMapping("/book/{id}")
    public String showFormForUpdatingBook(@PathVariable final long id,
                                          final Model model) {
        model.addAttribute("updateBook", bookService.getBookUpdateDtoById(id));
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "bookPages/updateBook";
    }

    @GetMapping("/book/form")
    public String createBookForm(final Model model) {
        model.addAttribute("createBook", new BookCreateDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "bookPages/createBook";
    }

    @PostMapping("/book")
    public String createBook(@ModelAttribute("createBook") final BookCreateDto book) {
        bookService.create(book);
        return "redirect:/";
    }

    @PatchMapping("/book")
    public String updateBook(@ModelAttribute("updateBook") final BookUpdateDto book) {
        bookService.update(book);
        return "redirect:/";
    }

    @DeleteMapping("/book/{id}")
    public String deleteBook(@PathVariable("id") final long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }
}
