import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ow2.chameleon.testing.helpers.BaseTest;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;

public class CommonTest extends BaseTest {

    /**
     * The everest services.
     */
    @Inject
    EverestService everest;

    /**
     * Common test options.
     */
    @Override
    protected Option[] getCustomOptions() {

        return options(  // everest bundles
                mavenBundle("org.apache.felix.ipojo", "everest-core").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-system").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-client-API").versionAsInProject(),
                // Fest assert JARs wrapped as bundles
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject()),
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject()
        );
    }


    @Override
    public boolean deployTestBundle() {
        return true;
    }

    @Test
    public void True() {
        assertThat("true").isEqualTo("true");
    }

    /**
     * Shortcut method to process a READ request on an everest resource.
     *
     * @param path the path of the resource to read
     * @return the read resource
     * @throws org.apache.felix.ipojo.everest.services.ResourceNotFoundException
     *          if the resource cannot be found
     * @throws org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException
     *          if READ is not a valid operation on the targeted resource
     */
    public Resource read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
    }


}
