--liquibase formatted sql

--changeset orlov:2024-09-10--0003-genres
CREATE TABLE IF NOT EXISTS genres
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
)
