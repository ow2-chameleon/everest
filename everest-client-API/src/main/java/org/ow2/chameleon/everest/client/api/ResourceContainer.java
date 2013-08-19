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
 * Date: 17/07/13
 * Time: 15:01
 * 
 */


import org.ow2.chameleon.everest.impl.DefaultRequest;
import org.ow2.chameleon.everest.services.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceContainer {

    /**
     * resource represented by the resourceContainer
     */
    protected Resource m_resource;

    /**
     * The action which be called by the doIt method. By default READ.
     */
    protected Action m_currentAction = Action.READ;


    /**
     * map of parameter use by the method doIt
     */
    protected Map<String, Object> m_currentParams = new HashMap<String, Object>();

    /**
     * Getter of m_currentParams
     *
     * @return
     */
    public Map<String, Object> getM_currentParams() {
        return m_currentParams;
    }

    /**
     * Getter of m_currentAction
     *
     * @return
     */
    public Action getM_currentAction() {
        return m_currentAction;
    }


    public ResourceContainer(Resource resource) {
        this.m_resource = resource;
    }

    /**
     * Method to get the parent of m_resource
     *
     * @return : A resource container which represent the parent resource
     * @throws ResourceNotFoundException
     */
    public synchronized ResourceContainer parent() throws ResourceNotFoundException {
        if (m_resource == null) {
            return this;
        }

        if (m_resource.getPath().toString().equalsIgnoreCase("/")) {
            m_resource = null;
            return this;
        } else {

            return read(m_resource.getPath().getParent().toString());
        }
    }

    /**
     * Get the list of all child of a resource
     *
     * @return Null if the resource haven't children
     */
    public synchronized ListResourceContainer children() {
        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();
        if (m_resource == null) {
            returnResources = null;
            return new ListResourceContainer(returnResources);
        }

        List<Resource> childrenResources = m_resource.getResources();


        for (Resource currentResource : childrenResources) {
            returnResources.add(new ResourceContainer(currentResource));
        }

        if (!(returnResources.isEmpty())) {
            return new ListResourceContainer(returnResources);
        }

        returnResources = null;
        return new ListResourceContainer(returnResources);

    }

    /**
     * Get a child identified by the name.
     *
     * @param name : Name of the child resource that you want to get
     * @return
     */
    public synchronized ResourceContainer child(String name) {
        if (m_resource == null) {
            return this;
        }

        List<Resource> childrenResources = m_resource.getResources();

        for (Resource currentResource : childrenResources) {
            if (currentResource.getPath().getLast().equalsIgnoreCase(name)) {
                this.m_resource = currentResource;
                return this;
            }
        }
       this.m_resource = null;
        return this;
    }

    /**
     * Get all the resource in relation with m_resource
     *
     * @return
     * @throws ResourceNotFoundException
     */
    public synchronized ListResourceContainer relations() throws ResourceNotFoundException {
        List<ResourceContainer> returnResources = new ArrayList<ResourceContainer>();

        if (m_resource == null) {
            returnResources = null;
            return new ListResourceContainer(returnResources);
        }
        Path currentPath;
        List<Path> listPath = new ArrayList<Path>();
        List<Relation> relations = m_resource.getRelations();

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

        returnResources = null;
        return new ListResourceContainer(returnResources);

    }

    /**
     * Get the resource in relation with m_resource with the nameRelation
     *
     * @param nameRelation : name of the relation
     * @return
     * @throws ResourceNotFoundException
     */
    public synchronized ResourceContainer relation(String nameRelation) throws ResourceNotFoundException {

        if (m_resource == null) {
            return this;
        }
        List<Relation> relations = m_resource.getRelations();

        for (Relation current : relations) {
            if (current.getName().equalsIgnoreCase(nameRelation)) {
                return read(current.getHref().toString());
            }
        }
        m_resource = null;
        return this;
    }

    /**
     * @return The current resource represented by the ResourceContainer
     */
    public synchronized Resource retrieve() {
        if (m_resource == null) {
            return null;
        }
        return this.m_resource;
    }


    /**
     * Get a metadata identified by his ID at the string format
     *
     * @param metadataId : Id of the Metadata
     * @return
     */
    public synchronized String retrieve(String metadataId) {
        if (m_resource == null) {
            System.out.println("RESOURCE NULL");
            return null;
        }
        ResourceMetadata resourceMetadata = m_resource.getMetadata();
        for (String current : resourceMetadata.keySet()) {
            System.out.println("METADATA CURRENT " + current + " VALUE " + resourceMetadata.get(current));
            if (current.equalsIgnoreCase(metadataId)) {
                return resourceMetadata.get(current).toString();
            }
        }
        return null;
    }

    /**
     * Get a metadata identified by his ID at the T format
     *
     * @param metadataId : Id of the Metadata
     * @param clazz      : Class of the value of the metadata
     * @param <T>
     * @return
     */
    public synchronized <T> T retrieve(String metadataId, Class<? extends T> clazz) {
        if (m_resource == null) {
            return null;
        }
        ResourceMetadata resourceMetadata = m_resource.getMetadata();
        for (String current : resourceMetadata.keySet()) {
            if (current.equalsIgnoreCase(metadataId)) {
                return resourceMetadata.get(metadataId, clazz);
            }
        }
        return null;
    }


    private ResourceContainer read(String path) throws ResourceNotFoundException {
        ResourceContainer resourceContainer = null;
        try {
            resourceContainer = new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));
        } catch (IllegalActionOnResourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return resourceContainer;
    }

    /**
     * Define the next action to UPDATE send by the doIt method.Erase the parameters.
     *
     * @return
     */
    public synchronized ResourceContainer update() {
        m_currentAction = Action.UPDATE;
        m_currentParams.clear();
        return this;
    }

    /**
     * Define the next action to CREATE send by the doIt method.Erase the parameters.
     *
     * @return
     */
    public synchronized ResourceContainer create() {
        m_currentAction = Action.CREATE;
        m_currentParams.clear();
        return this;
    }

    /**
     * Define the next action to DELETE send by the doIt method.Erase the parameters.
     *
     * @return
     */
    public synchronized ResourceContainer delete() {
        m_currentAction = Action.DELETE;
        m_currentParams.clear();
        return this;

    }

    /**
     * Define the parameters send by the doIt method. each called to with() put a new <key,value> in m_currentParams.
     *
     * @return
     */
    public synchronized ResourceContainer with(String key, Object value) {
        m_currentParams.put(key, value);
        return this;
    }

    /**
     * This method is called after called an action method (create , delete or update) and have set parameters with the
     * method with(). This method throw a request on m_resource with the action define by the last call of an action method and with a set of parameters.
     *
     * @return : The resource container which contains the resource create , update , delete.
     * @throws ResourceNotFoundException
     * @throws IllegalActionOnResourceException
     *
     */
    public synchronized ResourceContainer doIt() throws ResourceNotFoundException, IllegalActionOnResourceException {
        if (m_resource == null) {
            return this;
        }
        return new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(m_currentAction, m_resource.getPath(), m_currentParams)));
    }


    public synchronized ResourceContainer filter(ResourceFilter filter) {
        if (m_resource == null) {
            return this;
        }
        if (filter.accept(m_resource)) {
            return this;
        }

        m_resource = null;
        return this;
    }
}
