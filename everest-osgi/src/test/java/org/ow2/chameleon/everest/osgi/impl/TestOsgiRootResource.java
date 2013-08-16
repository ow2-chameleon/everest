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

package org.ow2.chameleon.everest.osgi.impl;

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.osgi.OsgiRootResource;
import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.FrameworkWiring;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
        Bundle zero = mock(Bundle.class, RETURNS_MOCKS);
        BundleContext context = mock(BundleContext.class, RETURNS_MOCKS);

        when(zero.getBundleContext()).thenReturn(context);
        when(context.getBundle(0)).thenReturn(zero);
        when(context.getProperty(anyString())).thenReturn("Some Property");
        when(zero.adapt(FrameworkWiring.class)).thenReturn(mock(FrameworkWiring.class));
        when(zero.adapt(FrameworkStartLevel.class)).thenReturn(mock(FrameworkStartLevel.class));

        m_osgi = new OsgiRootResource(context);
        m_everest.bindRootResource(m_osgi);
    }


    @Test
    public void testEmpty() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Well don't know how to write unit tests...

    }


}
