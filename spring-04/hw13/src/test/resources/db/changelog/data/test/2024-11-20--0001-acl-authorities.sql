--liquibase formatted sql

--changeset orlov:2024-11-20--0001-acl-authorities
insert into acl_sid(principal, sid)
values (true, 'user'), -- 1
       (true, 'admin'); -- 2

INSERT INTO acl_class (class)
VALUES ('ru.otus.hw.dto.BookUpdateDto'); -- 1

insert into acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
values (1, 1, null, 2, false), -- 1 : BookUpdateDto : 1 Экземпляр : нет родителя : admin : без наследования
       (1, 2, null, 2, false), -- 2 : BookUpdateDto : 2 Экземпляр : нет родителя : admin : без наследования
       (1, 3, null, 2, false); -- 3 : BookUpdateDto : 3 Экземпляр : нет родителя : admin : без наследования

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 2, 2, 1, 1, 1), -- 1 : BookUpdateDto : admin : WRITE
       (2, 1, 2, 2, 1, 1, 1), -- 2 : BookUpdateDto : admin : WRITE
       (3, 1, 2, 2, 1, 1, 1); -- 3 : BookUpdateDto : admin : WRITE
