package org.ow2.chameleon.everest.ipojo.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.ow2.chameleon.everest.osgi.OsgiResourceUtils;
import org.ow2.chameleon.everest.osgi.bundle.BundleResource;
import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.event.Event;
import org.ow2.chameleon.testing.helpers.TimeUtils;
import org.ow2.chameleon.testing.tinybundles.ipojo.IPOJOStrategy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/28/13
 * Time: 7:20 PM
 */
@ExamReactorStrategy(PerMethod.class)
public class TestBundleActions extends EverestOsgiTest {

    InputStream input = null;

    File file = new File("target/tested/bundle.jar");

    @Override
    public boolean deployTestBundle() {
        return false;
    }

    @Override
    protected Option[] getCustomOptions() {
        try {
            input = testedBundleStream();
            file = testedBundleFile(input, file);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return options(
                systemProperty("ipojo.processing.synchronous").value("true"),
                systemProperty("everest.processing.synchronous").value("true"),
                everestBundles(),
                osgiBundles(),
                festBundles()
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

    public Resource installTestBundle() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = get("/osgi/bundles");
        Resource bundleResource = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("location", file.toURI().toString());
        params.put("input", input);
        return create(r.getPath(), params);
    }

    public Resource startBundle(Resource resource) throws ResourceNotFoundException, IllegalActionOnResourceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newState", "ACTIVE");
        params.put("update", false);
        resource = update(resource.getPath(), params);
        return resource;
    }

    @Test
    public void testBundleIsNotHere() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource<?> r = get("/osgi/bundles");
        Resource<?> testedBundle = null;
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

        Resource bundleResource = installTestBundle();
        String symbolicName = bundleResource.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(bundleResource.getMetadata().get("bundle-state", String.class)).isEqualTo("INSTALLED");

        assertThat(bundleResource).isNotNull();
        // test event
        Event last = createdEvents.getLast();
        assertThat(last.getProperty("canonicalPath")).isEqualTo(bundleResource.getCanonicalPath().toString());
        testCreatedEventFrom(bundleResource.getCanonicalPath().toString());

    }

    @Test
    public void testBundleStart() throws ResourceNotFoundException, IllegalActionOnResourceException, MalformedURLException {

        Resource bundleResource = installTestBundle();
        String symbolicName = bundleResource.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(bundleResource.getMetadata().get("bundle-state", String.class)).isEqualTo("INSTALLED");
        assertThat(bundleResource).isNotNull();
        testCreatedEventFrom(bundleResource.getCanonicalPath().toString());

        bundleResource = startBundle(bundleResource);
        assertThat(bundleResource.getMetadata().get("bundle-state", String.class)).isEqualTo("ACTIVE");
        assertThat(bundleResource).isNotNull();
        testUpdatedEventFrom(bundleResource.getCanonicalPath().toString());

    }

