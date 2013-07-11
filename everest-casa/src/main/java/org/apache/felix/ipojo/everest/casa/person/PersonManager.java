package org.apache.felix.ipojo.everest.casa.person;

import org.apache.felix.ipojo.everest.casa.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.Path;

import java.util.HashMap;
import java.util.Map;

import static org.apache.felix.ipojo.everest.casa.CasaRootResource.m_casaRootPath;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 11/07/13
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class PersonManager extends AbstractResourceCollection {

    public static final String m_personName = "person";

    public static final Path m_personPath = m_casaRootPath.add(Path.from(Path.SEPARATOR + m_personName));


    /**
     * A MODIFFFF
     */
    private Map<String, PersonManager> m_genericDeviceResourcesMap = new HashMap<String, PersonManager>();

    /**
     * Static instance of this singleton class
     */
    private static final PersonManager m_instance = new PersonManager();

    public PersonManager() {
        super(m_personPath);
    }
}
