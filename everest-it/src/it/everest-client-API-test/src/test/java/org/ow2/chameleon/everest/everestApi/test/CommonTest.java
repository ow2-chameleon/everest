package org.ow2.chameleon.everest.everestApi.test;

import org.ow2.chameleon.everest.impl.DefaultRequest;
import org.ow2.chameleon.everest.services.*;
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
                mavenBundle("org.ow2.chameleon.everest", "everest-core").versionAsInProject(),
                mavenBundle("org.ow2.chameleon.everest", "everest-system").versionAsInProject(),
                mavenBundle("org.ow2.chameleon.everest", "everest-client-API").versionAsInProject(),
                // Fest assert JARs wrapped as bundles
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject()),
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject(),
                bootDelegationPackage("com.intellij.rt.coverage.data")
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
     * @throws org.ow2.chameleon.everest.services.ResourceNotFoundException
     *          if the resource cannot be found
     * @throws org.ow2.chameleon.everest.services.IllegalActionOnResourceException
     *          if READ is not a valid operation on the targeted resource
     */
    public Resource read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
    }


}
