--liquibase formatted sql

--changeset orlov:2024-09-10--0002-books
CREATE TABLE IF NOT EXISTS books
(
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(255),
    author_id BIGINT,
    FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE
);
