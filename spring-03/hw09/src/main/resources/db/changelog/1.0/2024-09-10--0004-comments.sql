--liquibase formatted sql

--changeset orlov:2024-09-10--0004-comments
CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_text VARCHAR(255),
    book_id      BIGINT,
    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
);
