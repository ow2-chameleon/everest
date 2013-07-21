import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 16/07/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class TestResources extends CommonTest {


    /**
     * Check that the '/system' resource is present.
     */
    @Test
    public void testRootIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system");
    }

    /**
     * Check that the '/system/properties' resource is present.
     */
    @Test
    public void testPropertiesIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/properties");
    }

    /**
     * Check that the '/system/environment' resource is present.
     */
    @Test
    public void testEnvironmentIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/environment");
    }

    /**
     * Check that the '/system/mx' resource is present.
     */
    @Test
    public void testMxManagerIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/mx");
    }

    /**
     * Check that the '/system/mx/runtime' resource is present.
     */
    @Test
    public void testRuntimeMxIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/mx/runtime");
    }

    /**
     * Check that the '/system/mx/threads' resource is present.
     */
    @Test
    public void testThreadMxIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/mx/threads");
    }

    /**
     * Check that the '/system/mx/os' resource is present.
     */
    @Test
    public void testOperatingSystemMxIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/mx/os");
    }

    /**
     * Check that the '/system/mx/memory' resource is present.
     */
    @Test
    public void testMemoryMxIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/mx/memory");
    }
}