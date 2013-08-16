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

import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.ResourceMetadata;

import java.util.Map;

import static org.ow2.chameleon.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/23/13
 * Time: 3:46 PM
 */
public class EnvironmentPropertiesResource extends DefaultResource {

    public static final String ENV_NAME = "environment";

    private ResourceMetadata metadata;

    public EnvironmentPropertiesResource() {
        super(SYSTEM_ROOT_PATH.addElements(ENV_NAME));

        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            metadataBuilder.set(entry.getKey(), entry.getValue());
        }
        metadata = metadataBuilder.build();
    }


    @Override
    public ResourceMetadata getMetadata() {
        return metadata;
    }
}
