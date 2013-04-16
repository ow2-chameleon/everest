package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.managers.everest.EverestRootResource;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test the everest resource manager.
 */
public class TestEverestResourceManager {

    private Everest everest;
    private EverestRootResource manager;

    @Before
    public void setUp() {
        this.everest = new Everest();
        this.manager = new EverestRootResource(everest);
        everest.bindResourceManager(this.manager);
    }

    @Test
    public void testMetadata() {
         System.out.println(manager.getMetadata());
    }

    @Test
    public void testGetOnRoot() throws ResourceNotFoundException, IllegalActionOnResourceException {
        DefaultRequest request = new DefaultRequest(Action.GET, Path.from("/everest"), null);
        Resource resource = everest.process(request);
        System.out.println(resource);
    }


}
