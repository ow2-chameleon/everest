package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/22/13
 * Time: 4:24 PM
 */
public class TestOsgiResources extends Common {

    /**
     * Check that the '/osgi' resource is present.
     */
    @Test
    public void testOsgiRootIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource osgi = get("/osgi");
        Assert.assertNotNull(osgi);
    }

    @Test
    public void testOsgiBundles() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Assert.assertNotNull(get("/osgi/bundles"));
    }

    @Test
    public void testOsgiPackages() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Assert.assertNotNull(get("/osgi/packages"));
    }

    @Test
    public void testOsgiServices() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Assert.assertNotNull(get("/osgi/services"));
    }

    @Test
    public void testOsgiBundleZero() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Assert.assertNotNull(get("/osgi/bundles/0"));
    }

}
