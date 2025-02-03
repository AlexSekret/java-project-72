package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString(exclude = "url")
public class UrlCheck {
    private Long id;
    private Integer statusCode;
    private String title;
    private String h1;
    private String description;
    private Timestamp createdAt;
    private Url url;

    public UrlCheck(String h1, int responseStatus, String title, String description) {
        this.h1 = h1;
        this.statusCode = responseStatus;
        this.title = title;
        this.description = description;
    }
}
