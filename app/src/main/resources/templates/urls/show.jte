@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@param UrlPage page

!{var formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");}
!{var url = page.getUrl();}

@template.layout.page(
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайт ${url.getName()}</h1>

            <table class="table table-bordered table-hover mt-3">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td>${url.getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${url.getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${url.getCreatedAt().toLocalDateTime().format(formatter)}</td>
                </tr>
                </tbody>
            </table>

            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="/urls/1/checks">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>

            <table class="table table-bordered table-hover mt-3">
                <thead>
                <th class="col-1">ID</th>
                <th class="col-1">Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th class="col-2">Дата проверки</th>
                </thead>
                <tbody>

                </tbody>
            </table>
        </div>
    </section>
`
)