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

package org.ow2.chameleon.everest.client;


import org.osgi.framework.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ow2.chameleon.everest.impl.DefaultRequest;
import org.ow2.chameleon.everest.services.*;
import org.ow2.chameleon.everest.core.Everest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * User: Colin
 * Date: 17/07/13
 * Time: 14:22
 *
 */

public class EverestClient extends ResourceContainer implements ServiceTrackerCustomizer,EventHandler {



    /**
     * Service of everest-core
     */
    public static EverestService m_everest;

    /**
     * path of the current resource point by the EverestClient
     */
    public volatile Path m_currentPath;


    /**
     * @return @m_currentPath
     */
    public Path getM_currentPath() {
        return m_currentPath;
    }

    /**
     * Tracker for services
     */
    private  ServiceTracker m_serviceTracker;

    /**
     * Tracker for services
     */
    private  BundleContext m_context;

    /**
     * Event Handler registration
     */
    private  ServiceRegistration m_serviceRegistration;

    private Map<EverestListener, CustomEventAdmin> m_listeners = new ConcurrentHashMap<EverestListener, CustomEventAdmin>();

    private Object m_lock = new Object();

    /**
     * @param m_everest : Need the everest core service to browse the resource tree
     */
    public EverestClient(BundleContext context, EverestService m_everest) {
        super(null);
        m_serviceTracker = null;
        m_context = context;
        try {
            m_resource = m_everest.process(new DefaultRequest(Action.READ, Path.from("/"), null));
            m_currentPath = m_resource.getPath();
        } catch (IllegalActionOnResourceException e) {

        } catch (ResourceNotFoundException e) {
        }
        this.m_everest = m_everest;
    }

    public EverestClient(EverestService m_everest) {
        super(null);
        m_serviceTracker = null;
        m_context = null;
        try {
            m_resource = m_everest.process(new DefaultRequest(Action.READ, Path.from("/"), null));
            m_currentPath = m_resource.getPath();
        } catch (IllegalActionOnResourceException e) {

        } catch (ResourceNotFoundException e) {
        }
        this.m_everest = m_everest;
    }

