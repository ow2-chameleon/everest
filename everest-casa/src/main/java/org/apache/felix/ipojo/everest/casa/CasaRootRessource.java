package org.apache.felix.ipojo.everest.casa;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.everest.casa.device.GenericDeviceManager;
import org.apache.felix.ipojo.everest.impl.AbstractResourceManager;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 09/07/13
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
@Component(name = "CasaRootRessource")
@Provides(specifications = Resource.class)
@Instantiate
public class CasaRootRessource extends AbstractResourceManager {

    public static final String m_casaRoot = "casa";
    public static final Path m_casaRootPath = Path.from(Path.SEPARATOR + m_casaRoot);
    private static final String m_casaDescription = "casa resources";
    ;
    private final List<Resource> m_casaResources = new ArrayList<Resource>();

    public CasaRootRessource() {
        super(m_casaRoot, m_casaDescription);
        m_casaResources.add(new GenericDeviceManager());
    }

    @Override
    public List<Resource> getResources() {
        return m_casaResources;
    }
}
