package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.BookServiceImpl;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookController.class, BookServiceImpl.class, ObjectMapper.class})
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookServiceImpl bookService;


    @Test
    void testFindAll() throws Exception {
        final List<BookDto> books = List.of(
                new BookDto(1L, "book_1", new AuthorDto(1L, "author_1"),
                        List.of(new GenreDto(1L, "genre_1")))
        );
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/book")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("book_1"));
    }

    @Test
    void testCreate() throws Exception {
        final BookCreateDto bookCreateDto = new BookCreateDto("BookNew", 1L, Set.of(1L, 2L));
        final BookDto bookDto = new BookDto(1L, "BookNew", new AuthorDto(1L, "author_1"),
                List.of(new GenreDto(1L, "genre_1"), new GenreDto(2L, "genre_2")));
        when(bookService.create(bookCreateDto)).thenReturn(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("BookNew"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.fullName").value("author_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres[0].name").value("genre_1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres[1].name").value("genre_2"));
    }

    @Test
    void testUpdate() throws Exception {
        final BookUpdateDto bookUpdateDto = new BookUpdateDto(1L, "Updated Book", 1L, Set.of(1L, 2L));
        final BookDto bookDto = new BookDto(1L, "Updated Book", new AuthorDto(1L, "Updated Author"),
                List.of(new GenreDto(1L, "Updated Genre")));
        when(bookService.update(bookUpdateDto)).thenReturn(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/book/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Book"));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/book/{id}", 1L))
                .andExpect(status().is(204));
        Mockito.verify(bookService).deleteById(1L);
    }

    @Test
    void testCreateNotFoundException() throws Exception {
        final BookCreateDto bookCreateDto = new BookCreateDto("title", 777L, Set.of(1L));
        final String jsonRequest = new ObjectMapper().writeValueAsString(bookCreateDto);

        when(bookService.create(bookCreateDto))
                .thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateInternalServerError() throws Exception {
        when(bookService.create(Mockito.any())).thenThrow(new NotFoundException("Internal Server Error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
