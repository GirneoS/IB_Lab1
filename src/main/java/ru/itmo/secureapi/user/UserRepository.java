package ru.itmo.secureapi.user;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "JdbcTemplate is a thread-safe Spring singleton")
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AppUser> findByUsername(String username) {
        return jdbcTemplate.query(
                        "SELECT id, username, password_hash FROM users WHERE username = ?",
                        (resultSet, rowNumber) -> new AppUser(
                                resultSet.getLong("id"),
                                resultSet.getString("username"),
                                resultSet.getString("password_hash")),
                        username)
                .stream()
                .findFirst();
    }

    public long create(String username, String passwordHash) {
        Long key = jdbcTemplate.queryForObject(
                "INSERT INTO users(username, password_hash) VALUES (?, ?) RETURNING id",
                Long.class,
                username,
                passwordHash);
        if (key == null) {
            throw new IllegalStateException("Database did not return the new user identifier");
        }
        return key;
    }
}
