package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.Factory;
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
 * '/ipojo/factory' resource.
 */
public class FactoriesResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("factory");

    /**
     * The enclosing iPOJO resource.
     */
    private final IpojoResource m_ipojo;

    private final Map<String, FactoryNameResource> m_factories = new LinkedHashMap<String, FactoryNameResource>();

    public FactoriesResource(IpojoResource ipojo) {
        super(PATH);
        m_ipojo = ipojo;
    }

    public void addFactory(Factory factory) {
        FactoryNameResource r;
        synchronized (m_factories) {
            String name = factory.getName();
            r = m_factories.get(name);
            if (r == null) {
                r = new FactoryNameResource(m_ipojo, name);
                m_factories.put(name, r);
            }
        }
        r.addFactoryVersion(factory);
    }

    public void removeFactory(Factory factory) {
        synchronized (m_factories) {
            String name = factory.getName();
            FactoryNameResource r = m_factories.get(name);
            if (r.removeFactoryVersion(factory)) {
                m_factories.remove(name);
            }
        }
    }

    @Override
    public List<Resource> getResources() {
        synchronized (m_factories) {
            return new ArrayList<Resource>(m_factories.values());
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder();
        synchronized (m_factories) {
            // For each factory name...
            for (String name : m_factories.keySet()) {
                b.set(name, m_factories.get(name).getMetadata());
            }
        }
        return b.build();
    }
}
