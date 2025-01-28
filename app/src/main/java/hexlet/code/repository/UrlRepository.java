package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sqlStatement = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, url.getName());
            LocalDateTime createdAt = LocalDateTime.now();
            preparedStatement.setTimestamp(2, Timestamp.valueOf(createdAt));
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
                url.setCreatedAt(Timestamp.valueOf(createdAt));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Url> search(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name LIKE ?";
        try (Connection conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            var resultSet = preparedStatement.executeQuery();
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

    public static Optional<Url> find(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                Url url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT * FROM urls";
        try (var conn = dataSource.getConnection();
             var prepareStatement = conn.prepareStatement(sql)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            ArrayList<Url> result = new ArrayList<Url>();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                Url url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                result.add(url);
            }
            return result;
        }
    }
}
