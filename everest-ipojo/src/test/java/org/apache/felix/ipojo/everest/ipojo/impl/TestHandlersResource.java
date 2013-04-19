package org.apache.felix.ipojo.everest.ipojo.impl;

import org.apache.felix.ipojo.HandlerFactory;
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
 * Test the iPOJO handler everest resource.
 */
public class TestHandlersResource extends TestIpojoResource {

    public static final String FANCY_HANDLER_NS = "org.example.fancy";
    public static final String FANCY_HANDLER_NAME = "FancyHandler";

    @Before
    public void setUp() {
        super.setUp();

        // Create a fake fancy handler factory and bind it.
        HandlerFactory fancy = mock(HandlerFactory.class);
        when(fancy.getHandlerName()).thenReturn(FANCY_HANDLER_NAME);
        when(fancy.getNamespace()).thenReturn(FANCY_HANDLER_NS);
        m_ipojo.bindHandler(fancy);
    }

    @Test
    public void testHandlersResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/handler"), null));
    }

    @Test
    public void testFancyHandlerNamespaceResourceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/handler").addElements(FANCY_HANDLER_NS), null));
    }

    @Test
    public void testFancyHandlerNamespaceNameIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everest.process(new DefaultRequest(Action.GET, Path.from("/ipojo/handler").addElements(FANCY_HANDLER_NS, FANCY_HANDLER_NAME), null));
    }


}
