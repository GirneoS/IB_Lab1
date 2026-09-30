package ru.itmo.secureapi.note;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NoteRepository {

    private static final DateTimeFormatter SQLITE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final JdbcTemplate jdbcTemplate;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "JdbcTemplate is a thread-safe Spring singleton")
    public NoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Note> findAllForUser(String username) {
        return jdbcTemplate.query("""
                        SELECT n.id, n.title, n.content, n.created_at
                        FROM notes n
                        JOIN users u ON u.id = n.user_id
                        WHERE u.username = ?
                        ORDER BY n.id
                        """,
                (resultSet, rowNumber) -> new Note(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        LocalDateTime.parse(resultSet.getString("created_at"), SQLITE_TIMESTAMP)),
                username);
    }

    public Note createForUser(String username, String title, String content) {
        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO notes(user_id, title, content)
                        SELECT id, ?, ? FROM users WHERE username = ?
                        RETURNING id, title, content, created_at
                        """,
                (resultSet, rowNumber) -> new Note(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        LocalDateTime.parse(resultSet.getString("created_at"), SQLITE_TIMESTAMP)),
                title,
                content,
                username);
    }
}
