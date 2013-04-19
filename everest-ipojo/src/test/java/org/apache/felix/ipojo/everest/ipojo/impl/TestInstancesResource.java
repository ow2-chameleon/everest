package org.apache.felix.ipojo.everest.ipojo.impl;

import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the iPOJO instance everest resource.
 */
public class TestInstancesResource extends TestIpojoResource {

    public static final String FANCY_INSTANCE_NAME = "FancyInstance";

    @Before
    public void setUp() {
        super.setUp();

        // Create a fake fancy instance architecture and bind it.
        InstanceDescription desc = mock(InstanceDescription.class);
        when(desc.getName()).thenReturn(FANCY_INSTANCE_NAME);
        Architecture fancy = mock(Architecture.class);
        when(fancy.getInstanceDescription()).thenReturn(desc);

        m_ipojo.bindInstance(fancy);
    }

    @Test
    public void testInstancesResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/instance"), null));
    }

    @Test
    public void testFancyInstanceNameResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/instance").addElements(FANCY_INSTANCE_NAME), null));
    }

}
