package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.commons.io.FileUtils;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.ow2.chameleon.testing.helpers.BaseTest;
import org.ow2.chameleon.testing.tinybundles.ipojo.IPOJOStrategy;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;

import static junit.framework.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.CoreOptions.bundle;

/**
 * Common configuration for the everest iPOJO tests.
 */
public class EverestIpojoTestCommon extends BaseTest {

    /**
     * The bundle symbolic name of the first generated test bundle.
     */
    public static final String TEST_BUNDLE_SYMBOLIC_NAME = "test.bundle";

    /**
     * The bundle symbolic name of the second generated test bundle.
     */
    public static final String TEST_BUNDLE_2_SYMBOLIC_NAME = "test.bundle2";

    /**
     * The everest service.
     */
    @Inject
    EverestService everest;

    /**
     * The iPOJO bundle.
     */
    Bundle ipojoBundle;

    /**
     * The first generated test bundle.
     */
    Bundle testBundle;

    /**
     * The second generated test bundle.
     */
    Bundle testBundle2;

    /**
     * Disable construction of the test bundle, as we need to construct manually two of them.
     *
     * @return {@code false}
     */
    @Override
    public boolean deployTestBundle() {
        return false;
    }

    /**
     * Enable deployment of the ConfigAdmin bundle, needed by everest OSGi.
     *
     * @return {@code true}
     */
    @Override
    public boolean deployConfigAdmin() {
        return true;
    }

    /**
     * Common test options.
     */
    @Override
    protected Option[] getCustomOptions() {
        return options(
                systemProperty("ipojo.processing.synchronous").value("true"),
                // everest bundles
                mavenBundle("org.apache.felix.ipojo", "everest-core").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-ipojo").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-osgi").versionAsInProject(),
                // The EventAdmin service
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject(),
                // Generated test bundles
                generateBundle(TEST_BUNDLE_SYMBOLIC_NAME, "org.apache.felix.ipojo.everest.ipojo.test.b1"),
                generateBundle(TEST_BUNDLE_2_SYMBOLIC_NAME, "org.apache.felix.ipojo.everest.ipojo.test.b2"),
                // Fest assert JARs wrapped as bundles
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject())
        );
    }

    /**
     * Common test setup.
     */
    @Before
    public void commonSetUp() {
        super.commonSetUp();
        // Get the interesting bundles
        ipojoBundle = osgiHelper.getBundle("org.apache.felix.ipojo");
        testBundle = osgiHelper.getBundle(TEST_BUNDLE_SYMBOLIC_NAME);
        testBundle2 = osgiHelper.getBundle(TEST_BUNDLE_2_SYMBOLIC_NAME);
    }

    /**
     *
     * @param symbolicName
     * @param packageName
     * @return
     */
    private static Option generateBundle(String symbolicName, String packageName) {
        TinyBundle bundle = TinyBundles.bundle();

        // We look inside target/classes/$packageName to find the class and resources
        File classesDir = new File("target/classes/");
        File packageDir = new File(classesDir, packageName.replace('.', '/'));
        Collection<File> classes = FileUtils.listFiles(packageDir, null, true);

        // Add all classes and resources to the tiny bundle
        int index = classesDir.getAbsolutePath().length() +1;
        for (File clazz : classes) {
            String relativePath = clazz.getAbsolutePath().substring(index);
            try {
                bundle.add(relativePath, clazz.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException("cannot add resource/class: " + relativePath, e);
            }
        }

        // Add the bundle headers and generate the stream
        InputStream stream = bundle
                .set(Constants.BUNDLE_SYMBOLICNAME, symbolicName)
                .set(Constants.IMPORT_PACKAGE, "*")
                .set(Constants.EXPORT_PACKAGE, packageName + ", " + packageName + ".*")
                .build(IPOJOStrategy.withiPOJO());

        // Write the bundle and return its URL
        File output = new File("target/tested/" + symbolicName + ".jar");
        try {
            FileUtils.copyInputStreamToFile(stream, output);
            return bundle(output.toURI().toURL().toExternalForm());
        } catch (IOException e) {
            throw new RuntimeException("cannot write generated bundle: " + output.getAbsolutePath(), e);
        }
    }

    /**
     * Read the resource at the given path.
     *
     * @param path the path of the resource
     * @return the read resource
     * @throws ResourceNotFoundException if there is no resource at the given path
     * @throws IllegalActionOnResourceException
     *                                   if reading this resource is an illegal action
     */
    public Resource read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
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
