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

package org.ow2.chameleon.everest.system;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.ow2.chameleon.everest.impl.AbstractResourceManager;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/23/13
 * Time: 3:37 PM
 */
@Component
@Provides(specifications = Resource.class)
@Instantiate
public class SystemRootResource extends AbstractResourceManager {

    public static final String SYSTEM_ROOT = "system";
    public static final Path SYSTEM_ROOT_PATH = Path.from(Path.SEPARATOR + SYSTEM_ROOT);

    private static final String SYSTEM_DESCRIPTION = "system resources";
    private final List<Resource> systemResources = new ArrayList<Resource>();

    public SystemRootResource() {
        super(SYSTEM_ROOT, SYSTEM_DESCRIPTION);
        systemResources.add(new SystemPropertiesResource());
        systemResources.add(new EnvironmentPropertiesResource());
        systemResources.add(new MemoryResource());
        systemResources.add(new OperatingSystemResource());
        systemResources.add(new RuntimeResource());
        systemResources.add(new ThreadManagerResource());
    }

    @Override
    public List<Resource> getResources() {
        return systemResources;
    }
}
