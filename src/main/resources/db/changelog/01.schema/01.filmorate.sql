--liquibase formatted sql

--changeset devices11:schema-filmorate-initialization
create schema if not exists filmorate;
--rollback --not supported

--changeset devices11:schema-filmorate_liquibase-initialization
create schema if not exists filmorate_liquibase;
--rollback --not supported