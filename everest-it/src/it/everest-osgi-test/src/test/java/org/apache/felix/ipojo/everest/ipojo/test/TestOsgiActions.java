package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleResource;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.After;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.startlevel.FrameworkStartLevel;

import java.util.HashMap;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/29/13
 * Time: 9:10 AM
 */
public class TestOsgiActions extends Common {

    @After
    public void commonTearDown() {
        try {
            ipojoHelper.dispose();
            osgiHelper.dispose();
        } catch (Exception e) {
            // this may happen in this test suite
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testOsgiDelete() throws ResourceNotFoundException, IllegalActionOnResourceException {
        //BEHOLD this should break things!
        Resource resource = osgiHelper.waitForService(Resource.class, "(type=osgi)", 5000);

        Resource osgi = get("/osgi");
        delete(osgi.getPath());
        // limboo
    }

    @Test
    public void testOsgiRestart() throws ResourceNotFoundException, IllegalActionOnResourceException {
        //BEHOLD this should break things!
        Resource osgi = get("/osgi");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("restart", true);
        update(osgi.getPath(), params);
        // limboo
    }

    @Test
    public void testOsgiInitialBundleStartLevel() throws ResourceNotFoundException, IllegalActionOnResourceException {

        Resource osgi = get("/osgi");
        // get initial bundle start level
        Integer bundleStartlevel = osgi.getMetadata().get("startlevel.bundle", Integer.class);
        System.out.println(bundleStartlevel);
        FrameworkStartLevel fwStartlevel = osgiHelper.getBundle(0).adapt(FrameworkStartLevel.class);
        assertThat(bundleStartlevel).isEqualTo(fwStartlevel.getInitialBundleStartLevel());

        Resource bundles = get("/osgi/bundles");
        for (Resource bundle : bundles.getResources()) {
            BundleResource bundleResource = bundle.adaptTo(BundleResource.class);
            System.out.println(bundleResource.getBundleId() + " : " + bundleResource.getStartLevel());
        }

        // set initial bundle start level
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("startlevel.bundle", 2);
        update(osgi.getPath(), params);
        osgi = get("/osgi");
        assertThat(osgi.getMetadata().get("startlevel.bundle")).isEqualTo(2);
    }

    @Test
    public void testOsgiStartLevel() throws ResourceNotFoundException, IllegalActionOnResourceException, InterruptedException {

        Resource osgi = get("/osgi");
        // get bundle start level
        Integer startlevel = osgi.getMetadata().get("startlevel", Integer.class);
        System.out.println("startlevel: " + startlevel);
        FrameworkStartLevel fwStartlevel = osgiHelper.getBundle(0).adapt(FrameworkStartLevel.class);
        assertThat(startlevel).isEqualTo(fwStartlevel.getStartLevel());

        // set start level
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("startlevel", 3);
        update(osgi.getPath(), params);

        // wait for start level passage
        System.out.println("Waiting for start level passage");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Interrupted
        }
        osgi = get("/osgi");
        startlevel = osgi.getMetadata().get("startlevel", Integer.class);
        assertThat(startlevel).isEqualTo(3);

        Resource bundles = get("/osgi/bundles");
        for (Resource bundle : bundles.getResources()) {
            BundleResource bundleResource = bundle.adaptTo(BundleResource.class);
            if (bundleResource.getStartLevel() > startlevel) {
                assertThat(bundleResource.getState()).isEqualTo(OsgiResourceUtils.bundleStateToString(Bundle.RESOLVED));
            }
            //System.out.println(bundleResource.getBundleId() + " :"+bundleResource.getSymbolicName()+"-"+bundleResource.getState()+" : " + bundleResource.getStartLevel());
        }
    }
}
