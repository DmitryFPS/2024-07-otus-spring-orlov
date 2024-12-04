package ru.otus.hw.dto.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {
    private String id;

    private String commentText;

    private String bookId;
}
