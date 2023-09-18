package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.RootController;
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
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "production");
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    private static HikariConfig getHikariConfig() {
        var hikariConfig = new HikariConfig();

        if (isProduction()) {
            hikariConfig.setJdbcUrl("jdbc:postgresql://dpg-cj5pddqcn0vc73f895cg-a:5432/example_base");
            hikariConfig.setUsername("example_base_user");
            hikariConfig.setPassword("ZxYV34oq7oO4tBGfIoTz6cOmIBDUwRhg");
            return hikariConfig;
        }
        hikariConfig.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        return hikariConfig;
    }

    public static Javalin getApp() throws IOException, SQLException {
        var dataSource = new HikariDataSource(getHikariConfig());
        String sql;
        try {
            var url = App.class.getClassLoader().getResource("schema.sql");
            var file = new File(url.getFile());
            sql = Files.lines(file.toPath())
                    .collect(Collectors.joining("\n"));
        } catch (NoSuchFileException e) {
            sql = """
                    DROP TABLE IF EXISTS urls;
                    CREATE TABLE urls
                    (
                        id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        name VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP NOT NULL
                    );
                    """;
        }
        log.info(sql);
        try(var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        JavalinJte.init(createTemplateEngine());

        app.get(NamedRoutes.rootPath(), RootController::index);
        app.get(NamedRoutes.buildUrlPath(), UrlsController::build);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);

        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }
}
