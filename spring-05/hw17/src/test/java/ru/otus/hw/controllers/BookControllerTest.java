package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.*;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.BookMapperImpl;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({BookController.class, BookMapperImpl.class, ExceptionHandlerController.class})
class BookControllerTest {

    @Autowired
    private MockMvc mvc;


    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;


    private List<AuthorDto> authors;

    private List<GenreDto> genres;

    private List<BookDto> books;


    @BeforeEach
    void setUp() {
        authors = createAuthors();
        genres = createGenres();
        books = createBooks();
    }


    @Test
    void testShowFormForUpdatingBook() throws Exception {
        final BookUpdateDto bookUpdateDto = new BookUpdateDto(1L, "title", 1L, Set.of(1L, 2L));

        when(bookService.getBookUpdateDtoById(bookUpdateDto.getId())).thenReturn(bookUpdateDto);
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/{id}", bookUpdateDto.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("updateBook", bookUpdateDto))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres))
                .andExpect(view().name("bookPages/updateBook"));
    }

    @Test
    void testNotFoundException() throws Exception {
        final ErrorDto error = new ErrorDto();
        error.setStatusCode(HttpStatus.NOT_FOUND);

        when(bookService.getBookUpdateDtoById(Mockito.anyLong())).thenThrow(NotFoundException.class);
        mvc.perform(get("/book/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(model().attribute("error", error))
                .andExpect(view().name("errorPages/error"));
    }

    @Test
    void testInternalException() throws Exception {
        final ErrorDto error = new ErrorDto();
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        error.setMessage("Failed to convert value of type 'java.lang.String' to required type 'long'; For input string: \"test\"");

        mvc.perform(get("/book/{id}", "test"))
                .andExpect(status().is5xxServerError())
                .andExpect(model().attribute("error", error))
                .andExpect(view().name("errorPages/error"));
    }

    @Test
    void testFindBooks() throws Exception {
        when(bookService.findAll()).thenReturn(books);

        mvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", books))
                .andExpect(view().name("bookPages/books"));
    }

    @Test
    void testCreateBookForm() throws Exception {
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mvc.perform(get("/book/form"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("createBook", new BookCreateDto()))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres))
                .andExpect(view().name("bookPages/createBook"));
    }

    @Test
    void testCreateBook() throws Exception {
        final BookCreateDto book = new BookCreateDto("NewBook", 2L, Set.of(1L, 3L));

        mvc.perform(post("/book").flashAttr("createBook", book))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).create(book);
    }

    @Test
    void testUpdateBook() throws Exception {
        final BookUpdateDto book = new BookUpdateDto(0L, "UpdateBook", 2L, Set.of(1L, 3L));

        mvc.perform(patch("/book").flashAttr("updateBook", book))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).update(book);
    }

    @Test
    void testDeleteBook() throws Exception {
        mvc.perform(delete("/book/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).deleteById(1L);
    }


    private List<AuthorDto> createAuthors() {
        final AuthorDto author1 = new AuthorDto(1L, "Author_1");
        final AuthorDto author2 = new AuthorDto(2L, "Author_2");
        final AuthorDto author3 = new AuthorDto(3L, "Author_3");
        return List.of(author1, author2, author3);
    }

    private List<GenreDto> createGenres() {
        final GenreDto genre1 = new GenreDto(1L, "Genre_1");
        final GenreDto genre2 = new GenreDto(2L, "Genre_2");
        final GenreDto genre3 = new GenreDto(3L, "Genre_3");
        final GenreDto genre4 = new GenreDto(4L, "Genre_4");
        final GenreDto genre5 = new GenreDto(5L, "Genre_5");
        final GenreDto genre6 = new GenreDto(6L, "Genre_6");
        return List.of(genre1, genre2, genre3, genre4, genre5, genre6);
    }

    private List<BookDto> createBooks() {
        final BookDto book1 = new BookDto(1L, "BookTitle_1", createAuthors().get(0),
                List.of(createGenres().get(0), createGenres().get(1)));
        final BookDto book2 = new BookDto(2L, "BookTitle_2", createAuthors().get(1),
                List.of(createGenres().get(2), createGenres().get(3)));
        final BookDto book3 = new BookDto(3L, "BookTitle_3", createAuthors().get(2),
                List.of(createGenres().get(4), createGenres().get(5)));
        return List.of(book1, book2, book3);
    }
}
