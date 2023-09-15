@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(

page = page,
content = @`
    <h1>All urls</h1>
    <div class="mb-3">
        <a href="${NamedRoutes.buildUrlPath()}">Добавить новый url</a>
    </div>
    <table class="table table-striped">
        @for(var url : page.getUrls())
            <tr>
                <td>
                    ${url.getId()}
                </td>
                <td>
                    <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                </td>
                <td>
                    ${url.getCreatedAtFormat()}
                </td>
            </tr>
        @endfor
    </table>
`
)