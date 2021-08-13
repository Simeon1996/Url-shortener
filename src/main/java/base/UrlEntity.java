package base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UrlEntity {
    private Long id;
    private String urlId;
    private String url;

    @JsonIgnore
    private String ip;

    public UrlEntity(String urlId, String url, String ip) {
        this.urlId = urlId;
        this.url = url;
        this.ip = ip;
    }

    public UrlEntity(long id, String urlId, String url, String ip) {
        this(urlId, url, ip);
        this.id = id;
    }
}
