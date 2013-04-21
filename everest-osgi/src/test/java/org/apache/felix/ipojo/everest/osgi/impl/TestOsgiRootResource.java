package org.apache.felix.ipojo.everest.osgi.impl;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.osgi.OsgiRootResource;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import static org.mockito.Mockito.mock;

/**
 * Test the iPOJO m_everest resource.
 */
public class TestOsgiRootResource {

    private Everest m_everest;
    private OsgiRootResource m_osgi;

    @Before
    public void setUp() {
        m_everest = new Everest();

        // Create a fake bundle context.
        BundleContext context = mock(BundleContext.class);

        m_osgi = new OsgiRootResource(context);
        m_everest.bindRootResource(m_osgi);
    }

    @Test
    public void testEmpty() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Well don't know how to write unit tests...

    }


}
