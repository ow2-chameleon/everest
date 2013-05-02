package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

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

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder();
        synchronized (m_instances) {
            // For each instance name...
            for (String name : m_instances.keySet()) {
                b.set(name, m_instances.get(name).getMetadata());
            }
        }
        return b.build();
    }

    @Override
    public List<Relation> getRelations() {
        // TODO aggregate relations of m_instances
        return super.getRelations();
    }
    
}
