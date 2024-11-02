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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;


    @Test
    void testFindAll() throws Exception {
        final List<AuthorDto> authors = List.of(new AuthorDto(1L, "author_1"));
        when(authorService.findAll()).thenReturn(authors);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value("author_1"));
    }

    @Test
    void testFindById() throws Exception {
        final AuthorDto author = new AuthorDto(1L, "author_1");
        when(authorService.findById(anyLong())).thenReturn(author);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("author_1"));
    }

    @Test
    void testFindByIdNotFoundException() throws Exception {
        when(authorService.findById(777L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author/{id}", 777L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByIdInternalServerError() throws Exception {
        when(authorService.findById(Mockito.any())).thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/author/{id}", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
