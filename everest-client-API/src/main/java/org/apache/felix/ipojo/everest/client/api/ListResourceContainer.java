package org.apache.felix.ipojo.everest.client.api;/*
 * User: Colin
 * Date: 17/07/13
 * Time: 15:14
 * 
 */

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;

import java.util.ArrayList;
import java.util.List;

public class ListResourceContainer {


    List<ResourceContainer> m_resourcesContainer = new ArrayList<ResourceContainer>();


    private ResourceContainer read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {

        return new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));

    }

    public ListResourceContainer(ResourceContainer resourceContainer) {
        m_resourcesContainer.add(resourceContainer);
    }

    public ListResourceContainer(List<ResourceContainer> listresource) {
        this.m_resourcesContainer = listresource;
    }

    public synchronized ListResourceContainer parent() throws ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            try {
                returnResources.add(current.parent());
            } catch (RuntimeException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }


        throw new RuntimeException();
    }


    public synchronized ListResourceContainer childrens() throws RuntimeException {


        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer currentResourceContainer : m_resourcesContainer) {
            try {
                returnResources.addAll(currentResourceContainer.childrens().m_resourcesContainer);
            } catch (RuntimeException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        throw new RuntimeException();
    }

    public synchronized ListResourceContainer children(String name) throws RuntimeException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer currentResourceContainer : m_resourcesContainer) {
            try {
                returnResources.add(currentResourceContainer.children(name));
            } catch (RuntimeException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        throw new RuntimeException();


    }

    public synchronized ListResourceContainer relations() throws RuntimeException, ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            try {
                returnResources.addAll(current.relations().m_resourcesContainer);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        throw new RuntimeException();
    }

    public synchronized ListResourceContainer relation(String relationName) throws RuntimeException, ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            try {
                returnResources.add(current.relation(relationName));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        throw new RuntimeException();
    }

    public synchronized List<String> retrieve(String metadataId) {

        List<String> returnList = new ArrayList<String>();

        for (ResourceContainer current : m_resourcesContainer) {
            if (current.retrieve(metadataId) != null) {
                returnList.add(current.retrieve(metadataId));
            }
        }

        if (returnList.isEmpty()) {
            return null;
        }


        return returnList;

    }

    public synchronized List<Resource> retrieve() {

        List<Resource> returnList = new ArrayList<Resource>();

        for (ResourceContainer current : m_resourcesContainer) {
            returnList.add(current.retrieve());
        }

        if (returnList.isEmpty()) {
            return null;
        }

        return returnList;

    }


    public synchronized <T> List<T> retrieve(String metadataId, Class<T> clazz) {

        List<T> returnList = new ArrayList<T>();

        for (ResourceContainer current : m_resourcesContainer) {
            if (current.retrieve(metadataId) != null) {
                returnList.add(current.retrieve(metadataId, clazz));
            }
        }

        if (returnList.isEmpty()) {
            return null;
        }

        return returnList;

    }

    public synchronized ListResourceContainer update() throws ResourceNotFoundException, IllegalActionOnResourceException {
        for (ResourceContainer current : m_resourcesContainer) {
            current.update();
        }

        return this;
    }


    public synchronized ListResourceContainer create() throws ResourceNotFoundException, IllegalActionOnResourceException {
        for (ResourceContainer current : m_resourcesContainer) {
            current.create();
        }
        return this;
    }


    public synchronized ListResourceContainer delete() throws ResourceNotFoundException, IllegalActionOnResourceException {
        for (ResourceContainer current : m_resourcesContainer) {
            current.delete();
        }
        return this;
    }

    public synchronized ListResourceContainer with(String key, Object value) {
        for (ResourceContainer current : m_resourcesContainer) {
            current.with(key, value);
        }
        return this;
    }

    public synchronized ListResourceContainer doIt() throws ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            returnResources.add(current.doIt());
        }
        return new ListResourceContainer(returnResources);
    }


}
