--liquibase formatted sql

--changeset orlov:2024-09-10--0001-authors
CREATE TABLE IF NOT EXISTS authors
(
    id        BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL
);
