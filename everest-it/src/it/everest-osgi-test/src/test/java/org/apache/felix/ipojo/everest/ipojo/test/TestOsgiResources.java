package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
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

}
