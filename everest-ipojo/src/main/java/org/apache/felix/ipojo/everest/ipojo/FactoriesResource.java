package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.*;

/**
 * '/ipojo/factory' resource.
 */
public class FactoriesResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("factory");

    private final Map<String, FactoryNameResource> m_factories = new LinkedHashMap<String, FactoryNameResource>();

    public FactoriesResource() {
        super(PATH);
    }

    public void addFactory(Factory factory) {
        FactoryNameResource r;
        synchronized (m_factories) {
            String name = factory.getName();
            r = m_factories.get(name);
            if (r == null) {
                r = new FactoryNameResource(name);
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

}