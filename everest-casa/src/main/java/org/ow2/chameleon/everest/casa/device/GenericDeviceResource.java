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

package org.ow2.chameleon.everest.casa.device;

import org.ow2.chameleon.everest.casa.AbstractResourceCollection;
import org.ow2.chameleon.everest.casa.zone.ZoneManager;
import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 09/07/13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class GenericDeviceResource extends AbstractResourceCollection {


    /**
     * Represented bundle
     */
    private final GenericDevice m_genericDevice;


    /**
     * Reference to bundle resource manager
     */
    private final GenericDeviceManager m_genericDeviceManager;


    public GenericDeviceResource(GenericDevice genericDevice, GenericDeviceManager genericDeviceManager) {

        super(genericDeviceManager.m_genericDevicePath.add(Path.from(Path.SEPARATOR + genericDevice.DEVICE_SERIAL_NUMBER)));
        this.m_genericDevice = genericDevice;
        this.m_genericDeviceManager = genericDeviceManager;
        new DefaultRelation(genericDeviceManager.m_genericDevicePath.add(Path.from(Path.SEPARATOR + genericDevice.DEVICE_SERIAL_NUMBER)), Action.UPDATE, "Update field",
                new DefaultParameter()
                        .name("parameter/value")
                        .description(" Modify the parameter of the device with the value")
                        .optional(false)
                        .type(Map.class));

    }

    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Serial Number", m_genericDevice.DEVICE_SERIAL_NUMBER);
        metadataBuilder.set("State Activated", m_genericDevice.STATE_ACTIVATED);
        metadataBuilder.set("State Deactivated", m_genericDevice.STATE_DEACTIVATED);
        metadataBuilder.set("State Property Name", m_genericDevice.STATE_PROPERTY_NAME);
        metadataBuilder.set("State Unknown", m_genericDevice.STATE_UNKNOWN);
        return metadataBuilder.build();
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        //Map<String, String> newMap = request.get("configuration", Map.class);
        Map<String, ?> newMap = request.parameters();
        if (newMap != null) {
            for (String key : newMap.keySet()) {
                if (key.contentEquals("STATE_DEACTIVATED"))
                    m_genericDevice.setSTATE_DEACTIVATED(newMap.get(key).toString());
                else if (key.contentEquals("zone")) {
                    m_genericDevice.zone = newMap.get(key).toString();
                    Relation relations = new DefaultRelation(ZoneManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + m_genericDevice.zone)), Action.READ, "Location");
                    setRelations(relations);

                    ZoneManager.getInstance().setChildLocation(m_genericDevice.DEVICE_SERIAL_NUMBER, newMap.get(key).toString());
                }
            }
        }
        return this;
    }
}
