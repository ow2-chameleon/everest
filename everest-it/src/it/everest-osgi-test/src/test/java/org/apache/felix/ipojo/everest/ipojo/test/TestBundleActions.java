package org.apache.felix.ipojo.everest.ipojo.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Constants;
import org.ow2.chameleon.testing.tinybundles.ipojo.IPOJOStrategy;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/28/13
 * Time: 7:20 PM
 */
public class TestBundleActions extends Common {

    InputStream input = null;

    File file = new File("target/tested/bundle.jar");

    @Configuration
    public Option[] config() throws MalformedURLException {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        input = testedBundleStream();
        file = testedBundleFile(input, file);
        return options(
                // The EventAdmin service
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject(),
                ipojoBundles(),
                everestBundles(),
                junitBundles(),
                festBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN")
        );
    }

    public InputStream testedBundleStream() throws MalformedURLException {

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

        return inputStream;
    }

    public File testedBundleFile(InputStream inputStream, File out) {
        try {
            FileUtils.copyInputStreamToFile(inputStream, out);
            return out;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot compute the url of the manipulated bundle");
        } catch (IOException e) {
            throw new RuntimeException("Cannot write of the manipulated bundle");
        }
    }

    @Test
    public void testBundleIsNotHere() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = get("/osgi/bundles");
        Resource testedBundle = null;
        for (Resource res : r.getResources()) {
            String symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
            if (symbolicName.equals("test.bundle")) {
                testedBundle = res;
            }
        }
        assertThat(testedBundle).isNull();
    }

    @Test
    public void testBundleInstall() throws ResourceNotFoundException, IllegalActionOnResourceException, MalformedURLException {
        Resource r = get("/osgi/bundles");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("location", file.toURI().toString());
        params.put("input", input);
        Resource res = create(r.getPath(), params);
        String symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(res.getMetadata().get("bundle-state", String.class)).isEqualTo("INSTALLED");

        params.put("newState", "ACTIVE");
        params.put("update", false);
        res = update(res.getPath(), params);
        assertThat(res.getMetadata().get("bundle-state", String.class)).isEqualTo("ACTIVE");
    }

    @Test
    public void testBundleStart() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = get("/osgi/bundles");
        for (Resource res : r.getResources()) {
            String symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
            if (symbolicName.equals("test.bundle")) {

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("newState", "RESOLVED");
                res = update(res.getPath(), params);
                symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
                assertThat(symbolicName).isEqualTo("test.bundle");
                assertThat(res.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_STATE, String.class)).isEqualTo("RESOLVED");


                // Stopped now restarting
                params = new HashMap<String, Object>();
                params.put("newState", "ACTIVE");
                res = update(res.getPath(), params);
                symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
                assertThat(symbolicName).isEqualTo("test.bundle");
                assertThat(res.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_STATE, String.class)).isEqualTo("ACTIVE");
            }
        }
    }

    @Test
    public void testBundleStop() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = get("/osgi/bundles");
        for (Resource res : r.getResources()) {
            String symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
            if (symbolicName.equals("test.bundle")) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("newState", "RESOLVED");
                res = update(res.getPath(), params);
                symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
                assertThat(symbolicName).isEqualTo("test.bundle");
                assertThat(res.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_STATE, String.class)).isEqualTo("RESOLVED");
            }
        }
    }

    @Test
    public void testBundleUninstall() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = get("/osgi/bundles");
        for (Resource res : r.getResources()) {
            String symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
            if (symbolicName.equals("test.bundle")) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("newState", "UNINSTALLED");
                res = update(res.getPath(), params);
                symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
                assertThat(symbolicName).isEqualTo("test.bundle");
                assertThat(res.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_STATE, String.class)).isEqualTo("UNINSTALLED");
            }
        }
    }


}
