package base;

import base.Helpers.ShortenedUrlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Repository
public class RootRepository {

    private static final String TABLE_NAME = "urls";

    @PostConstruct
    public void init() {

        // @TODO use ipv4 and ipv6 for ip address and use another type in the table
        String createTableQuery = "CREATE TABLE " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTO_INCREMENT, url TEXT NOT NULL, urlId CHAR(5) NOT NULL UNIQUE, ip VARCHAR(60) NOT NULL," +
                " created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin";

        jdbcTemplate.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        jdbcTemplate.execute(createTableQuery);
        jdbcTemplate.execute("CREATE INDEX ip_idx ON " + TABLE_NAME + "(ip)");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean save(UrlEntity entity) {
        int result = jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (url, urlId, ip) VALUES (?, ?, ?)",
                entity.getUrl(), entity.getUrlId(), entity.getIp()
        );

        return result != 0;
    }

    public boolean checkUrlIdExists(String urlId) {
        Integer objectsCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE urlId = ?",
                (rs, rowNum) -> rs.getInt(1), urlId
        );

        if (objectsCount == null) {
            return false;
        }

        return objectsCount > 0;
    }

    public int getUrlsCountByIp(String ip) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE ip = ?",
                (rs, rowNum) -> rs.getInt(1), ip
        );

        if (count == null) {
            return 0;
        }

        return count;
    }

    public boolean delete(long id) {
        return jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", id) > 0;
    }

    public Optional<UrlEntity> getById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM " + TABLE_NAME + " WHERE id = ?", new ShortenedUrlMapper(), id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<UrlEntity> getByIp(String ip) {
        return jdbcTemplate.query("SELECT id, url, urlId, ip FROM " + TABLE_NAME + " WHERE ip = ?", new ShortenedUrlMapper(), ip);
    }

    public Optional<UrlEntity> getByUrlId(String urlId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM " + TABLE_NAME + " WHERE urlId = ?", new ShortenedUrlMapper(), urlId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public int deleteUrlsCreatedBeforeDaysAgo(int days) {
        return jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE DATEDIFF(CURRENT_TIMESTAMP, created) > " + days);
    }
}
