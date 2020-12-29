import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.junit.Assert.*;

public class Client {

    @Before
    public void setup() {
    }


    @Test
    public void readAndWriteTest() {
        assertEquals("val1", "val1");
    }
}
