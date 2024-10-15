package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookEditDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final BookMapper bookMapper;

    @GetMapping("/")
    public String getAllBooks(final Model model) {
        final List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "bookPages/books";
    }

    @GetMapping("book/{id}")
    public String getBookById(@PathVariable final long id, final Model model) {
        final BookDto book = bookService.findById(id);
        final BookEditDto bookEdit = bookMapper.bookDtoToBookEditDto(book);
        final List<AuthorDto> authors = authorService.findAll();
        final List<GenreDto> genres = genreService.findAll();
        model.addAttribute("book", bookEdit);
        model.addAttribute("authors", authors);
        model.addAttribute("allGenres", genres);
        model.addAttribute("bookGenres", book.getGenres());
        return "bookPages/book";
    }

    @GetMapping(value = "book", params = "authorId")
    public String getBooksByAuthorId(@RequestParam("authorId") final long authorId, final Model model) {
        final List<BookDto> books = bookService.findAllByAuthorId(authorId);
        model.addAttribute("books", books);
        model.addAttribute("authorTitleIsVisible", true);
        model.addAttribute("authorColumnIsVisible", false);
        model.addAttribute("showGoToAllBooks", true);
        return "bookPages/bookList";
    }

    @GetMapping(value = "book/new", params = "authorId")
    public String getNewFormToBook(@RequestParam("authorId") final long authorId, final Model model) {
        final List<AuthorDto> authors = authorService.findAll();
        final List<GenreDto> allGenres = genreService.findAll();
        final BookEditDto newBook = new BookEditDto();
        newBook.setAuthorId(authorId);
        model.addAttribute("authors", authors);
        model.addAttribute("allGenres", allGenres);
        model.addAttribute("newBook", newBook);
        return "bookPages/newBook";
    }

    @PostMapping("book")
    public String save(@ModelAttribute("newBook") final BookEditDto book) {
        bookService.create(book);
        return "redirect:/book";
    }

    @PatchMapping("book")
    public String update(@ModelAttribute("book") final BookEditDto book) {
        bookService.update(book);
        return "redirect:/book/%s".formatted(book.getId());
    }

    @DeleteMapping("book/{id}")
    public String delete(@PathVariable long id) {
        bookService.deleteById(id);
        return "redirect:/author";
    }
}
