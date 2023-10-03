package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlChecksController {
    public static void addCheck(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        String urlName = url.getName();
        HttpResponse<String> response = Unirest.get(urlName).asString();
        int statusCode = response.getStatus();
        String body = response.getBody();

        Matcher matcher = Pattern.compile("(<head>[\\s\\S]*?<title>)(?<title>[\\s\\S]*?)(?=</title>)" +
                "([\\s\\S]*?\"description\"[\\s\\S]*?content=\")(?<description>[\\s\\S]*?)(?=\">)" +
                "([\\s\\S]*?<h1[\\s\\S]*?>)(?<h1>[\\s\\S]*?)(?=</h1>)").matcher(body);
        String title = "";
        String description = "";
        String h1 = "";

        while (matcher.find()){
            title = matcher.group("title");
            description = matcher.group("description");
            h1 = matcher.group("h1");
        }

        UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
        UrlChecksRepository.save(urlCheck);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
