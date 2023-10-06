package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public class AppTest {
    Javalin app;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testRootPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты на SEO пригодность");
        }));
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        }));
    }

    @Test
    public void testCreateValidUrlWithoutPortWithoutPath() {
        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=https://www.examplename.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.examplename.com");
        }));
    }

    @Test
    public void testCreateValidUrlWithPortWithPath() {
        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=https://some-domain.org:8080/example/path";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://some-domain.org:8080");
        }));
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url("https://www.github.com");
        UrlsRepository.save(url);
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        }));
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        }));
    }

    @Test
    public void testAddUrlCheck() throws Exception {
        var url = new Url("https://mail.ru");
        UrlsRepository.save(url);
        JavalinTest.test(app, ((server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Mail.ru: почта, поиск в интернете, новости, игры");
        }));
    }
}
