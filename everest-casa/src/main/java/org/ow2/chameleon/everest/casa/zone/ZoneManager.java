/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.casa.zone;

import org.ow2.chameleon.everest.casa.AbstractResourceCollection;
import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Request;
import org.ow2.chameleon.everest.services.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ow2.chameleon.everest.casa.CasaRootResource.m_casaRootPath;

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
     * A MODIFF
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
    }


    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<Resource>();
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
                    m_zoneResourcesMap.put(newZone.name, resource);
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


}
