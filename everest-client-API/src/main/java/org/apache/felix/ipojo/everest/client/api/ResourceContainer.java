package org.apache.felix.ipojo.everest.client.api;/*
 * User: Colin
 * Date: 17/07/13
 * Time: 15:01
 * 
 */


import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceContainer {

    protected Resource m_resource;

    protected Action m_currentAction = Action.READ;

    public Map<String, Object> getM_currentParams() {
        return m_currentParams;
    }

    public Action getM_currentAction() {
        return m_currentAction;
    }

    protected Map<String, Object> m_currentParams = new HashMap<String, Object>();

    public ResourceContainer(Resource resource) {
        this.m_resource = resource;
    }

    public synchronized ResourceContainer parent() throws RuntimeException, ResourceNotFoundException, IllegalActionOnResourceException {

        if (m_resource.getPath().toString().equalsIgnoreCase("/")) {

            throw new RuntimeException();
        } else {

            return read(m_resource.getPath().getParent().toString());
        }
    }

    public synchronized ListResourceContainer children() throws RuntimeException {

        List<Resource> childrenResources = m_resource.getResources();
        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (Resource currentResource : childrenResources) {
            returnResources.add(new ResourceContainer(currentResource));
        }
        ///*TO DO : JETER L EXCEPTION RESSOURCE NOT FOUND
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        throw new RuntimeException();

    }

    public synchronized ResourceContainer child(String name) throws RuntimeException {

        List<Resource> childrenResources = m_resource.getResources();

        for (Resource currentResource : childrenResources) {
            if (currentResource.getPath().getLast().equalsIgnoreCase(name)) {
                return new ResourceContainer(currentResource);
            }
        }
        throw new RuntimeException();

    }

    public synchronized ListResourceContainer relations() throws RuntimeException, ResourceNotFoundException, IllegalActionOnResourceException {

        Path currentPath;
        List<Path> listPath = new ArrayList<Path>();
        List<Relation> relations = m_resource.getRelations();
        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (Relation current : relations) {
            currentPath = current.getHref();

            if (!(listPath.contains(currentPath))) {
                listPath.add(currentPath);
                returnResources.add(read(currentPath.toString()));
            }
        }

        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        throw new RuntimeException();
    }

    public synchronized ResourceContainer relation(String nameRelation) throws RuntimeException, ResourceNotFoundException, IllegalActionOnResourceException {

        List<Relation> relations = m_resource.getRelations();

        for (Relation current : relations) {
            if (current.getName().equalsIgnoreCase(nameRelation)) {
                return read(current.getHref().toString());
            }
        }

        throw new RuntimeException();
    }

    public synchronized Resource retrieve() {
        return this.m_resource;
    }


    public synchronized String retrieve(String metadataId) {

        ResourceMetadata resourceMetadata = m_resource.getMetadata();
        for (String current : resourceMetadata.keySet()) {
            if (current.equalsIgnoreCase(metadataId)) {
                return resourceMetadata.get(current).toString();
            }
        }
        return null;
    }

    public synchronized <T> T retrieve(String metadataId, Class<? extends T> clazz) {

        ResourceMetadata resourceMetadata = m_resource.getMetadata();
        for (String current : resourceMetadata.keySet()) {
            if (current.equalsIgnoreCase(metadataId)) {
                return resourceMetadata.get(metadataId, clazz);
            }
        }
        return null;
    }


    private ResourceContainer read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {

        return new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));
    }


    public synchronized ResourceContainer update() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentAction = Action.UPDATE;
        m_currentParams.clear();
        return this;
    }


    public synchronized ResourceContainer create() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentAction = Action.CREATE;
        m_currentParams.clear();
        return this;
    }


    public synchronized ResourceContainer delete() throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentAction = Action.DELETE;
        m_currentParams.clear();
        return this;

    }

    public synchronized ResourceContainer with(String key, Object value) {
        m_currentParams.put(key, value);
        return this;
    }

    public synchronized ResourceContainer doIt() throws ResourceNotFoundException, IllegalActionOnResourceException {
        return new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(m_currentAction, m_resource.getPath(), m_currentParams)));
    }


    public synchronized ResourceContainer filter(ResourceFilter filter) {
        if (filter.accept(m_resource)) {
            return this;
        }

        throw new RuntimeException();
    }
}
