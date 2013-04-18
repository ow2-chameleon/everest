package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Version;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.IPOJO_ROOT_PATH;

/**
 * Resource representation for all iPOJO factories.
 */
public class IpojoFactoryRootResource extends DefaultReadOnlyResource {

    /**
     * The path of the iPOJO factory resource manager.
     */
    public static final Path IPOJO_FACTORY_ROOT_PATH = IPOJO_ROOT_PATH.add(Path.from("/factory"));

    /**
     * The iPOJO component factories.
     */
    private final Map<String, SortedMap<Version, Factory>> m_factories = new HashMap<String, SortedMap<Version, Factory>>();

    public IpojoFactoryRootResource() {
        super(IPOJO_FACTORY_ROOT_PATH);
    }

    /**
     * Add an iPOJO factory.
     * @param factory the arriving iPOJO factory
     */
    void addFactory(Factory factory) {
        synchronized (m_factories) {
            String name = factory.getName();
            SortedMap<Version, Factory> versions = m_factories.get(name);
            if (versions == null) {
                versions = new TreeMap<Version, Factory>();
                m_factories.put(name, versions);
            }
            Version version = Version.parseVersion(factory.getVersion());
            versions.put(version, factory);
        }
    }

    /**
     * Remove an iPOJO factory.
     * @param factory the leaving iPOJO factory
     */
    void removeFactory(Factory factory) {
        synchronized (m_factories) {
            String name = factory.getName();
            SortedMap<Version, Factory> versions = m_factories.get(name);
            Version version = Version.parseVersion(factory.getVersion());
            versions.remove(version);
            if (versions.isEmpty()) {
                m_factories.remove(name);
            }
        }
    }

}
