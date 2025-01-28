package hexlet.code.controller;

import hexlet.code.dto.UrlCheckPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.Util;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import static io.javalin.rendering.template.TemplateUtil.model;


public class UrlsController {

    //обработчик запускается, когда с главной страницы "/" прилетает POST-запрос с URL
    public static void addUrl(Context ctx) {
        try {
            String rawURI = ctx.formParam("url");
            URL url = URI.create(rawURI).toURL();
            String protocol = url.getProtocol();
            String authority = url.getAuthority();
            String name = protocol + "://" + authority;
            if (UrlRepository.search(name).isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.redirect("/");
            } else {
                UrlRepository.save(new Url(name));
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.redirect("/urls");
            }
        } catch (MalformedURLException | IllegalArgumentException | NullPointerException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect("/");
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.redirect("/");
        }
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        for (var url : urls) {
            List<UrlCheck> checks = UrlCheckRepository.getEntities(url.getId());
            for (var check : checks) {
                url.addUrlCheck(check);
            }
        }
        UrlsPage page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setResult("success");
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL with id = " + id + " not found"));
        List<UrlCheck> checks = UrlCheckRepository.getEntities(url.getId());
        if (!checks.isEmpty()) {
            UrlCheckPage page = new UrlCheckPage(checks, url);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setResult("success");
            ctx.render("urls/show.jte", model("page", page));
        } else {
            UrlCheckPage page = new UrlCheckPage(List.of(), url);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setResult("alert");
            ctx.render("urls/show.jte", model("page", page));
        }

    }

    public static void check(Context ctx) {
        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlRepository.find(urlId)
                    .orElseThrow(() -> new NotFoundResponse("URL with id = " + urlId + " not found"));
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            Document body = Jsoup.parse(response.getBody());

            int responseStatus = response.getStatus();
            String h1 = Util.getFirstElementText(body, "h1");
            String title = Util.getFirstElementText(body, "title");
            String description = Util.getMetaContent(body);

            UrlCheck urlCheck = new UrlCheck(h1, responseStatus, title, description);
            url.addUrlCheck(urlCheck);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.redirect("/urls/" + urlId);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.redirect("/urls/" + urlId);
        }
    }

}
