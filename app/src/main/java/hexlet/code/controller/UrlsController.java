package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsData;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UrlsController {
    private static final int ROWS_PER_PAGE = 12;
    public static void create(Context ctx) throws SQLException {
        try {
            var inputParam = ctx.formParamAsClass("url", String.class)
                    .getOrDefault("")
                    .trim()
                    .toLowerCase();
            URL inputUrl = new URL(inputParam);
            String protocol = inputUrl.getProtocol();
            String host = inputUrl.getHost();
            int port = inputUrl.getPort();
            URL customUrl = new URL(protocol, host, port, "");

            if (UrlsRepository.existsByName(customUrl.toString().trim())) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "info");
                ctx.redirect(NamedRoutes.urlsPath());
                return;
            }

            Url url = new Url(customUrl.toString().trim());
            UrlsRepository.save(url);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect(NamedRoutes.urlsPath());

        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void index(Context ctx) throws SQLException {
        int currentPage = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = ROWS_PER_PAGE;

        UrlsData urlsData = UrlsRepository.findEntities(currentPage - 1, rowsPerPage);

        List<Url> urls = urlsData.getUrls();
        int count = urlsData.getTotalCount();

        var lastPage = (int) Math.ceil((float) count / rowsPerPage);
        List<Integer> pages = IntStream
                .range(1, lastPage + 1)
                .boxed()
                .collect(Collectors.toList());

        var page = new UrlsPage(urls, currentPage, pages);

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
