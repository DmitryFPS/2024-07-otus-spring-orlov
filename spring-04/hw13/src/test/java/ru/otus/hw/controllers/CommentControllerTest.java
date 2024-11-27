package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.mapper.CommentMapperImpl;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = CommentController.class)
@Import({CommentMapperImpl.class, CommentServiceImpl.class})
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private CommentRepository commentRepository;


    @Test
    void testFindComments() throws Exception {
        mockMvc.perform(get("/comment/{id}", 1L).with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("commentPages/comments"));
    }

    @Test
    void testFindCommentsUnauthorized() throws Exception {
        mockMvc.perform(get("/comment/{id}", 1L).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateCommentForm() throws Exception {
        mockMvc.perform(get("/comment/form/{id}", 1L).with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("commentPages/createComment"));
    }

    @Test
    void testCreateCommentFormUnauthorized() throws Exception {
        mockMvc.perform(get("/comment/form/{id}", 1L).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateComment() throws Exception {
        final Optional<Book> optionalBook = Optional.of(new Book(1L, "title", new Author(), List.of()));
        Mockito.when(bookRepository.findById(1L)).thenReturn(optionalBook);

        mockMvc.perform(post("/comment/{id}", 1L).flashAttr("text", "content").with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comment/1"));
    }

    @Test
    void testCreateCommentUnauthorized() throws Exception {
        final Optional<Book> optionalBook = Optional.of(new Book(1L, "title", new Author(), List.of()));
        Mockito.when(bookRepository.findById(1L)).thenReturn(optionalBook);

        mockMvc.perform(post("/comment/{id}", 1L)
                        .flashAttr("text", "content").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateCommentForbidden() throws Exception {
        final Optional<Book> optionalBook = Optional.of(new Book(1L, "title", new Author(), List.of()));
        Mockito.when(bookRepository.findById(1L)).thenReturn(optionalBook);

        mockMvc.perform(post("/comment/{id}", 1L).flashAttr("text", "content")
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteComment() throws Exception {
        final Book book = new Book(1L, "title", new Author(), List.of());
        final Optional<Comment> optionalComment = Optional.of(new Comment(1L, "title", book));
        Mockito.when(commentRepository.findById(1L)).thenReturn(optionalComment);

        mockMvc.perform(delete("/comment/{id}", 1L).param("bookId", "1").with(csrf())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comment/1"));
    }

    @Test
    void testDeleteCommentUnauthorized() throws Exception {
        final Book book = new Book(1L, "title", new Author(), List.of());
        final Optional<Comment> optionalComment = Optional.of(new Comment(1L, "title", book));
        Mockito.when(commentRepository.findById(1L)).thenReturn(optionalComment);

        mockMvc.perform(delete("/comment/{id}", 1L)
                        .param("bookId", "1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteCommentForbidden() throws Exception {
        final Book book = new Book(1L, "title", new Author(), List.of());
        final Optional<Comment> optionalComment = Optional.of(new Comment(1L, "title", book));
        Mockito.when(commentRepository.findById(1L)).thenReturn(optionalComment);

        mockMvc.perform(delete("/comment/{id}", 1L).param("bookId", "1")
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }
}
