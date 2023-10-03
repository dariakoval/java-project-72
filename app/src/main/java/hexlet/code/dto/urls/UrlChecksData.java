package hexlet.code.dto.urls;

import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UrlChecksData {

    private List<UrlCheck> urlChecks;
}
