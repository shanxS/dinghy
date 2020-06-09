import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.junit.Assert.*;

public class Client {
    @Mock Dinghy dClient;

    @Before
    public void setup() {
        dClient = new DinghyImpl<String, String>();
    }

    @Test
    public void readAndWriteTest() {
        dClient.commit("key1", "val1");
        assertEquals("val1", dClient.read("key1"));
    }
}
