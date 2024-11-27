package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

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
    public String createBook(@Valid @ModelAttribute("createBook") final BookCreateDto book,
                             final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throwValidationException(bindingResult);
        }

        bookService.create(book);
        return "redirect:/";
    }

    @PatchMapping("/book")
    public String updateBook(@Valid @ModelAttribute("updateBook") final BookUpdateDto book,
                             final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throwValidationException(bindingResult);
        }

        bookService.update(book);
        return "redirect:/";
    }

    @DeleteMapping("/book/{id}")
    public String deleteBook(@PathVariable("id") final long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    private void throwValidationException(@NotNull final BindingResult bindingResult) {
        final StringBuilder message = new StringBuilder("Валидация не пройдена: ");
        final List<FieldError> errors = bindingResult.getFieldErrors();
        for (final FieldError error : errors) {
            message.append(error.getDefaultMessage()).append("; ");
        }
        throw new ValidationException(message.toString());
    }
}
