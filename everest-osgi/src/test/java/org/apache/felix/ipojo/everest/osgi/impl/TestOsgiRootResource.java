package org.apache.felix.ipojo.everest.osgi.impl;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;
/**
 * Test the iPOJO m_everest resource.
 */
public class TestOsgiRootResource {

    private Everest m_everest;

    public static final String FANCY_VERSION = "7.8.9.mock";

    @Before
    public void setUp() {
        m_everest = new Everest();

    }

    @Test
    public void testEmpty() throws ResourceNotFoundException, IllegalActionOnResourceException {

    }



}
