package org.apache.felix.ipojo.everest.system.mx;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:25 PM
 */
public class MXResourceManager extends DefaultReadOnlyResource {

    public static final String MX_NAME = "mx";

    public static final Path MX_PATH = SYSTEM_ROOT_PATH.addElements(MX_NAME);

    private RuntimeMxResource m_runtimeMxResource;

    private OperatingSystemMxResource m_operatingSystemMxResource;

    private ThreadMxResource m_threadMxResource;

    private MemoryMxResource m_memoryMxResource;

    public MXResourceManager() {
        super(MX_PATH);
        m_runtimeMxResource = RuntimeMxResource.getInstance();
        m_operatingSystemMxResource = OperatingSystemMxResource.getInstance();
        m_threadMxResource = ThreadMxResource.getInstance();
        m_memoryMxResource = MemoryMxResource.getInstance();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(m_operatingSystemMxResource);
        resources.add(m_runtimeMxResource);
        resources.add(m_threadMxResource);
        resources.add(m_memoryMxResource);
        return resources;
    }

    public List<Relation> getRelations() {
        List<Relation> relations = new ArrayList<Relation>();
        relations.addAll(super.getRelations());
        for (Resource resource : getResources()) {
            int size = getCanonicalPath().getCount();
            String name = resource.getCanonicalPath().getElements()[size];
            relations.add(new DefaultRelation(resource.getCanonicalPath(), Action.READ, getCanonicalPath().getLast() + ":" + name,
                    "Get " + name));
        }
        return relations;
    }
}
