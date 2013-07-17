import org.apache.felix.ipojo.everest.client.api.EverestClientApi;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;

public class TestFunctionality extends CommonTest {

    @Test
    public void testReadTop() throws ResourceNotFoundException, IllegalActionOnResourceException {
        EverestClientApi testAPI = new EverestClientApi(everest);
        testAPI.read("/system");
    }
}