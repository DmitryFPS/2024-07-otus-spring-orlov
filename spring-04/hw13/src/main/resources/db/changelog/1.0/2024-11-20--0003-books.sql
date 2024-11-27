--liquibase formatted sql

--changeset orlov:2024-11-20--0003-books
CREATE TABLE IF NOT EXISTS books
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    title     VARCHAR(255),
    author_id BIGINT,
    FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE
);
