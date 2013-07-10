package org.apache.felix.ipojo.everest.casa.device;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.services.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.casa.CasaRootRessource.m_casaRootPath;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 09/07/13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class GenericDeviceManager extends DefaultReadOnlyResource {


    public static final String m_genericDeviceName = "devices";

    public static final Path m_genericDevicePath = m_casaRootPath.add(Path.from(Path.SEPARATOR + m_genericDeviceName));


    /**
     * Map of bundle resource by bundle id
     */
    private Map<String, GenericDeviceRessource> m_genericDeviceResourcesMap = new HashMap<String, GenericDeviceRessource>();

    /**
     * Static instance of this singleton class
     */
    private static final GenericDeviceManager m_instance = new GenericDeviceManager();

    /**
     * Getter of the static instance of this singleton class
     *
     * @return the singleton static instance
     */
    public static GenericDeviceManager getInstance() {
        return m_instance;
    }

    public GenericDeviceManager() {
        super(m_genericDevicePath);
        setRelations(
                new DefaultRelation(getPath(), Action.CREATE, "create",
                        new DefaultParameter()
                                .name("serialNumber")
                                .description(" Serial number of the device")
                                .optional(false)
                                .type(String.class)));

    }

    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<Resource>();
        for (String key : m_genericDeviceResourcesMap.keySet()) {
            resources.add(m_genericDeviceResourcesMap.get(key));
        }
        return resources;

    }

    public Resource create(Request request) {
        GenericDeviceRessource resource = null;
        String newserialNumber = request.get("serialNumber", String.class);
        if (newserialNumber != null) {
            GenericDevice newGenericDevice = new GenericDevice(newserialNumber);
            resource = new GenericDeviceRessource(newGenericDevice, this);
            m_genericDeviceResourcesMap.put(newGenericDevice.DEVICE_SERIAL_NUMBER, resource);
        }

        return resource;
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
