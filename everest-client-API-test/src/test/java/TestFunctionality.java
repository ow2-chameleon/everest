import org.apache.felix.ipojo.everest.client.api.EverestClientApi;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;

public class TestFunctionality extends CommonTest {

    @Test
    public void testReadTop() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClientApi testAPI = new EverestClientApi(everest);

 /*


        try {
            assertThat(testAPI.read("/system").relation("everest:properties").retrieve("java.runtime.name")).isEqualTo("Java(TM) SE Runtime Environment");
        } catch (IllegalResourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } */

    }
}