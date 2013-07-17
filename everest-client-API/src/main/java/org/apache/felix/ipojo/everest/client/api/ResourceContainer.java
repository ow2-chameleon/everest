package org.apache.felix.ipojo.everest.client.api;/*
 * User: Colin
 * Date: 17/07/13
 * Time: 15:01
 * 
 */


import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;

import java.util.ArrayList;
import java.util.List;

public class ResourceContainer {

    Resource m_resource;

    public ResourceContainer(Resource resource) {
        this.m_resource = resource;
    }

    public synchronized ListResourceContainer children() {

        List<Resource> childrenResources = m_resource.getResources();
        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (Resource currentResource : childrenResources) {
            returnResources.add(new ResourceContainer(currentResource));
        }

        return new ListResourceContainer(returnResources);

    }

    public synchronized ListResourceContainer relations() throws ResourceNotFoundException, IllegalActionOnResourceException {

        Path currentPath;
        List<Relation> relations = m_resource.getRelations();
        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (Relation current : relations) {
            currentPath = current.getHref();
            returnResources.add(read(currentPath.toString()));
        }
        return new ListResourceContainer(returnResources);

    }

    public synchronized ResourceContainer relation(String nameRelation) throws ResourceNotFoundException, IllegalActionOnResourceException {

        List<Relation> relations = m_resource.getRelations();

        for (Relation current : relations) {
            if (current.getName().equalsIgnoreCase(nameRelation)) {
                return read(current.getHref().toString());
            }
        }

        ///*TO DO : JETER L EXCEPTION RESSOURCE NOT FOUND
        //  throw new ResourceNotFoundException ;
        return null;
    }


    private ResourceContainer read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {

        return new ResourceContainer(EverestClientApi.m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));

    }
}
