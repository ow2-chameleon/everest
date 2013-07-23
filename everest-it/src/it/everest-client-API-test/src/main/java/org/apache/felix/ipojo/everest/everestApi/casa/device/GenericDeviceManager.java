package org.apache.felix.ipojo.everest.everestApi.casa.device;

import org.apache.felix.ipojo.everest.everestApi.casa.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.everestApi.casa.TestRootResource.m_casaRootPath;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 09/07/13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class GenericDeviceManager extends AbstractResourceCollection {

    /**
     * Name of the resource manager
     */
    public static final String m_genericDeviceName = "devices";

    /**
     * Path of the resource manager, here /org.apache.felix.ipojo.everest.everestApi.casa/devices
     */
    public static final Path m_genericDevicePath = m_casaRootPath.add(Path.from(Path.SEPARATOR + m_genericDeviceName));


    /**
     * Map of Generic Device resource by serial Number
     */
    private Map<String, GenericDeviceResource> m_genericDeviceResourcesMap = new HashMap<String, GenericDeviceResource>();


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

        GenericDeviceResource resource = null;
        GenericDevice newGenericDevice = new GenericDevice("1");
        resource = new GenericDeviceResource(newGenericDevice, this);
        m_genericDeviceResourcesMap.put(newGenericDevice.DEVICE_SERIAL_NUMBER, resource);

        newGenericDevice = new GenericDevice("2");
        resource = new GenericDeviceResource(newGenericDevice, this);
        m_genericDeviceResourcesMap.put(newGenericDevice.DEVICE_SERIAL_NUMBER, resource);

        newGenericDevice = new GenericDevice("3");
        resource = new GenericDeviceResource(newGenericDevice, this);
        m_genericDeviceResourcesMap.put(newGenericDevice.DEVICE_SERIAL_NUMBER, resource);
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
        GenericDeviceResource resource = null;
        String newserialNumber = request.get("serialNumber", String.class);
        if (newserialNumber != null) {
            GenericDevice newGenericDevice = new GenericDevice(newserialNumber);
            resource = new GenericDeviceResource(newGenericDevice, this);
            m_genericDeviceResourcesMap.put(newGenericDevice.DEVICE_SERIAL_NUMBER, resource);
        }

        return resource;
    }

    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Name", m_genericDeviceName);
        metadataBuilder.set("Path", m_genericDevicePath);
        return metadataBuilder.build();
    }

    public void deleteresource(String key) {
        m_genericDeviceResourcesMap.remove(key);
    }
}
