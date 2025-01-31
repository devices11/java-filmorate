
--changeset yuri:create-user_review_reactions
CREATE TABLE IF NOT EXISTS filmorate.user_review_reactions (
                                                               user_review_reaction_id integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                               user_id integer NOT NULL,
                                                               review_id integer NOT NULL,
                                                               is_positive boolean DEFAULT false
);

COMMENT ON TABLE filmorate.user_review_reactions IS 'Реакции пользователей на отзывы';
COMMENT ON COLUMN filmorate.user_review_reactions.user_review_reaction_id IS 'Идентификатор реакции';
COMMENT ON COLUMN filmorate.user_review_reactions.user_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN filmorate.user_review_reactions.review_id IS 'Идентификатор отзыва';
COMMENT ON COLUMN filmorate.user_review_reactions.is_positive IS 'Тип реакции (позитивная или негативная)';

--rollback DROP TABLE filmorate.user_review_reactions;