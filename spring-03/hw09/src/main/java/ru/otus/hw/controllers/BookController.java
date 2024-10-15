package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.otus.hw.dto.BookEditDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;


    @GetMapping("/")
    public String findBooks(final Model model) {
        model.addAttribute("books", bookService.findAll());
        return "bookPages/books";
    }

    @GetMapping("/book/{id}")
    public String showEditBookForm(@PathVariable final long id,
                                   final Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "bookPages/editBook";
    }

    @GetMapping(value = "/book")
    public String createBookForm(final Model model) {
        model.addAttribute("book", new BookEditDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "bookPages/createBook";
    }

    @PostMapping(value = "/book")
    public String createBook(@ModelAttribute("book") final BookEditDto book) {
        bookService.create(book.getTitle(), book.getAuthor(), book.getGenres());
        return "redirect:/";
    }

    @PatchMapping("/book")
    public String updateBook(@ModelAttribute("book") final BookEditDto book) {
        bookService.update(book.getId(), book.getTitle(), book.getAuthor(), book.getGenres());
        return "redirect:/";
    }

    @DeleteMapping("/book/{id}")
    public String deleteBook(@PathVariable("id") final long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }
}
