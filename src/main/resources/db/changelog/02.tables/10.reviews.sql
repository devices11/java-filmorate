--liquibase formatted sql

--changeset yuri:create-reviews
CREATE TABLE IF NOT EXISTS filmorate.reviews (
      review_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
      content VARCHAR(500) NOT NULL,
      film_id INTEGER NOT NULL,
      user_id INTEGER NOT NULL,
      is_positive BOOLEAN DEFAULT false,
      useful INTEGER NOT NULL,
      CONSTRAINT fk_reviews_film FOREIGN KEY (film_id) REFERENCES filmorate.films (film_id) ON DELETE CASCADE,
      CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES filmorate.users (user_id)
);

COMMENT ON TABLE filmorate.reviews IS 'Отзывы пользователей на фильмы';
COMMENT ON COLUMN filmorate.reviews.review_id IS 'Идентификатор отзыва';
COMMENT ON COLUMN filmorate.reviews.content IS 'Текст отзыва';
COMMENT ON COLUMN filmorate.reviews.film_id IS 'Идентификатор фильма';
COMMENT ON COLUMN filmorate.reviews.user_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN filmorate.reviews.is_positive IS 'Тип отзыва (позитивный или негативный)';
COMMENT ON COLUMN filmorate.reviews.useful IS 'Сумма позитивных(+1) и негативных(-1) реакций на отзыв';

--rollback DROP TABLE filmorate.reviews;