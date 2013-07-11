package org.apache.felix.ipojo.everest.casa.zone;

import org.apache.felix.ipojo.everest.casa.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.Path;

import java.util.HashMap;
import java.util.Map;

import static org.apache.felix.ipojo.everest.casa.CasaRootResource.m_casaRootPath;

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
    private Map<String, ZoneManager> m_genericDeviceResourcesMap = new HashMap<String, ZoneManager>();

    /**
     * Static instance of this singleton class
     */
    private static final ZoneManager m_instance = new ZoneManager();

    public ZoneManager() {
        super(m_zonePath);
    }
}
