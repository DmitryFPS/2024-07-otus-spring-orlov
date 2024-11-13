package ru.otus.hw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books")
public class Book {
    @Id
    private String id;

    private String title;

    @DBRef(lazy = true)
    private Author author;

    @DBRef(lazy = true)
    private List<Genre> genres;
}
