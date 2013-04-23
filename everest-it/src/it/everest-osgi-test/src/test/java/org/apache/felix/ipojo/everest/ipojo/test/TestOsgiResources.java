package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;

import java.util.List;

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
    public void testBundlesResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Assert.assertNotNull(get("/osgi/bundles"));
    }

    @Test
    public void testPackagesResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Assert.assertNotNull(get("/osgi/packages"));
    }

    @Test
    public void testServicesResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Assert.assertNotNull(get("/osgi/services"));
    }

    /**
     * Check that first level resourcemanagers are present.
     */
    @Test
    public void testOsgiRootResources() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource osgi = get("/osgi");
        Assert.assertNotNull(osgi);
        List<Resource> resources = osgi.getResources();
        Assert.assertEquals("We must have 4 resources as we added config admin in tests ", 4, resources.size());
        for (Resource r : resources) {
            Assert.assertEquals(r.getPath(), r.getCanonicalPath());
        }
    }

    @Test
    public void testBundles() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource bundles = get("/osgi/bundles");
        for (Resource bundle : bundles.getResources()) {
            Bundle b = osgiHelper.getBundle(Long.parseLong(bundle.getPath().getLast()));
            Assert.assertNotNull(b);
        }
    }

    @Test
    public void testUsedPackages() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource packages = get("/osgi/packages");
        Assert.assertNotNull(osgiHelper.getPackageAdmin());
        for (Resource pack : packages.getResources()) {
            String packageName = pack.getMetadata().get("osgi.wiring.package", String.class);
            boolean used = pack.getMetadata().get("in-use", Boolean.class);
            ExportedPackage exportedPackage = osgiHelper.getPackageAdmin().getExportedPackage(packageName);
            Assert.assertNotNull(exportedPackage);
            if (used) {
                Assert.assertEquals(pack.getMetadata().get("version", Version.class), exportedPackage.getVersion());
                Assert.assertTrue(exportedPackage.getImportingBundles().length > 0);
//            }else{
//                System.out.println(pack.getMetadata());
//                Assert.assertEquals(0,exportedPackage.getImportingBundles().length);
            }
        }
    }

    @Test
    public void testServicesList() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = get("/osgi/services");
        int size = r.getResources().size();
        ServiceRegistration reg = osgiHelper.getContext().registerService(this.getClass().getName(), this, null);
        r = get("/osgi/services");
        Assert.assertEquals("Services should have incremented", size + 1, r.getResources().size());
    }

}
