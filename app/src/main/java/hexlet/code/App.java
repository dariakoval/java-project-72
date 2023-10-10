package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlChecksController;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import lombok.extern.slf4j.Slf4j;
import io.javalin.Javalin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;

@Slf4j
public class App {
    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    public static Javalin getApp() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        var dataSource = new HikariDataSource(hikariConfig);
        String sql;
        try {
            if (System.getenv("JDBC_DATABASE_URL") == null) {
                var url = App.class.getClassLoader().getResource("schema_h2.sql");
                var file = new File(url.getFile());
                sql = Files.lines(file.toPath())
                        .collect(Collectors.joining("\n"));
            } else {
                var url = App.class.getClassLoader().getResource("schema_psql.sql");
                var file = new File(url.getFile());
                sql = Files.lines(file.toPath())
                        .collect(Collectors.joining("\n"));
            }
        } catch (NoSuchFileException e) {
            sql = """
                    DROP TABLE IF EXISTS urls CASCADE;
                    CREATE TABLE urls
                    (
                        id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        name VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP NOT NULL
                    );
                    DROP TABLE IF EXISTS url_checks;
                    CREATE TABLE url_checks
                    (
                        id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        url_id BIGINT REFERENCES urls (id) NOT NULL,
                        status_code INTEGER,
                        h1 VARCHAR(255),
                        title VARCHAR(255),
                        description TEXT,
                        created_at TIMESTAMP NOT NULL
                    );
                    """;
        }
        log.info(sql);
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            if (!isProduction()) {
                config.plugins.enableDevLogging();
            }
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        JavalinJte.init(createTemplateEngine());

        app.get(NamedRoutes.rootPath(), RootController::index);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.post(NamedRoutes.checksPath("{id}"), UrlChecksController::addCheck);

        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }
}
