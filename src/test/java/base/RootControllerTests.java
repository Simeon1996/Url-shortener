package base;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import base.Helpers.UrlEntityFactory;

import java.util.ArrayList;
import java.util.List;

import javax.naming.LimitExceededException;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RootControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RootService service;

    @Test
    public void testGet() throws Exception {
        long expectedId = 1;
        String expectedUrlId = "AbcdE";
        String expectedUrl = "http://dummy-url.com?query=dummy";
        String expectedIp = "37.64.13.71";

        when(service.getById(1)).thenReturn(UrlEntityFactory.create(expectedId, expectedUrlId, expectedUrl, expectedIp));

        mockMvc.perform(get("/urls/1"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.urlId").value(expectedUrlId))
                .andExpect(jsonPath("$.url").value(expectedUrl));
    }

    @Test
    public void testGetAllByUrlId() throws Exception {
        String ip = "127.0.0.1";

        List<UrlEntity> urls = new ArrayList<>(3);

        urls.add(UrlEntityFactory.create(1, "AbcdE", "http://dummy-url.com/aaa", "127.0.0.1"));
        urls.add(UrlEntityFactory.create(2, "AbcdF", "http://dummy-url.com/fff", "127.0.0.1"));
        urls.add(UrlEntityFactory.create(3, "AbcdG", "http://dummy-url.com/ggg", "127.0.0.1"));

        when(service.getAll(ip)).thenReturn(urls);

        mockMvc.perform(get("/urls"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(urls.get(0).getId()))
                .andExpect(jsonPath("$.[0].urlId").value(urls.get(0).getUrlId()))
                .andExpect(jsonPath("$.[0].url").value(urls.get(0).getUrl()))
                .andExpect(jsonPath("$.[1].id").value(urls.get(1).getId()))
                .andExpect(jsonPath("$.[1].urlId").value(urls.get(1).getUrlId()))
                .andExpect(jsonPath("$.[1].url").value(urls.get(1).getUrl()))
                .andExpect(jsonPath("$.[2].id").value(urls.get(2).getId()))
                .andExpect(jsonPath("$.[2].urlId").value(urls.get(2).getUrlId()))
                .andExpect(jsonPath("$.[2].url").value(urls.get(2).getUrl()));
    }

    @Test
    public void testGetAllWhileEmptyByUrlId() throws Exception {
        String ip = "127.0.0.1";

        List<UrlEntity> urls = new ArrayList<>(0);

        when(service.getAll(ip)).thenReturn(urls);

        mockMvc.perform(get("/urls"))
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetByUnknownId() throws Exception
    {
        String expectedErrorMessage = "Element not found.";
        when(service.getById(1)).thenThrow(new NoSuchElementException(expectedErrorMessage));

        mockMvc.perform(get("/urls/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.description").value("uri=/urls/1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testDeleteByValidId() throws Exception
    {
        String ip = "37.64.13.71";

        doNothing().when(service).delete(1, ip);

        mockMvc.perform(delete("/urls/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteByValidIdAndNotOwnIp() throws Exception
    {
        String ip = "127.0.0.1";
        String expectedErrorMessage = "You are not authorized to perform the action.";

        // Client shouldn't be allowed to perform delete action if the url was created from a different ip address.
        doThrow(new InvalidParameterException("You are not authorized to perform the action.")).when(service).delete(1, ip);

        mockMvc.perform(delete("/urls/delete/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.description").value("uri=/urls/delete/1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testDeleteByInvalidId() throws Exception
    {
        String expectedErrorMessage = "Element not found.";
        String ip = "127.0.0.1";

        doThrow(new NoSuchElementException(expectedErrorMessage)).when(service).delete(1, ip);

        mockMvc.perform(delete("/urls/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.description").value("uri=/urls/delete/1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testRedirectByInvalidUrlId() throws Exception
    {
        String expectedErrorMessage = "Element not found.";
        String urlId = "AbcdE";

        when(service.getByUrlId(urlId)).thenThrow(new NoSuchElementException(expectedErrorMessage));

        mockMvc.perform(get("/" + urlId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.description").value("uri=/AbcdE"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testRedirectByValidUrlId() throws Exception
    {
        long expectedId = 1;
        String urlId = "AbcdE";
        String url = "http://dummy-url.com?query=dummy";
        String ip = "127.0.0.1";

        when(service.getByUrlId(urlId)).thenReturn(UrlEntityFactory.create(expectedId, urlId, url, ip));

        mockMvc.perform(get("/" + urlId))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://dummy-url.com?query=dummy"));

    }

    @Test
    public void testCreateSuccessful() throws Exception
    {
        String ip = "127.0.0.1";
        String url = "http://dummy-url.com?query=dummy";
        String urlId = "AbcdE";

        UrlEntity entity = UrlEntityFactory.create(urlId, url, ip);

        when(service.create(url, ip)).thenReturn(entity);

        mockMvc.perform(post("/urls/create").content("{ \"url\": \"http://dummy-url.com?query=dummy\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.urlId").value(urlId))
                .andExpect(jsonPath("$.url").value(url));
    }

    @Test
    public void testCreateEmptyUrl() throws Exception
    {
        String ip = "127.0.0.1";
        String url = "";
        String exception = "Invalid url.";

        when(service.create(url, ip)).thenThrow(new MalformedURLException("Invalid url."));

        mockMvc.perform(post("/urls/create").content("{ \"url\": \"\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_ACCEPTABLE.value()))
                .andExpect(jsonPath("$.message").value(exception))
                .andExpect(jsonPath("$.description").value("uri=/urls/create"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testCreateWithNoUrl() throws Exception
    {
        mockMvc.perform(post("/urls/create").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testCreateWithInvalidMediaType() throws Exception
    {
        mockMvc.perform(post("/urls/create").contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void testCreateInvalidUrl() throws Exception
    {
        String url = "dummy-url.com?query=dummy";
        String ip = "127.0.0.1";
        String exception = "Invalid url.";

        when(service.create(url, ip)).thenThrow(new MalformedURLException(exception));

        mockMvc.perform(post("/urls/create").content("{ \"url\": \"dummy-url.com?query=dummy\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_ACCEPTABLE.value()))
                .andExpect(jsonPath("$.message").value(exception))
                .andExpect(jsonPath("$.description").value("uri=/urls/create"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testCreateMoreUrlsThanAllowed() throws Exception
    {
        String url = "http://dummy-url.com?query=dummy";
        String ip = "127.0.0.1";
        String exception = "The allowed number of urls that can be added is exceeded for you.";

        when(service.create(url, ip)).thenThrow(new LimitExceededException(exception));

        mockMvc.perform(post("/urls/create").content("{ \"url\": \"http://dummy-url.com?query=dummy\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value(exception))
                .andExpect(jsonPath("$.description").value("uri=/urls/create"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