    public EverestClient(BundleContext context) {
        super(null);
        m_context = context;
        Filter serviceFilter = null;
        String stringFilter = "(objectclass="+EverestService.class.getName()+")";
        try {
            serviceFilter = context.createFilter(stringFilter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        m_serviceTracker = new ServiceTracker(context,serviceFilter, this);
        m_serviceTracker.open();

    }

    public EverestClient(BundleContext context,Filter filter) {
        super(null);
        m_context = context;
        String stringFilter = "(&(objectclass="+EverestService.class.getName()+")"+filter.toString()+")";
        Filter serviceFilter = null;
        try {
            serviceFilter = context.createFilter(stringFilter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        m_serviceTracker = new ServiceTracker(context,serviceFilter, this);
        m_serviceTracker.open();
    }

    /**
     * @param path : path of the resource to read
     * @return : A resource container which contain the resource you have read
     * @throws ResourceNotFoundException
     */
    public ResourceContainer read(String path) throws ResourceNotFoundException {
        try {
            this.m_resource = m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
        } catch (IllegalActionOnResourceException e) {
        }
        m_currentPath = m_resource.getPath();
        m_currentAction = Action.READ;
        m_currentParams.clear();
        return this; //new ResourceContainer(m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));
    }

    /**
     * Define the next action to UPDATE send by the doIt method.Erase the parameters.
     *
     * @param path :path of the resource to update
     * @return :
     */
    public synchronized ResourceContainer update(String path) {
        m_currentPath = Path.from(path);
        m_currentAction = Action.UPDATE;
        m_currentParams.clear();
        return this;
    }

    /**
     * Define the next action to UPDATE send by the doIt method.Erase the parameters.
     *
     * @param resource : The resource that you want to update
     * @return
     */
    public synchronized ResourceContainer update(Resource resource) {
        m_currentPath = resource.getPath();
        m_currentAction = Action.UPDATE;
        m_currentParams.clear();
        return this;
    }

    /**
     * Define the next action to CREATE send by the doIt method.Erase the parameters.
     *
     * @param path : path of the parent resource that you want to create
     * @return
     */
    public synchronized ResourceContainer create(String path) {
        m_currentPath = Path.from(path);
        m_currentAction = Action.CREATE;
        m_currentParams.clear();
        return this;
    }

    /**
     * Define the next action to CREATE send by the doIt method.Erase the parameters.
     *
     * @param resource :  The parent resource that you want to create
     * @return
     */
    public synchronized ResourceContainer create(Resource resource) {
        m_currentPath = resource.getPath();
        m_currentAction = Action.CREATE;
        m_currentParams.clear();
        return this;
    }

    /**
     * Define the next action to DELETE send by the doIt method.Erase the parameters.
     *
     * @param path : path of the resource that you want to delete
     * @return
     */
    public synchronized ResourceContainer delete(String path) {
        m_currentPath = Path.from(path);
        m_currentAction = Action.DELETE;
        m_currentParams.clear();
        return this;

    }

    /**
     * Define the next action to DELETE send by the doIt method.Erase the parameters.
     *
     * @param resource : The resource that you want to delete
     * @return
     */
    public synchronized ResourceContainer delete(Resource resource) {
        m_currentPath = resource.getPath();
        m_currentAction = Action.DELETE;
        m_currentParams.clear();
        return this;
    }

    /**
     * This method is called after called an action method (create , delete or update) and have set parameters with the
     * method with(). This method throw a request with the action define by the last call of an action method and with a set of parameters.
     *
     * @return : The resource container which contains the resource create , update , delete.
     * @throws ResourceNotFoundException
     * @throws IllegalActionOnResourceException
     *
     */
    @Override
    public synchronized ResourceContainer doIt() throws ResourceNotFoundException, IllegalActionOnResourceException {
        return new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(m_currentAction, m_currentPath, m_currentParams)));
    }

    public synchronized AssertionResource assertThat(Resource resource) {
        try {
            return new AssertionResource(resource);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        } catch (IllegalActionOnResourceException e) {
            return null;
        }
    }

    public synchronized AssertionString assertThat(String key) {
        return new AssertionString(key);

    }



    public ListResourceContainer subscribe(EverestListener everestListener, String request) {
        CustomEventAdmin customEventAdmin;
        synchronized (m_lock) {
            if (m_listeners.keySet().contains(everestListener)){
                customEventAdmin = m_listeners.get(everestListener);
            }else {
                customEventAdmin = new CustomEventAdmin(m_context,everestListener,request,this);
                m_listeners.put(everestListener, customEventAdmin);                                
            }
        }
        registerEventHandler();
        return  customEventAdmin.getM_lastRequestResult();        
    }

    public void unSubscribe(EverestListener  everestListener) {
        //defensive method
        synchronized (m_lock) {
            if (m_listeners.keySet().contains(everestListener)) {
                m_listeners.remove(everestListener);            
            }                
            if(m_listeners.isEmpty()){
                if(m_serviceRegistration != null) {
                    m_serviceRegistration.unregister();
                    m_serviceRegistration=null;
                }
            }
        }
    }


    /*
    * =================================
    * SERVICE TRACKER IMPLEMENTATION
     */
    public Object addingService(ServiceReference serviceReference) {
        if ( m_everest == null){

            Object serviceId = serviceReference.getProperty(Constants.SERVICE_ID);
            m_everest = (EverestService) m_context.getService(serviceReference);
            try {
                m_resource = m_everest.process(new DefaultRequest(Action.READ, Path.from("/"), null));
                m_currentPath = m_resource.getPath();
            } catch (IllegalActionOnResourceException e) {

            } catch (ResourceNotFoundException e) {
            }

            return serviceId;
        } else{
            return null;
        }
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        m_everest = (EverestService) serviceReference;
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        m_everest = null;
    }
    /* ================================*/


    /*
   * =================================
   * EVENT ADMIN HANDLER
    */
    public void handleEvent(Event event) {
        if ((m_listeners != null) && (!m_listeners.isEmpty())){
            Object eventType = event.getProperty("eventType");
            for(EverestListener currentListener : m_listeners.keySet()){
                CustomEventAdmin cea = m_listeners.get(currentListener);
                if(cea != null){
                if (ResourceEvent.CREATED.toString().equals(eventType)) {
                    cea.CreateEvent();

                } else if (ResourceEvent.DELETED.toString().equals(eventType)) {
                    cea.DeleteEvent();

                } else if (ResourceEvent.UPDATED.toString().equals(eventType)) {
                    cea.UpdateEvent();
                }
            }
            }
        }
    }

    protected void registerEventHandler() {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        String topic = Everest.topicFromPath(m_currentPath);
        String childTopic = topic.endsWith("/") ? topic.concat("*") :  topic.concat("/*");
        props.put(EventConstants.EVENT_TOPIC, new  String[]{ topic, childTopic });
        if(m_serviceRegistration==null){
            m_serviceRegistration = m_context.registerService(EventHandler.class.getName(), this, props);
        }else{
            m_serviceRegistration.setProperties(props);
        }
    }
    /*================================
    */


    public List<Resource> getAllResource() {
        List<Resource> list = new ArrayList<Resource>();
        try {            
            Resource root = read(m_currentPath.toString()).retrieve();
            list = root.getResources( new ResourceFilter() {
                public boolean accept(Resource r) {
                    return true;
                }
            });
        }catch (ResourceNotFoundException e) {            
            // this could happen
        }
        return list;
    }

    public boolean isEverestService(){
        if( m_everest == null){
            return false;
        }else {
            return true;
        }
    }

    public EverestService getM_everest() {
        return m_everest;
    }
}
