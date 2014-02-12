package org.ow2.chameleon.everest.client.casaTest.zone;


import org.ow2.chameleon.everest.client.casaTest.AbstractResourceCollection;
import org.ow2.chameleon.everest.client.casaTest.services.Zone;
import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ow2.chameleon.everest.client.casaTest.TestRootResource.m_casaRootPath;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 11/07/13
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class ZoneManager extends AbstractResourceCollection {

    public static final String m_zoneName = "zone";

    public static final Path m_zonePath = m_casaRootPath.add(Path.from(Path.SEPARATOR + m_zoneName));


    /**
     * A MODIF
     */
    private Map<String, ZoneResource> m_zoneResourcesMap = new HashMap<String, ZoneResource>();

    /**
     * Static instance of this singleton class
     */
    private static final ZoneManager m_instance = new ZoneManager();

    /**
     * Map of Generic Device resource by serial Number
     */
    private Map<String, String> m_genericDeviceByZone = new HashMap<String, String>();


    public static ZoneManager getInstance() {
        return m_instance;
    }


    public ZoneManager() {
        super(m_zonePath);

        setRelations(
                new DefaultRelation(getPath(), Action.CREATE, "create",
                        new DefaultParameter()
                                .name("zone/NameOfZOne")
                                .description("zone/NameOfZOne")
                                .optional(false)
                                .type(Map.class)));

        ZoneResource resource = null;
        Zone newZone = new Zone("room1");
        resource = new ZoneResource(newZone, this);
        m_zoneResourcesMap.put(newZone.getName(), resource);

        newZone = new Zone("room2");
        resource = new ZoneResource(newZone, this);
        m_zoneResourcesMap.put(newZone.getName(), resource);

        newZone = new Zone("room3");
        resource = new ZoneResource(newZone, this);
        m_zoneResourcesMap.put(newZone.getName(), resource);

    }


    @Override
    public List<Resource<?>> getResources() {
        List<Resource<?>> resources = new ArrayList<Resource<?>>();
        for (String key : m_zoneResourcesMap.keySet()) {
            resources.add(m_zoneResourcesMap.get(key));
        }
        return resources;

    }


    public Resource create(Request request) {
        ZoneResource resource = null;


        Map<String, ?> newMap = request.parameters();
        if (newMap != null) {
            for (String key : newMap.keySet()) {
                if (key.equalsIgnoreCase("zone")) {
                    Zone newZone = new Zone(newMap.get(key).toString());
                    resource = new ZoneResource(newZone, this);
                    m_zoneResourcesMap.put(newZone.getName(), resource);
                }

            }
        }
        return resource;
    }

    public void setChildLocation(String serialNumber, String Location) {
        if (m_genericDeviceByZone.get(serialNumber) != null) {
            m_zoneResourcesMap.get(m_genericDeviceByZone.get(serialNumber)).deleteDeviceLocation(serialNumber);
        }
        m_genericDeviceByZone.put(serialNumber, Location);
        for (String key : m_zoneResourcesMap.keySet()) {
            if (m_zoneResourcesMap.get(key).getPath().getLast().toString().equalsIgnoreCase(Location)) {
                m_zoneResourcesMap.get(key).setDeviceLocation(serialNumber);
            }
        }
    }

    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Name", m_zoneName);
        metadataBuilder.set("Path", m_zonePath);
        return metadataBuilder.build();
    }

    public void deleteresource(String key) {
        m_zoneResourcesMap.remove(key);
    }


}
