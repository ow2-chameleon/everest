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

package org.ow2.chameleon.everest.client.api;/*
 * User: Colin
 * Date: 22/07/13
 * Time: 11:36
 * 
 */


import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;

public class AssertionResource {

    private ResourceContainer m_resourceContainer;


    public AssertionResource(Resource resource) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_resourceContainer = new ResourceContainer(resource);
    }


    public synchronized boolean exist() throws ResourceNotFoundException, IllegalActionOnResourceException {
        if (!(m_resourceContainer.retrieve() == null)) {
            return true;
        }
        return false;
    }

    public synchronized boolean isEqualTo(Resource value) {
        if (m_resourceContainer.m_resource.getCanonicalPath() == value.getCanonicalPath()) {
            return true;
        }
        return false;
    }

}
