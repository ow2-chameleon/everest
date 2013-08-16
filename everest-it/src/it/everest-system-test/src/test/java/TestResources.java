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

import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 16/07/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class TestResources extends CommonTest {


    /**
     * Check that the '/system' resource is present.
     */
    @Test
    public void testRootIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system");
    }

    /**
     * Check that the '/system/properties' resource is present.
     */
    @Test
    public void testPropertiesIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/properties");
    }

    /**
     * Check that the '/system/environment' resource is present.
     */
    @Test
    public void testEnvironmentIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/environment");
    }


    /**
     * Check that the '/system/runtime' resource is present.
     */
    @Test
    public void testRuntimeIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/runtime");
    }

    /**
     * Check that the '/system/threads' resource is present.
     */
    @Test
    public void testThreadIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/threads");
    }

    /**
     * Check that the '/system/os' resource is present.
     */
    @Test
    public void testOperatingSystemIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/os");
    }

    /**
     * Check that the '/system/memory' resource is present.
     */
    @Test
    public void testMemoryMxIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/system/memory");

    }
}