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
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CommentController.class, ObjectMapper.class})
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;


    @Test
    void testFindAll() throws Exception {
        final List<CommentDto> comments = List.of(new CommentDto(1L, "comment_1"));
        when(commentService.findByBookId(anyLong())).thenReturn(comments);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/{id}", comments.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].commentText").value("comment_1"));
    }

    @Test
    void testCreate() throws Exception {
        final CommentCreateDto commentCreateDto = new CommentCreateDto(1L, "commentNew");
        final CommentDto commentDto = new CommentDto(1L, "commentNew");
        when(commentService.create(commentCreateDto)).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.commentText").value("commentNew"));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/comment/{id}", 1L))
                .andExpect(status().is(204));
        Mockito.verify(commentService).deleteById(1L);
    }

    @Test
    void testCreateNotFoundException() throws Exception {
        final CommentCreateDto commentCreateDto = new CommentCreateDto(777L, "title");
        final String jsonRequest = new ObjectMapper().writeValueAsString(commentCreateDto);

        when(commentService.create(commentCreateDto))
                .thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateInternalServerError() throws Exception {
        when(commentService.create(Mockito.any())).thenThrow(new NotFoundException("Internal Server Error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/comment")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
