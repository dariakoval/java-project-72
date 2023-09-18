@import gg.jte.Content
@import hexlet.code.dto.BasePage
@import hexlet.code.util.NamedRoutes
@param Content content
@param Content footer = null
@param BasePage page = null

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We"
          crossorigin="anonymous">
</head>
<body class="d-flex flex-column min-vh-100">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="${NamedRoutes.rootPath()}">Анализатор страниц</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <div class="navbar-nav">
                <a class="nav-link" href="${NamedRoutes.rootPath()}">Главная</a>
                <a class="nav-link" href="${NamedRoutes.urlsPath()}">Сайты</a>
            </div>
        </div>
    </div>
</nav>

@if(page != null && page.getFlash() != null)
    <div class="alert alert-${page.getFlashType()}" role="alert">
        ${page.getFlash()}
    </div>
@endif


<main class="flex-grow-1">
    ${content}
</main>

@if (footer != null)
    <footer class="footer border-top py-3 mt-5 bg-light">
        <div class="container-xl">
            <div class="text-center">
                ${footer}
            </div>
        </div>
    </footer>
@endif

</body>
</html>