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
import org.ow2.chameleon.everest.casa.device.GenericDeviceManager;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Relation;
import org.ow2.chameleon.everest.services.ResourceMetadata;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/07/13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class ZoneResource extends AbstractResourceCollection {

    /**
     * Represented zone
     */
    private final Zone m_zone;


    /**
     * Reference the manager
     */
    private final ZoneManager m_ZoneManager;


    public ZoneResource(Zone m_zone, ZoneManager m_zoneManager) {
        super(m_zoneManager.m_zonePath.add(Path.from(Path.SEPARATOR + m_zone.getName())));
        this.m_zone = m_zone;
        this.m_ZoneManager = m_zoneManager;
    }

    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Name", m_zone.getName());
        metadataBuilder.set("Luminosity", m_zone.getM_luminosity());
        return metadataBuilder.build();
    }

    public void deleteDeviceLocation(String serialNumber) {
        List<Relation> relations;
        for (Relation current : getRelations()) {
            if (current.getName().equalsIgnoreCase("device" + serialNumber)) {
                relations = getRelations();
                relations.remove(current);
                setRelations(relations);
            }
        }
    }

    public void setDeviceLocation(String serialNumber) {
        List<Relation> relations;
        relations = getRelations();
        relations.add(new DefaultRelation(GenericDeviceManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + serialNumber)), Action.READ, "device" + serialNumber));
        setRelations(relations);
    }

}
