package hexlet.code;


import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AppTest {
    public static Javalin app;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        app = App.getApp();
    }

    @AfterAll
    static void teardown() {
        // Остановка приложения после всех тестов
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            assertThat(client.get("/").code()).isEqualTo(200);
            assertThat(client.get("/").body().string())
                    .contains("<h1 class=\"display-3 mb-0\">Анализатор страниц</h1>");
        });
    }

    @Test
    void testAddCorrectUrl() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.post("/urls", "url=https://google.com");
            assertThat(response.code()).isEqualTo(200);
            assertEquals(true, UrlRepository.search("https://google.com").isPresent());
            assertThat(response.body().string()).contains("https://google.com");

            var response2 = client.post("/urls", "url=https://google.com:8080");
            assertEquals(true, UrlRepository.search("https://google.com:8080").isPresent());
            assertThat(response2.code()).isEqualTo(200);
            assertThat(response2.body().string()).contains("https://google.com:8080");
        });
    }

    @Test
    void testUrlControllerHandlers() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.post("/urls", "url=https://google.com");
            Url expected = new Url("https://google.com");
            Url url = UrlRepository.search("https://google.com").get();
            expected.setId(1L);
            expected.setCreatedAt(url.getCreatedAt());
            assertEquals(expected, url);
            Url url2 = UrlRepository.find(1L).get();
            assertEquals(expected, url2);
            Optional<Url> url3 = UrlRepository.find(10L);
            assertTrue(url3.isEmpty());
        });
    }

    @Test
    void testAddCorrectUrlTwice() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.post("/urls", "url=https://google.com");
            var response2 = client.post("/urls", "url=https://google.com");
            assertThat(response2.isRedirect());
        });
    }

    @Test
    void testAddIncorrectUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=1111");
            assertThat(response.isRedirect());
        });
    }

    @Test
    void testUrlsPageWhenFewURLsWasAdded() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.post("/urls", "url=https://google.com");
            Response response2 = client.post("/urls", "url=https://noodle.com:8080");
            List<Url> urls = UrlRepository.getEntities();
            String body = response2.body().string();
            assertTrue(urls.stream().allMatch(url -> body.contains(url.getName())));
        });
    }

    @Test
    void testShowUrlByIDPage() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.post("/urls", "url=https://google.com");
            Response response2 = client.post("/urls", "url=https://noodle.com:8080");
            List<Url> urls = UrlRepository.getEntities();
            for (var url : urls) {
                String resp = client.get("/urls/" + url.getId()).body().string();
                String name = url.getName();
                assertThat(resp).contains(name);
                assertThat(resp).contains("<td>" + url.getId() + "</td>");
            }
        });
    }

    @Test
    void testShowUrlByIDPageNotFound() {
        JavalinTest.test(app, (server, client) -> {
            Response response1 = client.post("/urls", "url=https://google.com");
            var id = 33L;
            var notFound = 404;
            String expected = "URL with id = " + id + " not found";
            Optional<Url> url = UrlRepository.find(id);
            assertTrue(url.isEmpty());
            var response = client.get("/urls/" + id);
            assertEquals(expected, response.body().string());
            assertEquals(notFound, response.code());
        });
    }
}
