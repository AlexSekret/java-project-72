package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        String sqlStatement = "INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, urlCheck.getUrl().getId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getTitle());
            preparedStatement.setString(5, urlCheck.getDescription());
            LocalDateTime createdAt = LocalDateTime.now();
            preparedStatement.setTimestamp(6, Timestamp.valueOf(createdAt));
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(Timestamp.valueOf(createdAt));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> getEntities(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (var conn = dataSource.getConnection();
             var prepareStatement = conn.prepareStatement(sql)) {
            prepareStatement.setLong(1, urlId);
            ResultSet resultSet = prepareStatement.executeQuery();
            List<UrlCheck> result = new ArrayList<>();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String h1 = resultSet.getString("h1");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                UrlCheck urlCheck = new UrlCheck(h1, statusCode, title, description);
                urlCheck.setId(id);
                urlCheck.setCreatedAt(createdAt);
                result.add(urlCheck);
            }
            return result;
        }
    }

    public static Map<Long, UrlCheck> getLastChecks() throws SQLException {
        String sql = "SELECT DISTINCT ON (url_id) id, url_id,status_code, h1, title, description, created_at"
                + " FROM url_checks ORDER BY url_id, created_at DESC";
        Map<Long, UrlCheck> result = new HashMap<>();
        try (var conn = dataSource.getConnection();
             var prepareStatement = conn.prepareStatement(sql)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long urlId = resultSet.getLong("url_id");
                int statusCode = resultSet.getInt("status_code");
                String h1 = resultSet.getString("h1");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                UrlCheck urlCheck = new UrlCheck(h1, statusCode, title, description);
                urlCheck.setId(id);
                urlCheck.setCreatedAt(createdAt);
                result.put(urlId, urlCheck);
            }
        }
        return result;
    }
}
