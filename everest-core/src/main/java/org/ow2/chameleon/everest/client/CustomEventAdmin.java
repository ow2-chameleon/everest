package org.ow2.chameleon.everest.client;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.ow2.chameleon.everest.filters.ResourceFilters;
import org.ow2.chameleon.everest.query.QueryFilter;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceFilter;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: colin
 * Date: 02/09/13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class CustomEventAdmin  {

    BundleContext m_context;

    private EverestListener m_everestListener;

    private String m_request;

    private ResourceFilter m_currentFilter;

    private EverestClient m_parentClient;

    private ListResourceContainer m_lastRequestResult;

    public CustomEventAdmin(BundleContext context,EverestListener everestListener , String request,EverestClient client) {
        this.m_context = context;
        this.m_everestListener = everestListener;
        this.m_request = request;
        m_parentClient = client;
        try{
            QueryFilter queryFilter = new QueryFilter(request,EverestClient.m_everest);
            m_currentFilter = queryFilter.input();
        }catch (Exception e){
            e.printStackTrace();
            m_currentFilter = ResourceFilters.none();
        }

        List<Resource> resourceList = m_parentClient.getAllResource();
        List<ResourceContainer> resourceContainers = new ArrayList<ResourceContainer>();
        for(Resource resource : resourceList){
            resourceContainers.add(new ResourceContainer(resource));
        }
        ListResourceContainer listResourceContainer = new ListResourceContainer(resourceContainers);
        m_lastRequestResult = listResourceContainer.filter(m_currentFilter);
    }

    protected void CreateEvent() {
        List<Resource> resourceList = m_parentClient.getAllResource();
        List<ResourceContainer> resourceContainers = new ArrayList<ResourceContainer>();
        for(Resource resource : resourceList){
            resourceContainers.add(new ResourceContainer(resource));
        }
        ListResourceContainer listResourceContainer = new ListResourceContainer(resourceContainers);
        listResourceContainer.filter(m_currentFilter);
        compareResult(listResourceContainer);
    }

    protected void UpdateEvent() {
        CreateEvent();
    }

    protected void DeleteEvent() {
        CreateEvent();
    }

    protected void registerEventHandler(String... topics) {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, topics);
        System.out.println(" this " + this);
        System.out.println(" PROPS " + EventHandler.class.getName());
        System.out.println(" props " + props.toString());
        m_context.registerService(EventHandler.class.getName(), this, props);
    }


    public EverestListener getM_everestListener() {
        return m_everestListener;
    }

    public String getM_request() {
        return m_request;
    }

    public ResourceFilter getM_currentFilter() {
        return m_currentFilter;
    }

    public ListResourceContainer getM_lastRequestResult() {
        return m_lastRequestResult;
    }


    public void compareResult(ListResourceContainer resultRequest) {
        if((resultRequest.retrieve() == null ) && (m_lastRequestResult.retrieve() != null )){
            m_everestListener.getNewResult(null);
            m_lastRequestResult = resultRequest;

        }else if((resultRequest.retrieve() == null) && (m_lastRequestResult.retrieve() == null )){

        }else if((resultRequest.retrieve() != null) && (m_lastRequestResult.retrieve() == null)){
            m_everestListener.getNewResult(resultRequest.retrieve());
            m_lastRequestResult = resultRequest;

        }else{

            if(!m_lastRequestResult.equals(resultRequest)){
                m_lastRequestResult = resultRequest;
                m_everestListener.getNewResult(resultRequest.retrieve());

            }else{
                m_lastRequestResult = resultRequest;

            }
        }
    }
}
