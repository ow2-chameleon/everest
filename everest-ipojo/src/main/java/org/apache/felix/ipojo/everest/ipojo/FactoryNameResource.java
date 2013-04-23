package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Version;

import java.util.*;

/**
 * '/ipojo/factory/$name' resource, where $name stands for the name of a factory.
 */
public class FactoryNameResource extends DefaultReadOnlyResource {

    /**
     * We use our own Version comparator to handle null factory version.
     * By convention, {@code null} is below everything.
     */
    private static final Comparator<Version> VERSION_COMPARATOR = new Comparator<Version>() {
        public int compare(Version v1, Version v2) {
            if (v1 == null) {
                return (v2 != null) ? -1 : 0;
            } else if (v2 == null) {
                // v1 is not null
                return 1;
            } else {
                return v1.compareTo(v2);
            }
        }
    };

    private static Version parseVersion(String v) {
        return v == null ? null : Version.parseVersion(v);
    }

    private final SortedMap<Version, FactoryNameVersionResource> m_versions = new TreeMap<Version, FactoryNameVersionResource>(VERSION_COMPARATOR);

    public FactoryNameResource(String name) {
        super(FactoriesResource.PATH.addElements(name));
    }

    public void addFactoryVersion(Factory factory) {
        synchronized (m_versions) {
            m_versions.put(parseVersion(factory.getVersion()), new FactoryNameVersionResource(factory));
        }
    }

    public boolean removeFactoryVersion(Factory factory) {
        synchronized (m_versions) {
            m_versions.remove(parseVersion(factory.getVersion()));
            return m_versions.isEmpty();
        }
    }

    @Override
    public List<Resource> getResources() {
        synchronized (m_versions) {
            return new ArrayList<Resource>(m_versions.values());
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder();
        synchronized (m_versions) {
            for(Version v : m_versions.keySet()) {
                b.set(v == null ? null : v.toString(), m_versions.get(v).getMetadata());
            }
        }
        return b.build();
    }
}
