package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class UrlsPage  extends BasePage {

    private List<Map<String, Object>> urls;

    private int currentPage;

    private List<Integer> pages;
}
