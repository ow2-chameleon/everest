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

package org.ow2.chameleon.everest.casa.person;

import org.ow2.chameleon.everest.casa.AbstractResourceCollection;
import org.ow2.chameleon.everest.services.Path;

import java.util.HashMap;
import java.util.Map;

import static org.ow2.chameleon.everest.casa.CasaRootResource.m_casaRootPath;

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
}
