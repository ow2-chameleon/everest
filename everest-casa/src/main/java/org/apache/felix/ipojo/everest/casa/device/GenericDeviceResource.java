package org.apache.felix.ipojo.everest.casa.device;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

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


}
