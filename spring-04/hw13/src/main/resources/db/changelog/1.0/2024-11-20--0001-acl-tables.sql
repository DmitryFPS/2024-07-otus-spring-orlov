--liquibase formatted sql

--changeset orlov:2024-11-20--0001-acl-tables
CREATE TABLE IF NOT EXISTS acl_sid
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    principal tinyint      NOT NULL,
    sid       varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS acl_class
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    class varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS acl_entry
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    acl_object_identity bigint  NOT NULL,
    ace_order           int     NOT NULL,
    sid                 bigint  NOT NULL,
    mask                int     NOT NULL,
    granting            tinyint NOT NULL,
    audit_success       tinyint NOT NULL,
    audit_failure       tinyint NOT NULL
);

CREATE TABLE IF NOT EXISTS acl_object_identity
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    object_id_class    bigint  NOT NULL,
    object_id_identity bigint  NOT NULL,
    parent_object      bigint DEFAULT NULL,
    owner_sid          bigint DEFAULT NULL,
    entries_inheriting tinyint NOT NULL
);

ALTER TABLE acl_entry
    ADD FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id);

ALTER TABLE acl_entry
    ADD FOREIGN KEY (sid) REFERENCES acl_sid (id);

--
-- Constraints for table acl_object_identity
--
ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id);

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (object_id_class) REFERENCES acl_class (id);

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (owner_sid) REFERENCES acl_sid (id);
