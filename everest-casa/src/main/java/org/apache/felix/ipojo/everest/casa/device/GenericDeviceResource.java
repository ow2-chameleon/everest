package org.apache.felix.ipojo.everest.casa.device;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 09/07/13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class GenericDeviceResource extends DefaultResource {


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
        new DefaultRelation(getPath(), Action.UPDATE, "Update field",
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
        metadataBuilder.set("Serial Unknown", m_genericDevice.STATE_UNKNOWN);
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
            }
        }
        return this;
    }
}
