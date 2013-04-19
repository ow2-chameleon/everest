package org.apache.felix.ipojo.everest.ipojo.impl;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.ipojo.FactoriesResource;
import org.apache.felix.ipojo.everest.ipojo.IpojoResource;
import org.apache.felix.ipojo.everest.services.*;
import org.apache.felix.ipojo.metadata.Element;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the iPOJO factory everest resource.
 */
public class TestFactoriesResource extends TestIpojoResource {

    public static final String FANCY_NAME = "org.example.FancyComponent";

    protected FactoriesResource m_factories;

    @Before
    public void setUp()  {
        super.setUp();

        // Create a fake fancy factory and bind it.
        Factory fancy = mock(Factory.class);
        when(fancy.getName()).thenReturn(FANCY_NAME);
        when(fancy.getVersion()).thenReturn(FANCY_VERSION);
        when(fancy.getComponentMetadata()).thenReturn(new Element("metadata", "fancy"));
        m_ipojo.bindFactory(fancy);
    }

    @Test
    public void testFactoriesResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/factory"), null));
    }

    @Test
    public void testFancyFactoryNameResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/factory").addElements(FANCY_NAME), null));
    }

    @Test
    public void testGetFancyFactoryWithNameAndVersion() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.fromElements("ipojo", "factory", FANCY_NAME, FANCY_VERSION), null));
    }


}
