package org.apache.felix.ipojo.everest.ipojo.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;
import org.ow2.chameleon.testing.tinybundles.ipojo.IPOJOStrategy;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Bootstrap the test from this project
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Common {

    OSGiHelper osgiHelper;

    IPOJOHelper ipojoHelper;

    @Inject
    BundleContext context;

    @Inject
    EverestService everest;

    Bundle ipojoBundle;
    Bundle testBundle;
    Bundle testBundle2;


    @Configuration
    public Option[] config() throws MalformedURLException {
        // Adjust the log level
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);

        return options(
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                // iPOJO bundles
                mavenBundle("org.apache.felix", "org.apache.felix.ipojo").versionAsInProject(),
                // Test utility bundles
                junitBundles(),
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject()),
                mavenBundle("org.ow2.chameleon.testing", "osgi-helpers").versionAsInProject(),
                // everest bundles
                mavenBundle("org.apache.felix.ipojo", "everest-core").versionAsInProject(),
                mavenBundle("org.apache.felix.ipojo", "everest-ipojo").versionAsInProject(),
                // Generated test bundles
                testedBundle(),
                testedBundle2()
        );
    }

    /**
     * Read the resource at the given path.
     *
     * @param path the path of the resource
     * @return the read resource
     * @throws ResourceNotFoundException if there is no resource at the given path
     * @throws IllegalActionOnResourceException if reading this resource is an illegal action
     */
    public Resource read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
    }



    @Before
    public void commonSetUp() {
        osgiHelper = new OSGiHelper(context);
        ipojoHelper = new IPOJOHelper(context);

        // Dump OSGi Framework information
        String vendor = (String) osgiHelper.getBundle(0).getHeaders().get(Constants.BUNDLE_VENDOR);
        if (vendor == null) {
            vendor = (String) osgiHelper.getBundle(0).getHeaders().get(Constants.BUNDLE_SYMBOLICNAME);
        }
        String version = (String) osgiHelper.getBundle(0).getHeaders().get(Constants.BUNDLE_VERSION);
        System.out.println("OSGi Framework : " + vendor + " - " + version);

        waitForStability(context);

        // Get the interesting bundles
        for(Bundle b : context.getBundles()) {
            if ("org.apache.felix.ipojo".equals(b.getSymbolicName())) {
                ipojoBundle = b;
            } else if ("test.bundle".equals(b.getSymbolicName())) {
                testBundle = b;
            } else if ("test.bundle2".equals(b.getSymbolicName())) {
                testBundle2 = b;
            }
        }
    }

    @After
    public void commonTearDown() {
        ipojoHelper.dispose();
        osgiHelper.dispose();
    }

    // Generated bundle that contains everything in src/main/java BUT BarProviderImpl2 class.
    // See testedBundle2() for details...
    public Option testedBundle() throws MalformedURLException {
        File out = new File("target/tested/bundle.jar");

        TinyBundle tested = TinyBundles.bundle();

        // We look inside target/classes to find the class and resources
        File classes = new File("target/classes");
        Collection<File> files = FileUtils.listFilesAndDirs(classes, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        List<File> services = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory()) {
                // By convention we export of .services and .service package
                if (file.getName().endsWith("services") || file.getName().endsWith("service")) {
                    services.add(file);
                }
            } else {
                // We need to compute the path
                String path = file.getAbsolutePath().substring(classes.getAbsolutePath().length() + 1);

                // Skip BarProviderImpl2
                if (path.contains("BarProviderImpl2")) {
                    continue;
                }

                tested.add(path, file.toURI().toURL());
                System.out.println(file.getName() + " added to " + path);
            }
        }

        String export = "";
        for (File file : services) {
            if (export.length() > 0) {
                export += ", ";
            }
            String path = file.getAbsolutePath().substring(classes.getAbsolutePath().length() + 1);
            String packageName = path.replace('/', '.');
            export += packageName;
        }

        System.out.println("Exported packages : " + export);

        InputStream inputStream = tested
                .set(Constants.BUNDLE_SYMBOLICNAME, "test.bundle")
                .set(Constants.IMPORT_PACKAGE, "*")
                .set(Constants.EXPORT_PACKAGE, export)
                .build(IPOJOStrategy.withiPOJO(new File("src/main/resources")));

        try {
            FileUtils.copyInputStreamToFile(inputStream, out);
            return bundle(out.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot compute the url of the manipulated bundle");
        } catch (IOException e) {
            throw new RuntimeException("Cannot write of the manipulated bundle");
        }
    }

    // It seems that we cannot define iPOJO component types twice (with different versions) in the same bundle
    // So the second bundle just contains version "2.0.0" or BarProviderImpl component type
    // Implementation class name is BarProviderImpl2
    public Option testedBundle2() throws MalformedURLException {
        File out = new File("target/tested/bundle2.jar");

        TinyBundle tested = TinyBundles.bundle();

        // We just add the BarProviderImpl2 class, service interfaces are imported from testedBundle()
        File classes = new File("target/classes");
        Collection<File> files = FileUtils.listFilesAndDirs(classes, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        List<File> services = new ArrayList<File>();
        for (File file : files) {
            if (!file.isDirectory()) {
                // We need to compute the path
                String path = file.getAbsolutePath().substring(classes.getAbsolutePath().length() + 1);

                // Skip non BarProviderImpl2
                if (!path.contains("BarProviderImpl2")) {
                    continue;
                }

                tested.add(path, file.toURI().toURL());
                System.out.println(file.getName() + " added to " + path);
            }
        }

        InputStream inputStream = tested
                .set(Constants.BUNDLE_SYMBOLICNAME, "test.bundle2")
                .set(Constants.IMPORT_PACKAGE, "*")
                .build(IPOJOStrategy.withiPOJO(new File("src/main/resources")));

        try {
            FileUtils.copyInputStreamToFile(inputStream, out);
            return bundle(out.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot compute the url of the manipulated bundle");
        } catch (IOException e) {
            throw new RuntimeException("Cannot write of the manipulated bundle");
        }
    }

    /**
     * Waits for stability:
     * <ul>
     * <li>all bundles are activated
     * <li>service count is stable
     * </ul>
     * If the stability can't be reached after a specified time,
     * the method throws a {@link IllegalStateException}.
     *
     * @param context the bundle context
     * @throws IllegalStateException when the stability can't be reach after a several attempts.
     */
    private void waitForStability(BundleContext context) throws IllegalStateException {
        // Wait for bundle initialization.
        boolean bundleStability = getBundleStability(context);
        int count = 0;
        while (!bundleStability && count < 500) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // Interrupted
            }
            count++;
            bundleStability = getBundleStability(context);
        }

        if (count == 500) {
            System.err.println("Bundle stability isn't reached after 500 tries");
            throw new IllegalStateException("Cannot reach the bundle stability");
        }

        boolean serviceStability = false;
        count = 0;
        int count1 = 0;
        int count2 = 0;
        while (!serviceStability && count < 500) {
            try {
                ServiceReference[] refs = context.getServiceReferences((String) null, null);
                count1 = refs.length;
                Thread.sleep(500);
                refs = context.getServiceReferences((String) null, null);
                count2 = refs.length;
                serviceStability = count1 == count2;
            } catch (Exception e) {
                System.err.println(e);
                serviceStability = false;
                // Nothing to do, while recheck the condition
            }
            count++;
        }

        if (count == 500) {
            System.err.println("Service stability isn't reached after 500 tries (" + count1 + " != " + count2);
            throw new IllegalStateException("Cannot reach the service stability");
        }
    }

    /**
     * Are bundle stables.
     *
     * @param bc the bundle context
     * @return <code>true</code> if every bundles are activated.
     */
    private boolean getBundleStability(BundleContext bc) {
        boolean stability = true;
        Bundle[] bundles = bc.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            stability = stability && (bundles[i].getState() == Bundle.ACTIVE);
        }
        return stability;
    }

    /**
     * Check that the EverestService service is present.
     */
    @Test
    public void testEverestServiceIsPresent() {
        assertNotNull(everest);
    }

}
