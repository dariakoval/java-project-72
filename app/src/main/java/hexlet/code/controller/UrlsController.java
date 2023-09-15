package hexlet.code.controller;

import hexlet.code.dto.urls.BuildUrlPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;

import java.sql.SQLException;
import java.util.Collections;

public class UrlsController {
    public static void build(Context ctx) {
        var page = new BuildUrlPage();
        ctx.render("urls/build.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        try {
            var name = ctx.formParamAsClass("name", String.class)
                    .check(value -> !value.isEmpty(), "Название не должно быть пустым")
                    .get();
            var url = new Url(name);
            UrlsRepository.save(url);
            ctx.sessionAttribute("flash", "Url был успешно добавлен!");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (ValidationException e) {
            var name = ctx.formParam("name");
            var page = new BuildUrlPage(name, e.getErrors());
            ctx.render("urls/build.jte", Collections.singletonMap("page", page)).status(422);
        }
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlsRepository.getEntities();
        var page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        var page = new UrlPage(url);
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }
}
