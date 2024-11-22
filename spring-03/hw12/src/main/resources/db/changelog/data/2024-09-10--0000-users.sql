--changeset orlov:2024-09-10--0000-users
insert into users (username, password, is_active, authority)
values ('admin', '$2a$12$KkBv4hhhf.nsY8YWPTE4ReKf9Fp5VBoD.vsliuy6jrl359GFEQ.dK', true, 'ADMIN'),
       ('user', '$2a$12$FqC93ciX/NjxGjcVXsAq2.duiN.ae2/fGH.9sJSXygc9xGB7j5N7C', true, 'USER')
