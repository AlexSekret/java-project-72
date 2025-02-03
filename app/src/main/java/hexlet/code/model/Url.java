package hexlet.code.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Url {
    private Long id;
    @ToString.Include
    private String name;
    private Timestamp createdAt;
    private List<UrlCheck> urlChecks;

    public Url(String name) {
        this.urlChecks = new ArrayList<>();
        this.name = name;
    }

    public UrlCheck getLastUrlCheck() {
        Long lastId = urlChecks.stream()
                .map(UrlCheck::getId)
                .max(Long::compareTo)
                .get();
        return urlChecks.stream()
                .filter(urlCheck -> urlCheck.getId().equals(lastId))
                .toList()
                .getLast();
    }

    public void addUrlCheck(UrlCheck urlCheck) {
        urlCheck.setUrl(this);
        urlChecks.add(urlCheck);
    }
}
