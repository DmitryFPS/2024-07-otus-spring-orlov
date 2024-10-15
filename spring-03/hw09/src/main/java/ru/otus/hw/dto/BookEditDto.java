package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEditDto {
    private long id;

    private String title;

    private long authorId;

    private Set<Long> genresIds;
}
