package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.HashMap;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/28/13
 * Time: 7:11 PM
 */
public class TestBundleStop extends Common {

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
}
