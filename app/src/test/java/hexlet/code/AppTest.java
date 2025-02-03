package hexlet.code;


import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import kong.unirest.HttpStatus;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AppTest {
    static Javalin app;
    static MockWebServer mockServer;
    static String baseUrl;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws Exception {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeAll
    static void startMockWebServer() throws Exception {
        mockServer = new MockWebServer();
        MockResponse goodMockedResponse = new MockResponse()
                .setBody(readFixture("goodCase.html"));
        mockServer.enqueue(goodMockedResponse);
        mockServer.enqueue(goodMockedResponse);
        mockServer.start();
        baseUrl = mockServer.url("/").toString().replaceAll("/$", "");
    }

    @AfterAll
    static void tearDown() throws IOException {
        // Остановка приложения и мок-сервера после всех тестов
        if (app != null) {
            app.stop();
        }
        mockServer.shutdown();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        app = App.getApp();

    }

    @AfterEach
    void tearDownApp() {
        // Остановка приложения после всех тестов
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            assertThat(client.get(NamedRoutes.root()).code()).isEqualTo(200);
            assertThat(client.get(NamedRoutes.root()).body().string())
                    .contains("<h1 class=\"display-3 mb-0\">Анализатор страниц</h1>");
        });
    }

    @Test
    void testAddCorrectUrl() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.post(NamedRoutes.urlsIndex(), "url=https://google.com");
            assertThat(response.code()).isEqualTo(200);
            assertTrue(UrlRepository.search("https://google.com").isPresent());
            assertThat(response.body().string()).contains("https://google.com");
            assertEquals(1, UrlRepository.search("https://google.com").get().getId());
            assertEquals(1, UrlRepository.getEntities().size());
            var response2 = client.post(NamedRoutes.urlsIndex(), "url=https://google.com:8080");
            assertTrue(UrlRepository.search("https://google.com:8080").isPresent());
            assertThat(response2.code()).isEqualTo(200);
            assertThat(response2.body().string()).contains("https://google.com:8080");
            assertEquals(2, UrlRepository.getEntities().size());
            assertEquals(2, UrlRepository.search("https://google.com:8080").get().getId());
        });
    }

    @Test
    void testRedirectAfterAddCorrectUrlTwice() {
        JavalinTest.test(app, (server, client) -> {
            client.post(NamedRoutes.urlsIndex(), "url=https://google.com");
            assertEquals(1, UrlRepository.getEntities().size());
            var response = client.post(NamedRoutes.urlsIndex(), "url=https://google.com");
            assertEquals(1, UrlRepository.getEntities().size());
            assertTrue(UrlRepository.search("https://google.com").isPresent());
            //при добавлении URL-дубля происходит редирект на "/"
            //после редиректа сервер автоматически возвращает код 200 вместо 301
            assertEquals(HttpStatus.OK, response.code());
        });
    }

    @Test
    void testRedirectAfterAddIncorrectUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsIndex(), "url=1111");
            assertEquals(0, UrlRepository.getEntities().size());
            //при добавлении некорректного URL происходит редирект на "/"
            //после редиректа сервер автоматически возвращает код 200 вместо 301
            assertEquals(HttpStatus.OK, response.code());
        });
    }

    @Test
    void testUrlsPageWhenFewURLsWasAdded() {
        JavalinTest.test(app, (server, client) -> {
            client.post(NamedRoutes.urlsIndex(), "url=https://google.com");
            Response response2 = client.post(NamedRoutes.urlsIndex(), "url=https://noodle.com:8080");
            List<Url> urls = UrlRepository.getEntities();
            String body = response2.body().string();
            assertEquals(2, UrlRepository.getEntities().size());
            assertTrue(urls.stream().allMatch(url -> body.contains(url.getName())));
        });
    }

    @Test
    void testShowUrlPageByID() {
        JavalinTest.test(app, (server, client) -> {
            client.post(NamedRoutes.urlsIndex(), "url=https://google.com");
            client.post(NamedRoutes.urlsIndex(), "url=https://noodle.com:8080");
            assertEquals(2, UrlRepository.getEntities().size());
            List<Url> urls = UrlRepository.getEntities();
            for (var url : urls) {
                String resp = client.get(NamedRoutes.urlShow(url.getId())).body().string();
                String name = url.getName();
                assertThat(resp).contains(name);
                assertThat(resp).contains("<td>" + url.getId() + "</td>");
            }
        });
    }

    @Test
    void testShowUrlPageByIDNotFound() {
        JavalinTest.test(app, (server, client) -> {
            client.post(NamedRoutes.urlsIndex(), "url=https://google.com");
            var incorrectID = 33L;
            String notFoundMessage = "URL with id = " + incorrectID + " not found";
            Optional<Url> url = UrlRepository.find(incorrectID);
            assertTrue(url.isEmpty());
            var response = client.get(NamedRoutes.urlShow(incorrectID));
            assertEquals(notFoundMessage, response.body().string());
            assertEquals(HttpStatus.NOT_FOUND, response.code());
        });
    }

    @Test
    void testCheckCorrectUrlIsSuccess() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            client.post(NamedRoutes.urlsIndex(), "url=" + baseUrl);
            Long id = UrlRepository.search(baseUrl).get().getId();
            var response = client.post(NamedRoutes.urlCheck(id));
            var body = response.body().string();
            assertThat(response.code()).isEqualTo(200);
            assertTrue(body.contains("<td>1</td>"));
            assertTrue(body.contains("<td>200</td>"));
            assertTrue(body.contains("<td>Тестовый заголовок с тегом title</td>"));
            assertTrue(body.contains("<td>Тестовый заголовок первого уровня</td>"));
            assertTrue(body.contains("<td>Описание description</td>"));
            var checksNumber = UrlCheckRepository.getEntities(id).size();
            assertEquals(checksNumber, 1);
            var response2 = client.post(NamedRoutes.urlCheck(id));
            var body2 = response2.body().string();
            assertThat(response2.code()).isEqualTo(200);
            assertTrue(body2.contains("<td>2</td>"));
            assertTrue(body2.contains("<td>200</td>"));
            assertTrue(body2.contains("<td>Тестовый заголовок с тегом title</td>"));
            assertTrue(body2.contains("<td>Тестовый заголовок первого уровня</td>"));
            assertTrue(body2.contains("<td>Описание description</td>"));
            var url = UrlRepository.find(id).get();
            var urlChecks = UrlCheckRepository.getEntities(id);
            for (var urlCheck : urlChecks) {
                url.addUrlCheck(urlCheck);
            }
            var expectedH1 = "Тестовый заголовок первого уровня";
            assertEquals(2, url.getLastUrlCheck().getId());
            assertEquals(expectedH1, url.getLastUrlCheck().getH1());
            var checksNumber2 = UrlCheckRepository.getEntities(id).size();
            assertEquals(checksNumber2, 2);
        });
    }
}
