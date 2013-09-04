package org.ow2.chameleon.everest.query.casaTest.person;

import org.ow2.chameleon.everest.query.casaTest.AbstractResourceCollection;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.ResourceMetadata;

import java.util.HashMap;
import java.util.Map;

import static org.ow2.chameleon.everest.query.casaTest.TestRootResource.m_casaRootPath;

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


    public static PersonManager getInstance() {
        return m_instance;
    }


    public PersonManager() {
        super(m_personPath);
    }

    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Name", m_personName);
        metadataBuilder.set("Path", m_personPath);
        return metadataBuilder.build();
    }
}
