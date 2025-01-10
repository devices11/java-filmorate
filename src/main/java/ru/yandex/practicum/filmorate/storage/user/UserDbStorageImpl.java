package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorageImpl extends BaseStorage<User> implements UserDbStorage {
    private final UserRowMapper userRowMapper;
    private final FriendRowMapper friendRowMapper;

    private static final String FIND_BY_ID_QUERY = """
            SELECT * FROM filmorate."user" WHERE user_id = ?
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT * FROM filmorate."user"
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO filmorate."user"(login, name, email, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE filmorate."user"
            SET login = ?, name = ?, email = ?, birthday = ? WHERE user_id = ?
            """;
    private static final String INSERT_FRIEND_QUERY = """
            INSERT INTO filmorate.friend (confirmed, user_id, friend_id)
            VALUES(?, ?, ?)
            """;
    private static final String FIND_ALL_FRIENDS_QUERY = """
            SELECT u.*, f.CONFIRMED FROM FILMORATE."user" u
            JOIN FILMORATE.FRIEND f ON u.USER_ID = f.FRIEND_ID
            WHERE f.USER_ID = ?
            """;
    private static final String UPDATE_FRIENDS_QUERY = """
            UPDATE FILMORATE.FRIEND SET CONFIRMED = ?
            WHERE USER_ID = ? AND FRIEND_ID = ?
            """;
    private static final String DELETE_FRIEND_QUERY = """
            DELETE FROM FILMORATE.FRIEND WHERE USER_ID = ? AND FRIEND_ID = ?
            """;

    public UserDbStorageImpl(JdbcTemplate jdbc, UserRowMapper userRowMapper, FriendRowMapper friendRowMapper) {
        super(jdbc);
        this.userRowMapper = userRowMapper;
        this.friendRowMapper = friendRowMapper;
    }

    @Override
    public Optional<User> findById(long id) {
        return findOne(userRowMapper, FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(userRowMapper, FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        long id = insert(INSERT_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public void addFriend(boolean confirmed, long userId, long friendId) {
        update(INSERT_FRIEND_QUERY, confirmed, userId, friendId);
    }

    public void updateFriend(boolean confirmed, long userId, long friendId) {
        update(UPDATE_FRIENDS_QUERY, confirmed, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    public List<User> getFriend(long userId) {
        return findMany(friendRowMapper, FIND_ALL_FRIENDS_QUERY, userId);
    }

}
