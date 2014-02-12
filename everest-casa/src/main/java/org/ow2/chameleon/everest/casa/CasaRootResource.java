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

package org.ow2.chameleon.everest.casa;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.ow2.chameleon.everest.casa.device.GenericDeviceManager;
import org.ow2.chameleon.everest.casa.person.PersonManager;
import org.ow2.chameleon.everest.casa.zone.ZoneManager;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceMetadata;

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
public class CasaRootResource extends AbstractResourceCollection {

    /*
    * Name ressource
     */
    public static final String m_casaRoot = "casa";

    /*
     * Path of the ressource
    */
    public static final Path m_casaRootPath = Path.from(Path.SEPARATOR + m_casaRoot);

    /*
     * Description of the ressource
    */
    private static final String m_casaDescription = "casa resources";

    /*
     * List of the subRessource
    */
    private final List<Resource<?>> m_casaResources = new ArrayList<Resource<?>>();

    /*
     * Constructor
    */
    public CasaRootResource() {
        super(m_casaRootPath);
        m_casaResources.add(GenericDeviceManager.getInstance());
        m_casaResources.add(PersonManager.getInstance());
        m_casaResources.add(ZoneManager.getInstance());
    }


    @Override
    public List<Resource<?>> getResources() {
        return m_casaResources;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Name", m_casaRoot);
        metadataBuilder.set("Path", m_casaRootPath);
        return metadataBuilder.build();
    }
}
