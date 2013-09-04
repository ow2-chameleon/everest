package org.ow2.chameleon.everest.client;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.ow2.chameleon.everest.filters.ResourceFilters;
import org.ow2.chameleon.everest.query.QueryFilter;
import org.ow2.chameleon.everest.services.ResourceEvent;
import org.ow2.chameleon.everest.services.ResourceFilter;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: colin
 * Date: 02/09/13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class CustomEventAdmin  {

    BundleContext m_context;

    private EverestListener everestListener;

    private String request;


    private ResourceFilter m_currentFilter;

    public CustomEventAdmin(BundleContext context,EverestListener everestListener , String request) {
        this.m_context = context;
        this.everestListener = everestListener;
        this.request = request;
        try{
            QueryFilter queryFilter = new QueryFilter(request,EverestClient.m_everest);
            m_currentFilter = queryFilter.input();
        }catch (Exception e){
            e.printStackTrace();
            m_currentFilter = ResourceFilters.none();
        }
    }



    protected void registerEventHandler(String... topics) {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, topics);
        System.out.println(" this " + this);
        System.out.println(" PROPS " + EventHandler.class.getName());
        System.out.println(" props " + props.toString());
        m_context.registerService(EventHandler.class.getName(), this, props);
    }


    public EverestListener getEverestListener() {
        return everestListener;
    }

    public String getRequest() {
        return request;
    }

    public ResourceFilter getM_currentFilter() {
        return m_currentFilter;
    }


}
