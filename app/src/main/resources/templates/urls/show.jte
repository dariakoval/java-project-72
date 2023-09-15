@import hexlet.code.dto.urls.UrlPage
@param UrlPage page

@template.layout.page(

content = @`
    <div class="mb-3">
        <h1>${page.getUrl().getName()}</h1>
    </div>

    <div class="mb-3">
        ${page.getUrl().getCreatedAtFormat()}
    </div>
`
)