package hexlet.code.repository;

import hexlet.code.dto.urls.UrlChecksData;
import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class UrlChecksRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = """
                INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        var datetime = new Timestamp(System.currentTimeMillis());

        try (var conn = dataSource.getConnection();
                var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, urlCheck.getUrlId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getTitle());
            preparedStatement.setString(5, urlCheck.getDescription());
            preparedStatement.setTimestamp(6, datetime);
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(datetime);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static UrlChecksData getEntities(Long urlId) throws SQLException {
        String sql = String.format("""
            SELECT * FROM url_checks WHERE url_id = %d ORDER BY created_at DESC
            """, urlId);
        var urlChecks = new ArrayList<UrlCheck>();

        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
                urlCheck.setId(id);
                urlCheck.setCreatedAt(createdAt);
                urlChecks.add(urlCheck);
            }
        }

        return new UrlChecksData(urlChecks);
    }

    public static Map<String, Object> findLastCheck(Long urlId, String name) throws SQLException {
        String sql = String.format("""
            SELECT status_code, MAX(created_at) AS created_at
            FROM url_checks GROUP BY url_id, status_code
            HAVING url_id = %d
            """, urlId);

        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                var statusCode = resultSet.getInt("status_code");
                var createdAt = resultSet.getTimestamp("created_at");

                return Map.of("id", urlId, "name", name,
                        "statusCode", statusCode, "createdAt", createdAt);
            }

            return Map.of("id", urlId, "name", name);
        }
    }
}
