package org.ow2.chameleon.query.test;

/**
 * Created with IntelliJ IDEA.
 * User: colin
 * Date: 22/08/13
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */

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




}
