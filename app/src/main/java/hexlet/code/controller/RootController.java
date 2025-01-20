package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import io.javalin.http.Context;

import static io.javalin.rendering.template.TemplateUtil.model;


public class RootController {
    public static void index(Context ctx) {
        MainPage page = new MainPage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setResult("alert");
        ctx.render("index.jte", model("page", page));
    }
}
