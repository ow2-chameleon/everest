package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.event.EventAdmin;
import org.ow2.chameleon.testing.helpers.BaseTest;

import javax.inject.Inject;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Bootstrap the test from this project
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EverestOsgiTest extends BaseTest {

    @Inject
    EverestService everest;

    @Inject
    EventAdmin eventAdmin;

    @Override
    protected Option[] getCustomOptions() {
        return options(
                systemProperty("ipojo.processing.synchronous").value("true"),
                systemProperty("everest.processing.synchronous").value("true"),
                everestBundles(),
                osgiBundles(),
                festBundles()
        );
    }

    @Override
    public boolean deployConfigAdmin() {
        return true;
    }

    @Override
    public boolean deployTestBundle() {
        return true;
    }

    @Before
    public void commonSetUp() {
        super.commonSetUp();
        // create bundles

    }

    @After
    public void commonTearDown() {
        ipojoHelper.dispose();
        osgiHelper.dispose();
    }

    public CompositeOption everestBundles() {
        return new DefaultCompositeOption(
                mavenBundle("org.apache.felix.ipojo", "everest-core").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-ipojo").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-osgi").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-system").versionAsInProject()
        );
    }

    public CompositeOption osgiBundles() {
        return new DefaultCompositeOption(
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject(),
                mavenBundle("org.apache.felix", "org.apache.felix.log").versionAsInProject()
        );
    }

    // Wrap fest-util and fest-assert into a bundle so we can test with happiness.
    public CompositeOption festBundles() {
        return new DefaultCompositeOption(
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject())
        );
    }

    public Resource get(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
    }

    public Resource update(Path path, Map<String, Object> params) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.UPDATE, path, params));
    }

    public Resource create(Path path, Map<String, Object> params) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.CREATE, path, params));
    }

    public Resource delete(Path path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.DELETE, path, null));
    }

    /**
     * Check that the EverestService service is present.
     * <p>
     * This test also avoids the test container to complain with a "no test method" error.
     * </p>
     */
    @Test
    public void testEverestServiceIsPresent() {
        assertNotNull(everest);
    }


}
