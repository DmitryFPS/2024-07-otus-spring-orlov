--liquibase formatted sql

--changeset orlov:2024-09-10--0000-users
CREATE TABLE IF NOT EXISTS users
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(15)  NOT NULL unique,
    password  VARCHAR(512) NOT NULL,
    is_active boolean default false,
    authority VARCHAR(30)  NOT NULL
);
