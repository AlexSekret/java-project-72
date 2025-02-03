package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;


public class App {
    public static void main(String[] args) {
        try {
            Javalin app = getApp();
            app.start(getPort());
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Javalin getApp() throws IOException, SQLException {
        var dbConnectionUrl = getDBUrl();
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbConnectionUrl);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;
        var sql = readResourceFile("schema.sql");

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get(NamedRoutes.root(), RootController::index);
        app.get(NamedRoutes.urlsIndex(), UrlsController::index);
        app.post(NamedRoutes.urlsIndex(), UrlsController::addUrl);
        app.get(NamedRoutes.urlShow("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlCheck("{id}"), UrlsController::check);
        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    private static String getDBUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;");
    }

    private static String readResourceFile(String fileName) throws IOException {
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
