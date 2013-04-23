package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * '/ipojo/instance' resource.
 */
public class InstancesResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("instance");

    private final Map<String, InstanceNameResource> m_instances = new LinkedHashMap<String, InstanceNameResource>();

    public InstancesResource() {
        super(PATH);
    }

    void addInstance(Architecture instance) {
        synchronized (m_instances) {
            m_instances.put(instance.getInstanceDescription().getName(), new InstanceNameResource(instance));
        }
    }

    void removeInstance(Architecture instance) {
        synchronized (m_instances) {
            m_instances.remove(instance.getInstanceDescription().getName()).setStale();
        }
    }

    @Override
    public List<Resource> getResources() {
        synchronized (m_instances) {
            return new ArrayList<Resource>(m_instances.values());
        }
    }

}
