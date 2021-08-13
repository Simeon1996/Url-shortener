package base;

import base.Helpers.UrlRequestBody;
import base.Helpers.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.LimitExceededException;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

@RestController
public class RootController {

    @Autowired
    private RootService service;

    @GetMapping(value = "/urls/{id}")
    public ResponseEntity<UrlEntity> get(@PathVariable long id) {
        UrlEntity entity = service.getById(id);
        return ResponseEntity.ok(entity);
    }

    @GetMapping(value = "/urls")
    public ResponseEntity<List<UrlEntity>> getAll(HttpServletRequest request) {
        String clientIp = Utils.getClientIp(request);
        List<UrlEntity> entity = service.getAll(clientIp);
        return ResponseEntity.ok(entity);
    }

    @GetMapping(value = "/{urlId}")
    public ResponseEntity<UrlEntity> redirect(@PathVariable String urlId) {
        UrlEntity entity = service.getByUrlId(urlId);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(entity.getUrl())).build();
    }

    @PostMapping(value = "/urls/create", consumes = { "application/json" })
    public ResponseEntity<UrlEntity> create(@RequestBody UrlRequestBody body, HttpServletRequest request) throws MalformedURLException, LimitExceededException {
        String clientIp = Utils.getClientIp(request);
        return ResponseEntity.ok(service.create(body.getUrl(), clientIp));
    }

    @DeleteMapping(value = "/urls/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, HttpServletRequest request) {
        String clientIp = Utils.getClientIp(request);
        service.delete(id, clientIp);
        return ResponseEntity.noContent().build();
    }
}
