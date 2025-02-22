package hexlet.code.controller;

import hexlet.code.dto.UrlCheckPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static io.javalin.rendering.template.TemplateUtil.model;


public class UrlsController {

    public static void addUrl(Context ctx) {
        try {
            String rawURI = ctx.formParam("url");
            URL url = URI.create(rawURI != null ? rawURI : "").toURL();
            String protocol = url.getProtocol();
            String authority = url.getAuthority();
            String name = protocol + "://" + authority;
            if (UrlRepository.search(name).isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.redirect(NamedRoutes.root());
            } else {
                UrlRepository.save(new Url(name));
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.redirect(NamedRoutes.urlsIndex());
            }
        } catch (MalformedURLException | IllegalArgumentException | NullPointerException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.root());
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.redirect(NamedRoutes.root());
        }
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        Map<Long, UrlCheck> lastChecks = UrlCheckRepository.getLastChecks();
        UrlsPage page = new UrlsPage(urls, lastChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setResult("success");
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL with id = " + id + " not found"));
        List<UrlCheck> checks = UrlCheckRepository.getEntities(url.getId());
        UrlCheckPage page;
        if (!checks.isEmpty()) {
            page = new UrlCheckPage(checks, url);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setResult("success");
        } else {
            page = new UrlCheckPage(List.of(), url);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setResult("alert");
        }
        ctx.render("urls/show.jte", model("page", page));

    }

    public static void check(Context ctx) {
        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlRepository.find(urlId)
                    .orElseThrow(() -> new NotFoundResponse("URL with id = " + urlId + " not found"));
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            Document body = Jsoup.parse(response.getBody());
            int responseStatus = response.getStatus();

            Element h1 = body.selectFirst("h1");
            String header = (h1 == null) ? "" : h1.text();

            String title = body.title();

            Element descr = body.selectFirst("meta[name=description]");
            String description = (descr == null) ? "" : descr.attr("content");

            UrlCheck urlCheck = new UrlCheck(header, responseStatus, title, description);
            url.addUrlCheck(urlCheck);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.redirect(NamedRoutes.urlShow(urlId));
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.redirect(NamedRoutes.urlShow(urlId));
        }
    }
}
