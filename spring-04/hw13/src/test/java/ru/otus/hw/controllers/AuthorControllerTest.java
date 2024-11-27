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
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.services.AuthorServiceImpl;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(controllers = AuthorController.class)
@Import({AuthorMapperImpl.class, AuthorServiceImpl.class})
class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorRepository authorRepository;


    @Test
    void testFindAuthors() throws Exception {
        mockMvc.perform(get("/author")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("authorPages/authors"));
    }

    @Test
    void testAuthorById() throws Exception {
        final Optional<Author> optionalAuthor = Optional.of(new Author(1L, "name"));
        Mockito.when(authorRepository.findById(1L)).thenReturn(optionalAuthor);

        mockMvc.perform(get("/author/{id}", 1L)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("authorPages/author"));
    }

    @Test
    void testFindAuthorsUnauthorized() throws Exception {
        mockMvc.perform(get("/author"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthorByIdUnauthorized() throws Exception {
        mockMvc.perform(get("/author/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
