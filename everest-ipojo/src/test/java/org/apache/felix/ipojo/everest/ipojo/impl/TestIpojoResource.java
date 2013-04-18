package org.apache.felix.ipojo.everest.ipojo.impl;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.ipojo.IpojoRootResource;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;
/**
 * Test the iPOJO m_everest resource.
 */
public class TestIpojoResource {

    private Everest m_everest;

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
        m_everest.bindRootResource(new IpojoRootResource(context));

    }

    @Test
    public void testGetIpojoPath() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo"), null));
    }

    @Test
    public void testGetIpojoFactoryPath() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/factory"), null));
    }

    @Test
    public void testGetIpojoHandlerResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/handler"), null));
    }

    @Test
    public void testGetIpojoInstanceResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/instance"), null));
    }

    @Test
    public void testGetIpojoDeclarationResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/declaration"), null));
    }

    @Test
    public void testGetIpojoMetadata() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource ipojo = m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo"), null));
        assertThat(ipojo.getMetadata().get("version")).isEqualTo(FANCY_VERSION);
    }


}
