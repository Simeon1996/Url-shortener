package base.Helpers;

import base.UrlEntity;

public class UrlEntityFactory {

    public static UrlEntity create(String urlId, String url, String ip) {
        return new UrlEntity(urlId, url, ip);
    }

    public static UrlEntity create(long id, String urlId, String url, String ip) {
        return new UrlEntity(id, urlId, url, ip);
    }
}
