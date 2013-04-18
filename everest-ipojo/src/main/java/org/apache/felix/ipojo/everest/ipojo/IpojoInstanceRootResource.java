package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;

import java.util.HashMap;
import java.util.Map;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.IPOJO_ROOT_PATH;

/**
 * Resource representation for all iPOJO instances.
 */
public class IpojoInstanceRootResource extends DefaultReadOnlyResource {

    /**
     * The path of the iPOJO instance resource manager.
     */
    public static final Path IPOJO_INSTANCE_ROOT_PATH = IPOJO_ROOT_PATH.add(Path.from("/instance"));

    /**
     * The iPOJO instances.
     */
    private final Map<String, Architecture> m_instances = new HashMap<String, Architecture>();

    public IpojoInstanceRootResource() {
        super(IPOJO_INSTANCE_ROOT_PATH);
    }

    /**
     * Add an iPOJO instance.
     * @param instance the arriving iPOJO instance
     */
    void addInstance(Architecture instance) {
        synchronized (m_instances) {
            m_instances.put(instance.getInstanceDescription().getName(), instance);
        }
    }

    /**
     * Remove an iPOJO instance.
     * @param instance the leaving iPOJO instance
     */
    void removeInstance(Architecture instance) {
        synchronized (m_instances) {
            m_instances.remove(instance.getInstanceDescription().getName());
        }
    }

}
