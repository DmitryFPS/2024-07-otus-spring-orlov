package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.GenreServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreServiceImpl genreService;

    @Test
    void testFindAll() throws Exception {
        final List<GenreDto> genres = Arrays.asList(
                new GenreDto(1L, "genre_1"),
                new GenreDto(2L, "genre_2")
        );

        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("genre_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("genre_2"));
    }

    @Test
    void testFindById() throws Exception {
        final GenreDto genre = new GenreDto(1L, "genre_1");

        when(genreService.findById(1L)).thenReturn(genre);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("genre_1"));
    }

    @Test
    void testFindByIdNotFoundException() throws Exception {
        when(genreService.findById(777L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre/{id}", 777L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByIdInternalServerError() throws Exception {
        when(genreService.findById(Mockito.any())).thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genre/{id}", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
