package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.mapper.BookMapperImpl;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.AuthorServiceImpl;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BookController.class)
@Import({BookMapperImpl.class, AuthorMapperImpl.class, GenreMapperImpl.class,
        BookServiceImpl.class, AuthorServiceImpl.class, GenreServiceImpl.class})
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;


    @Test
    void testFindBooks() throws Exception {
        mockMvc.perform(get("/book").with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("bookPages/books"));
    }

    @Test
    void testFindBooksUnauthorized() throws Exception {
        mockMvc.perform(get("/book").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testShowFormForUpdatingBook() throws Exception {
        final Optional<Book> optionalBook = Optional.of(new Book(1L, "title", new Author(), List.of()));
        Mockito.when(bookRepository.findById(1L)).thenReturn(optionalBook);

        mockMvc.perform(get("/book/1").with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("bookPages/updateBook"));
    }

    @Test
    void testShowFormForUpdatingBookUnauthorized() throws Exception {
        mockMvc.perform(get("/book/{id}", 1L).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateBookForm() throws Exception {
        mockMvc.perform(get("/book/form").with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(view().name("bookPages/createBook"));
    }

    @Test
    void testCreateBookFormUnauthorized() throws Exception {
        mockMvc.perform(get("/book/form").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testCreateBook() throws Exception {
        final Optional<Author> optionalAuthor = Optional.of(new Author(1L, "name"));
        Mockito.when(authorRepository.findById(1L)).thenReturn(optionalAuthor);
        final Genre genre = new Genre(1L, "genre");
        Mockito.when(genreRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(genre));
        final BookCreateDto createDto = new BookCreateDto("title", 1L, Set.of(1L));

        mockMvc.perform(post("/book").flashAttr("createBook", createDto).with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testCreateBookNotValidRequest() throws Exception {
        final Optional<Author> optionalAuthor = Optional.of(new Author(1L, "name"));
        Mockito.when(authorRepository.findById(1L)).thenReturn(optionalAuthor);
        final Genre genre = new Genre(1L, "genre");
        Mockito.when(genreRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(genre));
        final BookCreateDto createDto = new BookCreateDto("1", 1L, Set.of(1L));

        mockMvc.perform(post("/book").flashAttr("createBook", createDto).with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBookForbidden() throws Exception {
        final BookCreateDto createDto = new BookCreateDto("title", 1L, Set.of(1L));

        mockMvc.perform(post("/book").flashAttr("createBook", createDto)
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateBookUnauthorized() throws Exception {
        final BookCreateDto createDto = new BookCreateDto("title", 1L, Set.of(1L));

        mockMvc.perform(post("/book").flashAttr("createBook", createDto).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdateBook() throws Exception {
        final Optional<Book> optionalBook = Optional.of(new Book(1L, "titleNew", new Author(), List.of()));
        Mockito.when(bookRepository.findById(1L)).thenReturn(optionalBook);
        final Optional<Author> optionalAuthor = Optional.of(new Author(1L, "name"));
        Mockito.when(authorRepository.findById(1L)).thenReturn(optionalAuthor);
        final Genre genre = new Genre(1L, "genre");
        Mockito.when(genreRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(genre));
        final BookUpdateDto updateDto = new BookUpdateDto(1L, "titleNew", 1L, Set.of(1L));

        mockMvc.perform(patch("/book").flashAttr("updateBook", updateDto).with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdateBookNotValidRequest() throws Exception {
        final Optional<Book> optionalBook = Optional.of(new Book(1L, "1", new Author(), List.of()));
        Mockito.when(bookRepository.findById(1L)).thenReturn(optionalBook);
        final Optional<Author> optionalAuthor = Optional.of(new Author(1L, "name"));
        Mockito.when(authorRepository.findById(1L)).thenReturn(optionalAuthor);
        final Genre genre = new Genre(1L, "genre");
        Mockito.when(genreRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(genre));
        final BookUpdateDto updateDto = new BookUpdateDto(1L, "1", 1L, Set.of(1L));

        mockMvc.perform(patch("/book").flashAttr("updateBook", updateDto).with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateBookForbidden() throws Exception {
        final BookUpdateDto updateDto = new BookUpdateDto(1L, "titleNew", 1L, Set.of(1L));

        mockMvc.perform(patch("/book").flashAttr("updateBook", updateDto)
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateBookUnauthorized() throws Exception {
        final BookUpdateDto updateDto = new BookUpdateDto(1L, "titleNew", 1L, Set.of(1L));

        mockMvc.perform(patch("/book").flashAttr("updateBook", updateDto).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDeleteBook() throws Exception {

        mockMvc.perform(delete("/book/{id}", 1L).with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testDeleteBookForbidden() throws Exception {
        mockMvc.perform(delete("/book/1")
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBookUnauthorized() throws Exception {
        mockMvc.perform(delete("/book/{id}", 1L).with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
