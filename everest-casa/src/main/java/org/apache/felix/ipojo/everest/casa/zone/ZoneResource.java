package org.apache.felix.ipojo.everest.casa.zone;

import org.apache.felix.ipojo.everest.casa.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.casa.device.GenericDeviceManager;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

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
