package org.ow2.chameleon.everest.query.casaTest.zone;

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.query.casaTest.AbstractResourceCollection;
import org.ow2.chameleon.everest.query.casaTest.device.GenericDeviceManager;
import org.ow2.chameleon.everest.query.casaTest.services.Zone;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.util.ArrayList;
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
        List<Relation> relations = new ArrayList<Relation>();
        relations.add(new DefaultRelation(m_ZoneManager.m_zonePath.add(Path.from(Path.SEPARATOR + m_zone.getName())), Action.DELETE, "delete", null));
        setRelations(relations);
    }

    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Name", m_zone.getName());
        metadataBuilder.set("Luminosity", m_zone.getM_luminosity());
        metadataBuilder.set("Surface", m_zone.getM_Surface());
        metadataBuilder.set("Temperature", m_zone.getM_Temperature());
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

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        m_ZoneManager.deleteresource(m_zone.getName());
        Everest.postResource(ResourceEvent.DELETED, this);
        return (m_ZoneManager);
    }

}
