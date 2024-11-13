package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.GenreServiceImpl;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(controllers = GenreController.class)
@Import({GenreMapperImpl.class, GenreServiceImpl.class})
class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreRepository genreRepository;


    @Test
    void testFindGenres() throws Exception {
        mockMvc.perform(get("/genre")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("genrePages/genres"));
    }

    @Test
    void testFindGenresUnauthorized() throws Exception {
        mockMvc.perform(get("/genre"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testFindGenreById() throws Exception {
        final Optional<Genre> genre = Optional.of(new Genre(1L, "genre"));
        Mockito.when(genreRepository.findById(1L)).thenReturn(genre);

        mockMvc.perform(get("/genre/{id}", 1L)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(view().name("genrePages/genre"));
    }

    @Test
    void testFindGenreByIdUnauthorized() throws Exception {
        final Optional<Genre> genre = Optional.of(new Genre(1L, "genre"));
        Mockito.when(genreRepository.findById(1L)).thenReturn(genre);

        mockMvc.perform(get("/genre/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