    @Test
    public void testBundleStop() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource bundleResource = installTestBundle();
        assertThat(bundleResource).isNotNull();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newState", "RESOLVED");
        bundleResource = update(bundleResource.getPath(), params);
        String symbolicName = bundleResource.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(bundleResource.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_STATE, String.class)).isEqualTo("RESOLVED");

        //Event last = updatedEvents.getLast();
        //assertThat(last.getProperty("canonicalPath")).isEqualTo(bundleResource.getCanonicalPath().toString());
        testUpdatedEventFrom(bundleResource.getCanonicalPath().toString());
        updatedEvents.clear();

        // Stopped now restarting
        params = new HashMap<String, Object>();
        params.put("newState", "ACTIVE");
        bundleResource = update(bundleResource.getPath(), params);
        symbolicName = bundleResource.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(bundleResource.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_STATE, String.class)).isEqualTo("ACTIVE");

        //assertThat(updatedEvents.getLast()).isNotEqualTo(last);
        //last = updatedEvents.getLast();
        //assertThat(last.getProperty("canonicalPath")).isEqualTo(bundleResource.getCanonicalPath().toString());
        testUpdatedEventFrom(bundleResource.getCanonicalPath().toString());

    }

    @Test
    public void testBundleUninstall() throws ResourceNotFoundException, IllegalActionOnResourceException {

        Resource<?> bundleResource = installTestBundle();
        bundleResource = startBundle(bundleResource);

        bundleResource = delete(bundleResource.getPath());
        String symbolicName = bundleResource.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(bundleResource.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_STATE, String.class)).isEqualTo("UNINSTALLED");
        Bundle bundle = bundleResource.adaptTo(Bundle.class);

        System.out.println(OsgiResourceUtils.bundleStateToString(bundle.getState()));
        assertThat(bundleResource).isNotNull();
        // test Event
        testDeletedEventFrom(bundleResource.getCanonicalPath().toString());
    }

    @Test
    public void testBundleRefresh() throws ResourceNotFoundException, IllegalActionOnResourceException {

        Resource bundleResource = installTestBundle();
        bundleResource = startBundle(bundleResource);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("refresh", true);
        update(bundleResource.getPath(), params);

        assertThat(bundleResource).isNotNull();
        // test Event
        //Event last = updatedEvents.getLast();
        //assertThat(last.getProperty("canonicalPath")).isEqualTo(bundleResource.getCanonicalPath().toString());
    }

    @Test
    public void testBundleUpdate() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // install test bundle
        Resource<?> res = installTestBundle();
        updatedEvents.clear();
        String symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        BundleResource bundleResource = res.adaptTo(BundleResource.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(res.getMetadata().get("bundle-state", String.class)).isEqualTo("INSTALLED");

        // resolve test bundle
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newState", OsgiResourceUtils.bundleStateToString(Bundle.RESOLVED));
        update(res.getPath(), params);
        assertThat(bundleResource.getState()).isEqualTo(OsgiResourceUtils.bundleStateToString(Bundle.RESOLVED));
        testUpdatedEventFrom(bundleResource.getCanonicalPath().toString());

        updatedEvents.clear();
        // update test bundle
        params = new HashMap<String, Object>();
        params.put("update", true);
        update(bundleResource.getPath(), params);
        assertThat(bundleResource.getState()).isEqualTo(OsgiResourceUtils.bundleStateToString(Bundle.INSTALLED));
        testUpdatedEventFrom(bundleResource.getCanonicalPath().toString());

    }

    @Test
    public void testBundleStartLevel() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // set initial bundle start level to 6
        Resource osgi = get("/osgi");
        updatedEvents.clear();
        createdEvents.clear();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("startlevel.bundle", 6);
        update(osgi.getPath(), params);
        assertThat(osgi.getMetadata().get("startlevel.bundle", Integer.class)).isEqualTo(6);

        // install test bundle
        Resource bundles = get("/osgi/bundles");
        params = new HashMap<String, Object>();
        params.put("location", file.toURI().toString());
        params.put("input", input);
        Resource<?> res = create(bundles.getPath(), params);
        String symbolicName = res.getMetadata().get(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, String.class);
        BundleResource bundleResource = res.adaptTo(BundleResource.class);
        assertThat(symbolicName).isEqualTo("test.bundle");
        assertThat(res.getMetadata().get("bundle-state", String.class)).isEqualTo("INSTALLED");
        assertThat(bundleResource.getStartLevel()).isEqualTo(6);
        testCreatedEventFrom(bundleResource.getCanonicalPath().toString());

        // start bundle, it should not start
        bundleResource.changeState("ACTIVE");
        assertThat(bundleResource.getState()).isEqualTo("INSTALLED");

        // set bundle start level to 3
        params = new HashMap<String, Object>();
        params.put("startLevel", 3);
        update(bundleResource.getPath(), params);
        //bundleResource.setStartLevel(3);
        assertThat(bundleResource.getStartLevel()).isEqualTo(3);
        // wait for start level passage
        System.out.println("Waiting for start level passage");
        try {
            Thread.sleep(TimeUtils.TIME_FACTOR * 1000);
        } catch (InterruptedException e) {
            // Interrupted
        }
        assertThat(bundleResource.getState()).isEqualTo("ACTIVE");
        testUpdatedEventFrom(bundleResource.getCanonicalPath().toString());

        for (Event updatedEvent : updatedEvents) {
            System.out.println(updatedEvent.getProperty("canonicalPath"));
        }


    }

    @Test
    public void testBundleAdapt() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource<?> r = get("/osgi/bundles");
        for (Resource<?> res : r.getResources()) {
            Bundle bundle = res.adaptTo(Bundle.class);
            assertThat(bundle.getBundleId()).isEqualTo(res.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_ID, Long.class));
        }
        for (Resource<?> res : r.getResources()) {
            BundleResource bundleResource = res.adaptTo(BundleResource.class);
            assertThat(bundleResource.getBundleId()).isEqualTo(res.getMetadata().get(OsgiResourceUtils.BundleNamespace.BUNDLE_ID, Long.class));
        }
    }

}
