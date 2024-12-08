package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateDto {
    private Long id;

    private String title;

    private Long authorId;

    private Set<Long> genreIds;
}
