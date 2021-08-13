package base.Helpers;

import org.springframework.jdbc.core.RowMapper;
import base.UrlEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortenedUrlMapper implements RowMapper<UrlEntity> {
    @Override
    public UrlEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UrlEntityFactory.create(
                rs.getInt("id"), rs.getString("urlId"), rs.getString("url"), rs.getString("ip")
        );
    }
}
