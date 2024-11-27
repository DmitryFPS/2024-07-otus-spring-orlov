--liquibase formatted sql

--changeset orlov:2024-11-20--0004-genres
CREATE TABLE IF NOT EXISTS genres
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
)
