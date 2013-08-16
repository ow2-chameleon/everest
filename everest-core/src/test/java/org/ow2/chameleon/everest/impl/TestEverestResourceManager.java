/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.impl;

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.managers.everest.EverestRootResource;
import org.ow2.chameleon.everest.services.*;
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
        everest.bindRootResource(this.manager);
    }

    @Test
    public void testMetadata() {
         System.out.println(manager.getMetadata());
    }

    @Test
    public void testGetOnRoot() throws ResourceNotFoundException, IllegalActionOnResourceException {
        DefaultRequest request = new DefaultRequest(Action.READ, Path.from("/everest"), null);
        Resource resource = everest.process(request);
        System.out.println(resource.getMetadata());
        System.out.println(resource.getRelations());
    }

    @Test
    public void testEverest() throws ResourceNotFoundException, IllegalActionOnResourceException {
        DefaultRequest request = new DefaultRequest(Action.READ, Path.from("/everest/domains/everest"), null);
        Resource resource = everest.process(request);
        System.out.println(resource);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testResourceNotFound() throws ResourceNotFoundException, IllegalActionOnResourceException {
        DefaultRequest request = new DefaultRequest(Action.READ, Path.from("/everest/not"), null);
        everest.process(request);
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testIllegalPost() throws ResourceNotFoundException, IllegalActionOnResourceException {
        DefaultRequest request = new DefaultRequest(Action.UPDATE, Path.from("/everest"), null);
        everest.process(request);
    }


}
