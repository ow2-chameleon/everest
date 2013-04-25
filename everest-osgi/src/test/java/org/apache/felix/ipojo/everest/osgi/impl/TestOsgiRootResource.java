package org.apache.felix.ipojo.everest.osgi.impl;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.osgi.OsgiRootResource;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.FrameworkWiring;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Bundle zero = mock(Bundle.class);
        BundleContext context = mock(BundleContext.class);

        when(zero.getBundleContext()).thenReturn(context);
        when(context.getBundle(0)).thenReturn(zero);
        when(context.getProperty(anyString())).thenReturn("Some Property");
        when(zero.adapt(any(Class.class))).thenReturn(mock(FrameworkWiring.class));

        m_osgi = new OsgiRootResource(context);
        m_everest.bindRootResource(m_osgi);
    }


    @Test
    public void testEmpty() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Well don't know how to write unit tests...

    }


}
