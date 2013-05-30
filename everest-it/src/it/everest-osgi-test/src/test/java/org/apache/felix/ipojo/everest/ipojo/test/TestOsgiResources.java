package org.apache.felix.ipojo.everest.ipojo.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleResource;
import org.apache.felix.ipojo.everest.osgi.packages.PackageResource;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.packageadmin.ExportedPackage;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/22/13
 * Time: 4:24 PM
 */
public class TestOsgiResources extends Common {

    @Configuration
    public Option[] config() throws MalformedURLException {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        return options(
                systemProperty("ipojo.processing.synchronous").value("true"),
                systemProperty("everest.processing.synchronous").value("true"),
                // The EventAdmin service
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject(),
                ipojoBundles(),
                everestBundles(),
                junitBundles(),
                festBundles(),
                testedBundle(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN")
        );
    }

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
        assertThat(osgi).isNotNull();
        List<Resource> resources = osgi.getResources();
        assertThat(resources.size()).isGreaterThanOrEqualTo(4).describedAs("We must have at least 4 resources as we added config admin in tests ");
        for (Resource r : resources) {
            Assert.assertEquals(r.getPath(), r.getCanonicalPath());
        }
    }

    @Test
    public void testBundles() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource bundles = get("/osgi/bundles");
        for (Resource resource : bundles.getResources()) {
            Bundle bundle = resource.adaptTo(Bundle.class);
            BundleResource bundleResource = resource.adaptTo(BundleResource.class);
            assertThat(bundle).isNotNull();
            assertThat(bundleResource).isNotNull();
            assertThat(bundleResource.getBundle()).isEqualTo(bundle);
        }
    }

    @Test
    public void testUsedPackages() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource packages = get("/osgi/packages");
        assertThat(osgiHelper.getPackageAdmin()).isNotNull();
        for (Resource pkg : packages.getResources()) {
            PackageResource packageResource = pkg.adaptTo(PackageResource.class);
            String packageName = packageResource.getPackageName();
            boolean used = pkg.getMetadata().get("in-use", Boolean.class);
            assertThat(used).isEqualTo(packageResource.isUsed());
            ExportedPackage exportedPackage = osgiHelper.getPackageAdmin().getExportedPackage(packageName);
            assertThat(exportedPackage).isNotNull();
            if (used) {
                assertThat(pkg.getMetadata().get("version", Version.class)).isEqualTo(exportedPackage.getVersion());
                assertThat(exportedPackage.getImportingBundles()).isNotEmpty();
            }
        }
    }

    @Test
    public void testServicesList() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = get("/osgi/services");
        int size = r.getResources().size();
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, new String[]{"everest/osgi/services/*"});
        props.put(EventConstants.EVENT_FILTER, "(eventType=CREATED)");
        osgiHelper.getContext().registerService(EventHandler.class.getName(), new EventHandler() {
            public void handleEvent(Event event) {
                System.out.println("EVENT: " + event.toString());
            }
        }, props);
        ServiceRegistration reg = osgiHelper.getContext().registerService(this.getClass().getName(), this, null);

        Assert.assertEquals("Services should have incremented", size + 2, r.getResources().size());
    }

    @Test
    public void testBundleWiring() throws ResourceNotFoundException, IllegalActionOnResourceException {
        //TODO should find a way to write assertions on wires
        Resource bundles = get("/osgi/bundles");
        for (int i = 0; i < bundles.getResources().size(); i++) {
            Resource wires = get("/osgi/bundles/" + i + "/wires");
            for (Resource res : wires.getResources()) {
                assertThat(res).isNotNull();
                //System.out.println(res.getPath());
                //System.out.println("Related to :");
                for (Relation relation : res.getRelations()) {
                    //System.out.println("\t" + relation.getName() + " - " + relation.getHref());
                }
                //System.out.println("\tWires :");
                for (Resource wire : res.getResources()) {
                    for (Relation relation : wire.getRelations()) {
                        assertThat(relation).isNotNull();
                        //System.out.println("\t\t" + relation.getName() + " - " + relation.getHref());
                    }
                }
            }
        }
    }

}
