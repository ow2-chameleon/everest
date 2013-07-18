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

        return new ResourceContainer(EverestClientApi.m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));

    }

    public ListResourceContainer(ResourceContainer resourceContainer) {
        m_resourcesContainer.add(resourceContainer);
    }

    public ListResourceContainer(List<ResourceContainer> listresource) {
        this.m_resourcesContainer = listresource;
    }

    public synchronized ListResourceContainer childrens() throws ResourceNotFoundException {


        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer currentResourceContainer : m_resourcesContainer) {
            try {
                returnResources.addAll(currentResourceContainer.childrens().m_resourcesContainer);
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        ///*TO DO : JETER L EXCEPTION RESSOURCE NOT FOUND
        //  throw new ResourceNotFoundException ;
        return null;
    }

    public synchronized ListResourceContainer children(String name) throws ResourceNotFoundException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer currentResourceContainer : m_resourcesContainer) {
            try {
                returnResources.add(currentResourceContainer.children(name));
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        return null;
        ///*TO DO : JETER L EXCEPTION RESSOURCE NOT FOUND
        //  throw new ResourceNotFoundException ;


    }

    public synchronized ListResourceContainer relations() throws ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            try {
                returnResources.addAll(current.relations().m_resourcesContainer);
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }

        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        ///*TO DO : JETER L EXCEPTION RESSOURCE NOT FOUND
        //  throw new ResourceNotFoundException ;

        return null;
    }

    public synchronized ListResourceContainer relation(String relationName) throws ResourceNotFoundException, IllegalActionOnResourceException {

        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        for (ResourceContainer current : m_resourcesContainer) {
            try {
                returnResources.add(current.relation(relationName));
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        ///*TO DO : JETER L EXCEPTION RESSOURCE NOT FOUND
        //  throw new ResourceNotFoundException ;
        return null;
    }

    public synchronized List<String> retrieve(String metadataId) throws IllegalResourceException {

        List<String> returnList = new ArrayList<String>();

        for (ResourceContainer current : m_resourcesContainer) {
            if (current.retrieve(metadataId) != null) {
                returnList.add(current.retrieve(metadataId));
            }
        }

        if (returnList.isEmpty()) {
            return null;
        }

        ///*TO DO : JETER L EXCEPTION RESSOURCE NOT FOUND
        //  throw new ResourceNotFoundException ;
        return returnList;

    }

 /*   public synchronized <T> T retrieve(String metadataId,Class<? extends T> clazz)  {

        List<T> returnList= new ArrayList<T>();

        for (ResourceContainer current : m_resourcesContainer) {
            if (current.retrieve(metadataId) != null ){
                returnList.add(current.retrieve(metadataId,clazz));
            }
        }

        if (returnList.isEmpty()){
            return null;
        }

        return returnList;

    }     */


}
