package hexlet.code.repository;

import hexlet.code.dto.urls.UrlsData;
import hexlet.code.model.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;

public class UrlsRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        var datetime = new Timestamp(System.currentTimeMillis());

        try (var conn = dataSource.getConnection();
                var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, datetime);
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
                url.setCreatedAt(datetime);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";

        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);

                return Optional.of(url);
            }

            return Optional.empty();
        }
    }

    public static Optional<Url> find(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);

                return Optional.of(url);
            }

            return Optional.empty();
        }
    }

    public static boolean existsByName(String name) throws SQLException {
        return find(name).isPresent();
    }

    public static UrlsData findEntities(int page, int rowsPerPage) throws SQLException {
        var offset = page * rowsPerPage;
        var sql = String.format("""
            SELECT *, COUNT(*) OVER () AS TotalCount FROM urls
            ORDER BY id LIMIT %d OFFSET %d
            """, rowsPerPage, offset);
        var urls = new ArrayList<Url>();
        var totalCount = 0;

        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                urls.add(url);
                totalCount = resultSet.getInt("TotalCount");
            }

        }

        return new UrlsData(urls, totalCount);

    }
}
