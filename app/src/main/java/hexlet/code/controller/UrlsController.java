package hexlet.code.controller;


import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

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
        UrlsPage page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setResult("success");
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL with id = " + id + " not found"));
        UrlPage page = new UrlPage(url);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void check(Context ctx) {
        ctx.redirect("/");
    }
}
