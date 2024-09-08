--liquibase formatted sql

--changeset orlov:2024-08-31--0001-genres
CREATE TABLE IF NOT EXISTS genres
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
)
