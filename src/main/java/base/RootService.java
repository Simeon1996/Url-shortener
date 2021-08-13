package base;

import base.Helpers.UrlEntityFactory;
import base.Helpers.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.naming.LimitExceededException;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;
import java.util.*;

@Service
public class RootService {

    @Value("${app.baseUrl}")
    private String baseUrl;

    @Value("${urls.count.per.user}")
    private long urlsCountPerUser;

    @Value("${urls.id.length}")
    private short idLength;

    @Value("${urls.delete.after.days}")
    private short deleteAfterDays;

    @Autowired
    private RootRepository repository;

    public UrlEntity create(String clientUrl, String clientIp) throws MalformedURLException, LimitExceededException {
        if (!Utils.isUrlValid(clientUrl)) {
            throw new MalformedURLException("Invalid url.");
        }

        // @TODO validate url against xss attacks

        if (hasUserExceededUrlsLimit(clientIp)) {
            throw new LimitExceededException("The allowed number of urls that can be added is exceeded for you.");
        }

        String urlId = generateUniqueId(idLength);

        UrlEntity entity = UrlEntityFactory.create(urlId, clientUrl, clientIp);

        boolean saved = repository.save(entity);

        if (!saved) {
            throw new IllegalStateException("Something went wrong during attempts to save.");
        }

        entity.setIp(null);

        return entity;
    }

    public void delete(long id, String ip) {
        UrlEntity entity = getById(id);

        if (!entity.getIp().equals(ip)) {
            throw new InvalidParameterException("You are not authorized to perform the action.");
        }

        repository.delete(id);
    }

    public UrlEntity getById(long id) {
        return repository.getById(id).orElseThrow(() -> new NoSuchElementException("Not found."));
    }

    public UrlEntity getByUrlId(String id) {
        if (id == null || id.isEmpty() || id.length() != idLength) {
            throw new InvalidParameterException("Invalid urlId.");
        }

        return repository.getByUrlId(id).orElseThrow(() -> new NoSuchElementException("Not found."));
    }

    private String generateUniqueId(short digits) {
        String id = Utils.generateRandomString(digits);

        boolean urlExists = repository.checkUrlIdExists(id);

        while (urlExists) {
            id = Utils.generateRandomString(digits);
            urlExists = repository.checkUrlIdExists(id);
        }

        return id;
    }

    public boolean hasUserExceededUrlsLimit(String ip) {
        return repository.getUrlsCountByIp(ip) >= urlsCountPerUser;
    }

    public List<UrlEntity> getAll(String clientIp) {

        if (clientIp == null || clientIp.isEmpty()) {
            throw new InvalidParameterException("Invalid parameter.");
        }

        return repository.getByIp(clientIp);
    }

    @Scheduled(cron = "0 0 0 * * *") // everyday at midnight
    public void deleteStaleUrls() {
        int deletedRecords = repository.deleteUrlsCreatedBeforeDaysAgo(deleteAfterDays);

        // @TODO log how many records were deleted.
    }
}
