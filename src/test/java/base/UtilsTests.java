package base;

import org.junit.jupiter.api.Test;
import base.Helpers.Utils;
import java.net.MalformedURLException;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTests {

    @Test
    public void testUrlValidation() {
        String[] urls = new String[] {
                "test",
                "dddddd",
                "http://hello.com",
                "https://google.bg",
                "https://google.bg?query=something",
                "hppt://domain.com",
                "",
                null,
                "1",
                "      "
        };

        assertTrue(Utils.isUrlValid(urls[2]));
        assertTrue(Utils.isUrlValid(urls[3]));
        assertTrue(Utils.isUrlValid(urls[4]));

        assertFalse(Utils.isUrlValid(urls[0]));
        assertFalse(Utils.isUrlValid(urls[1]));
        assertFalse(Utils.isUrlValid(urls[5]));
        assertFalse(Utils.isUrlValid(urls[6]));
        assertFalse(Utils.isUrlValid(urls[7]));
        assertFalse(Utils.isUrlValid(urls[8]));
        assertFalse(Utils.isUrlValid(urls[9]));
    }

    @Test
    public void testUrlIdGeneration() {
        for (int i = 0; i < 100; i++) {
            String randomString = Utils.generateRandomString(5);

            assertEquals(5, randomString.length());
            assertTrue(randomString.matches("[a-zA-Z]+"));
        }
    }

    @Test
    public void testCreateValidationWithEmptyUrl() {
        RootService service = new RootService();

        Exception exception = assertThrows(MalformedURLException.class, () -> {
            service.create("", "127.0.0.1");
        });

        assertEquals("Invalid url.", exception.getMessage());
    }

    @Test
    public void testCreateValidationWithNullUrl() {
        RootService service = new RootService();

        Exception exception = assertThrows(MalformedURLException.class, () -> {
            service.create(null, "127.0.0.1");
        });

        assertEquals("Invalid url.", exception.getMessage());
    }

    @Test
    public void testCreateValidationWithInvalidUrl() {
        RootService service = new RootService();

        Exception exception = assertThrows(MalformedURLException.class, () -> {
            service.create("test", "127.0.0.1");
        });

        assertEquals("Invalid url.", exception.getMessage());
    }
}
