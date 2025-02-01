--liquibase formatted sql

--changeset yuri:create-reviews_dislikes
CREATE TABLE IF NOT EXISTS filmorate.reviews_dislikes (
                                                          reviews_dislike_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                          user_id INTEGER NOT NULL,
                                                          review_id INTEGER NOT NULL
);

COMMENT ON TABLE filmorate.reviews_dislikes IS 'Негативные реакции пользователей на отзывы';
COMMENT ON COLUMN filmorate.reviews_dislikes.reviews_dislike_id IS 'Идентификатор реакции';
COMMENT ON COLUMN filmorate.reviews_dislikes.user_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN filmorate.reviews_dislikes.review_id IS 'Идентификатор отзыва';

--rollback DROP TABLE filmorate.reviews_dislikes;