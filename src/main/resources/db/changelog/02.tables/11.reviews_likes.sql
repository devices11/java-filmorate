--liquibase formatted sql

--changeset yuri:create-reviews_likes
CREATE TABLE IF NOT EXISTS filmorate.reviews_likes (
   reviews_like_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
   user_id INTEGER NOT NULL,
   review_id INTEGER NOT NULL,
   CONSTRAINT fk_reviews_likes_review FOREIGN KEY (review_id) REFERENCES filmorate.reviews (review_id) ON DELETE CASCADE,
   CONSTRAINT fk_reviews_likes_user FOREIGN KEY (user_id) REFERENCES filmorate.users (user_id)
);

COMMENT ON TABLE filmorate.reviews_likes IS 'Позитивные реакции пользователей на отзывы';
COMMENT ON COLUMN filmorate.reviews_likes.reviews_like_id IS 'Идентификатор реакции';
COMMENT ON COLUMN filmorate.reviews_likes.user_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN filmorate.reviews_likes.review_id IS 'Идентификатор отзыва';

--rollback DROP TABLE filmorate.reviews_likes;