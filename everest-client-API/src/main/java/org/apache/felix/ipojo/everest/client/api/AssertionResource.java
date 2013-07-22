package org.apache.felix.ipojo.everest.client.api;/*
 * User: Colin
 * Date: 22/07/13
 * Time: 11:36
 * 
 */


import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;

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
