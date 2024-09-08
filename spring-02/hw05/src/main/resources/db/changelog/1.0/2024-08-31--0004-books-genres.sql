--liquibase formatted sql

--changeset orlov:2024-08-31--0001-books-genres
CREATE TABLE IF NOT EXISTS books_genres
(
    book_id  BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (book_id, genre_id),
    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);
