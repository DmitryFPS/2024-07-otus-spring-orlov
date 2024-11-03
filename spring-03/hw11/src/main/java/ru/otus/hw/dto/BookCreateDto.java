package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateDto {

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 100, message = "Заголовок должен содержать от 3 до 100 символов")
    private String title;

    @NotNull(message = "Автор не может быть пустым")
    private String authorId;

    @NotNull(message = "Книга должна содержать хотя бы один жанр")
    private Set<String> genreIds;
}
