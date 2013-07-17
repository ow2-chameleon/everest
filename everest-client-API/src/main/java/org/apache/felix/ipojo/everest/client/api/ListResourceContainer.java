package org.apache.felix.ipojo.everest.client.api;/*
 * User: Colin
 * Date: 17/07/13
 * Time: 15:14
 * 
 */

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class ListResourceContainer {


    List<ResourceContainer> m_resourcesContainer = new ArrayList<ResourceContainer>();

    private ResourceContainer read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {

        return new ResourceContainer(EverestClientApi.m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));

    }

    public ListResourceContainer(ResourceContainer resourceContainer) {
        m_resourcesContainer.add(resourceContainer);
    }

    public ListResourceContainer(List<ResourceContainer> listresource) {
        this.m_resourcesContainer = listresource;
    }

    public synchronized ListResourceContainer children() {


        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer currentResourceContainer : m_resourcesContainer) {
            returnResources.addAll(currentResourceContainer.children().m_resourcesContainer);
        }

        return new ListResourceContainer(returnResources);
    }


    public synchronized ListResourceContainer relations() throws ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            returnResources.addAll(current.relations().m_resourcesContainer);
        }

        return new ListResourceContainer(returnResources);

    }

    public synchronized ListResourceContainer relation(String relationName) throws ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            returnResources.add(current.relation(relationName));
        }

        return new ListResourceContainer(returnResources);

    }


}
