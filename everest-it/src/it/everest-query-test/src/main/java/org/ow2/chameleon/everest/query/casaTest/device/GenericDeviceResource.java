package org.ow2.chameleon.everest.query.casaTest.device;

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.query.casaTest.AbstractResourceCollection;
import org.ow2.chameleon.everest.query.casaTest.zone.ZoneManager;
import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.util.ArrayList;
import java.util.List;
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
        List<Relation> relations = new ArrayList<Relation>();
        relations.add(new DefaultRelation(genericDeviceManager.m_genericDevicePath.add(Path.from(Path.SEPARATOR + genericDevice.DEVICE_SERIAL_NUMBER)), Action.UPDATE, "Update field",
                new DefaultParameter()
                        .name("parameter/value")
                        .description(" Modify the parameter of the device with the value")
                        .optional(false)
                        .type(Map.class)));


        relations.add(new DefaultRelation(genericDeviceManager.m_genericDevicePath.add(Path.from(Path.SEPARATOR + genericDevice.DEVICE_SERIAL_NUMBER)), Action.DELETE, "delete", null));
        setRelations(relations);
    }

    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("SerialNumber", m_genericDevice.DEVICE_SERIAL_NUMBER);
        metadataBuilder.set("StateActivated", m_genericDevice.STATE_ACTIVATED);
        metadataBuilder.set("StateDeactivated", m_genericDevice.STATE_DEACTIVATED);
        metadataBuilder.set("StatePropertyName", m_genericDevice.STATE_PROPERTY_NAME);
        metadataBuilder.set("StateUnknown", m_genericDevice.STATE_UNKNOWN);
        return metadataBuilder.build();
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        //Map<String, String> newMap = request.get("configuration", Map.class);
        Map<String, ?> newMap = request.parameters();
        if (newMap != null) {
            for (String key : newMap.keySet()) {
                if (key.contentEquals("STATE_DEACTIVATED")){
                    m_genericDevice.setSTATE_DEACTIVATED(newMap.get(key).toString());
                    Everest.postResource(ResourceEvent.UPDATED,this);
                }
                else if (key.contentEquals("zone")) {
                    m_genericDevice.zone = newMap.get(key).toString();
                    Relation relations = new DefaultRelation(ZoneManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + m_genericDevice.zone)), Action.READ, "Location");
                    setRelations(relations);

                    ZoneManager.getInstance().setChildLocation(m_genericDevice.DEVICE_SERIAL_NUMBER, newMap.get(key).toString());
                    Everest.postResource(ResourceEvent.UPDATED,this);
                } else if (key.contentEquals("State Unknown")) {
                    m_genericDevice.setSTATE_UNKNOWN(newMap.get(key).toString());
                    Everest.postResource(ResourceEvent.UPDATED, this);
                } else if (key.contentEquals("State Activated")) {
                    m_genericDevice.setSTATE_ACTIVATED(newMap.get(key).toString());
                    Everest.postResource(ResourceEvent.UPDATED,this);
                }
            }
        }
        return this;
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        m_genericDeviceManager.deleteresource(m_genericDevice.DEVICE_SERIAL_NUMBER);
        Everest.postResource(ResourceEvent.DELETED,this);
        return (m_genericDeviceManager);
    }
}
