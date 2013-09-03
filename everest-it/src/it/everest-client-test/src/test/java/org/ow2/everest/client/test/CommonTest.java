package org.ow2.everest.client.test;


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
                systemProperty("ipojo.processing.synchronous").value("true"),
                systemProperty("everest.processing.synchronous").value("true"),
                mavenBundle("org.ow2.chameleon.everest", "everest-core").versionAsInProject(),
                mavenBundle("org.ow2.chameleon.everest", "everest-system").versionAsInProject(),
            // Fest assert JARs wrapped as bundles
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject()),
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject(),
                mavenBundle("org.apache.felix", "org.apache.felix.log").versionAsInProject(),
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




}
