package org.apache.felix.ipojo.everest.ipojo.impl;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.ipojo.IpojoResource;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the iPOJO m_everest resource.
 */
public class TestIpojoResource {

    protected Everest m_everest;
    protected IpojoResource m_ipojo;

    public static final String FANCY_VERSION = "7.8.9.mock";

    @Before
    public void setUp() {
        m_everest = new Everest();

        // Create a fake iPOJO bundle with a fancy version.
        Bundle ipojo = mock(Bundle.class);
        when(ipojo.getSymbolicName()).thenReturn("org.apache.felix.ipojo");
        when(ipojo.getVersion()).thenReturn(Version.parseVersion(FANCY_VERSION));

        // Create a fake bundle context.
        BundleContext context = mock(BundleContext.class);
        when(context.getBundles()).thenReturn(new Bundle[]{ipojo});

        // Now we can create and register the "/ipojo" resource
        m_ipojo = new IpojoResource(context);
        m_everest.bindRootResource(m_ipojo);
    }

    @Test
    public void testIpojoResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo"), null));
    }

    @Test
    public void testIpojoVersion() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource ipojo = m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo"), null));
        assertThat(ipojo.getMetadata().get("version")).isEqualTo(FANCY_VERSION);
    }

}
