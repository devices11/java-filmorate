--liquibase formatted sql

--changeset devices11:create-friend
CREATE TABLE IF NOT EXISTS filmorate.friend (
                               user_id integer NOT NULL,
                               friend_id integer NOT NULL,
                               confirmed boolean DEFAULT false,
                               CONSTRAINT friend_unique UNIQUE (friend_id, user_id)
);

comment on table filmorate.friend is 'Друзья';
comment on column filmorate.friend.user_id is 'Идентификатор пользователя';
comment on column filmorate.friend.friend_id is 'Идентификатор друга пользователя';
comment on column filmorate.friend.confirmed is 'Флаг подтверждения дружбы';
--rollback DROP TABLE filmorate.friend;


--changeset devices11:create-friend-fk
ALTER TABLE filmorate.friend ADD CONSTRAINT friend_id_user_id_fk FOREIGN KEY (friend_id) REFERENCES filmorate."user"(user_id);
ALTER TABLE filmorate.friend ADD CONSTRAINT friend_user_fk FOREIGN KEY (user_id) REFERENCES filmorate."user"(user_id);
--rollback drop table filmorate.friend;

--changeset devices11:create-friend_user_id_idx-index
CREATE INDEX friend_user_id_idx ON filmorate.friend (user_id,friend_id);
--rollback DROP INDEX filmorate.friend_user_id_idx;