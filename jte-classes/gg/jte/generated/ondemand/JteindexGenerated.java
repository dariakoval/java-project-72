package gg.jte.generated.ondemand;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {28,28,28,28,28,28};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor) {
		jteOutput.writeContent("<!doctype html>\n<html lang=\"en\">\n    <head>\n        <meta charset=\"utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n        <title>Welcome page</title>\n        <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css\"\n            rel=\"stylesheet\"\n            integrity=\"sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We\"\n            crossorigin=\"anonymous\">\n    </head>\n    <body>\n        <nav class=\"navbar navbar-expand-lg navbar-light bg-light\">\n            <div class=\"collapse navbar-collapse\" id=\"navbarNavDropdown\">\n                <ul class=\"navbar-nav\">\n                    <li class=\"nav-item active\">\n                        <a class=\"nav-link\" href=\"/\">Home</a>\n                    </li>\n                </ul>\n            </div>\n        </nav>\n\n        <div class=\"mx-auto p-4 py-md-5\">\n            <h2>Hello, World!</h2>\n        </div>\n\n    </body>\n</html>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		render(jteOutput, jteHtmlInterceptor);
	}
}
