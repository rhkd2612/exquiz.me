insert into OAUTH_USER (username, password, role, nickname, email, activated) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'ROLE_ADMIN',  'admin', 'asdasd@naver.com', 1);
insert into OAUTH_USER (username, password, role, nickname, email, activated) values ('user', '$2a$08$asdasdau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'ROLE_USER',  'user', 'user@naver.com', 1);

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');